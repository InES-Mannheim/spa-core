/*******************************************************************************
 *    Copyright 2016 University of Mannheim
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.unima.core.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.hashids.Hashids;

import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Repository;
import de.unima.core.domain.model.Schema;
import de.unima.core.storage.Store;

/**
 * PersistenceService which stores entities locally on disk or in memory.
 */
public class PersistenceService {

	private final static String REPOSITORY_URI = "http://www.uni-mannheim.de/spa/Repository/single";
	
	private final Random rand;
	private final RepositoryRepository repositoryRepository;
	private final SchemaRepository schemaRepository;
	private final ProjectRepository projectRepository;
	private final DataPoolRepository dataPoolRepository;
	private final DataBucketRepository dataBucketRepository;
	
	public PersistenceService(Store store) {
		this.repositoryRepository = new RepositoryRepository(store);
		this.schemaRepository = new SchemaRepository(store);
		this.projectRepository = new ProjectRepository(store);
		this.dataPoolRepository = new DataPoolRepository(store);
		this.dataBucketRepository = new DataBucketRepository(store);
		this.rand = new Random();
	}

	/**
	 * Creates a new {@link Project} with generated URI.
	 * 
	 * <p>
	 * The project and changes to the repository are persisted.
	 * 
	 * @param label
	 *            of the new project
	 * 
	 * @return new {@code Project} with generated id
	 */
	public Project createPersistentProjectWithGeneratedId(String label) {
		final Repository repository = findOrCreateSingleRepository();
		final Project project = new Project(createId(Vocabulary.Project), label, repository);
		repository.addProject(project);
		repositoryRepository.save(repository);
		projectRepository.save(project);
		return project;
	}
	
	/**
	 * Finds all projects.
	 * 
	 * <p>
	 * {@code DataPool}s and linked {@code Schema}s of each project are not
	 * loaded. To fully load a project use
	 * {@link PersistenceService#findProjectById(String)};
	 * 
	 * @return list of persisted projects
	 */
	public List<Project> findAllProjects() {
		return projectRepository.findAll();
	}
	
	/**
	 * Finds project with given id.
	 * 
	 * <p>
	 * All {@code DataPool}s and linked {@code Schema}s of the project are
	 * loaded.
	 * 
	 * @param id
	 *            as URI; for example: http://www.test.com/1
	 * @return project if found; empty otherwise
	 */
	public Optional<Project> findProjectById(String id) {
		final Optional<Project> foundProject = projectRepository.findById(id);
		loadAndAddDataPoolsIfPresent(foundProject);
		loadAndAddSchemassIfPresent(foundProject);
		return foundProject;
	}

	private void loadAndAddDataPoolsIfPresent(final Optional<Project> foundProject) {
		if(!foundProject.isPresent()){
			return;
		}
		final List<DataPool> loadedDataPools = foundProject.get().getDataPools()
			.stream()
			.map(pool -> findDataPoolById(pool.getId()))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(pool -> {
				pool.setProject(foundProject.get());
				return pool;	
			})
			.collect(Collectors.toList());
		foundProject.get().replaceDataPools(loadedDataPools);
	}
	
	private void loadAndAddSchemassIfPresent(Optional<Project> foundProject) {
		if(!foundProject.isPresent()){
			return;
		}
		final List<Schema> loadedSchemas = foundProject.get().getLinkedSchemas()
			.stream()
			.map(schema -> schemaRepository.findById(schema.getId()))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
		foundProject.get().replaceLinkedSchemas(loadedSchemas);
	}
	
	/**
	 * Saves given project.
	 * 
	 * <p>
	 * This action also saves all contained {@code DataPool}s.
	 * 
	 * @param project
	 *            which should be saved
	 * @return id of the project
	 * @throws IllegalStateException
	 *             if project could not be saved
	 */
	public String saveProject(Project project) {
		project.getDataPools().forEach(this::saveDataPool);
		return projectRepository.save(project).orElseThrow(() -> new IllegalStateException("Could not save project."));
	}
	
	/**
	 * Deletes given project.
	 * 
	 * <p>
	 * All schemas linked to this project are unlinked. Further, all contained
	 * data pools and buckets are removed.
	 * 
	 * @param project
	 *            which should be deleted
	 * @return number of deleted statements
	 */
	public long deleteProject(Project project) {
		final long totalNumberOfDeletedStatements = deleteDataBuckets(project) + deleteDataPools(project)
				+ deleteProjectAndSchemaLinks(project);
		removeDataPoolsAndSchemasFromProjectEntity(project);
		removeProjectFromRepository(project);
		return totalNumberOfDeletedStatements;
	}
	
	private long deleteDataBuckets(Project project) {
		final List<DataBucket> affectedDataBuckets = project.getDataPools().stream().flatMap(pool -> pool.getDataBuckets().stream()).collect(Collectors.toList());
		return dataBucketRepository.deleteAll(affectedDataBuckets);
	}
	
	private long deleteDataPools(Project project) {
		return dataPoolRepository.deleteAll(project.getDataPools());
	}
	
	private long deleteProjectAndSchemaLinks(Project project) {
		return projectRepository.delete(project);
	}

	private void removeDataPoolsAndSchemasFromProjectEntity(Project project) {
		project.removeAllDataPools();
		project.unlinkAllSchemas();
	}
	
	private void removeProjectFromRepository(Project project) {
		final Repository repository = findOrCreateSingleRepository();
		repository.removeProject(project.getId());
		project.getRepository().removeProject(project.getId());
		repositoryRepository.save(repository);
	}

	/**
	 * Adds given schema data as new schema to the given repository and returns
	 * a generated schema Id.
	 * 
	 * <p>
	 * Changes made to the repository and the created schema are persisted.
	 * 
	 * @param label
	 *            of the new schema
	 * @param data
	 *            containing RDF
	 * @return created schema
	 * @throws IllegalStateException
	 *             if schema data could not be stored
	 */
	public Schema addDataAsNewSchema(String label, Model data) {
		final Schema schema = new Schema(createId(Vocabulary.Schema), label);
		final Repository repository = findOrCreateSingleRepository();
		schemaRepository.save(schema);
		schemaRepository.addDataToEntity(schema, data).orElseThrow(() -> new IllegalStateException("Could not add data to new schema."));
		repository.addSchema(schema);
		repositoryRepository.save(repository);
		return schema;
	}

	private Repository findOrCreateSingleRepository() {
		return repositoryRepository.findById(REPOSITORY_URI).orElseGet(() -> new Repository(REPOSITORY_URI));
	}
	
	/**
	 * Replaces data of given schema with given data.
	 * 
	 * @param schema
	 *            which data should be replaced
	 * @param data
	 *            containing RDF
	 * @return saved schema
	 * @throws IllegalStateException
	 *             if the schema data could not be stored
	 */
	public Schema replaceDataOfSchema(Schema schema, Model data) {
		final Repository repository = findOrCreateSingleRepository();
		schemaRepository.save(schema);
		schemaRepository.addDataToEntity(schema, data).orElseThrow(() -> new IllegalStateException("Could not replace schema data."));
		if (!repository.findSchemaById(schema.getId()).isPresent()) {
			repository.addSchema(schema);
			repositoryRepository.save(repository);
		}
		return schema;
	}

	/**
	 * Unlinks given schema from all affected projects and deletes the content.
	 * 
	 * <p>
	 * <b>Note:</b> Affected projects need to be reloaded.
	 * 
	 * @param schema
	 *            which should be removed
	 * @return number of statements which have been deleted
	 */
	public long deleteSchema(Schema schema) {
		final Repository repository = findOrCreateSingleRepository();
		projectRepository.saveAll(findAndUnlinkSchemaFromProjects(repository, schema));
		repository.removeSchema(schema.getId());
		repositoryRepository.save(repository);
		return schemaRepository.delete(schema);
	}

	private List<Project> findAndUnlinkSchemaFromProjects(Repository repository, Schema schema) {
		return repository.getProjects().stream()
				.filter(project -> project.isSchemaLinked(schema.getId())).map(project -> {
					project.unlinkSchema(schema.getId());
					return project;
				}).collect(Collectors.toList());
	}
	
	/**
	 * Finds all {@code Schema}s.
	 * 
	 * @return list of persisted {@code Schema}s
	 */
	public List<Schema> findAllSchemas() {
		return schemaRepository.findAll();
	}
	
	/**
	 * Finds {@code Schema} by id.
	 * 
	 * The id must be an URI (e.g. http://www.test.com/1)
	 * 
	 * @param id
	 *            of the Schema as URI
	 * @return found {@code Schema}; empty otherwise
	 */
	public Optional<Schema> findSchemaById(String id) {
		return schemaRepository.findById(id);
	}
	
	/**
	 * Finds data stored for given {@code Schema}.
	 * 
	 * @param schema
	 *            which data should be returned
	 * @return the data if present; empty otherwise
	 */
	public Optional<Model> findDataOfSchema(Schema schema) {
		return schemaRepository.findDataOfEntity(schema);
	}
	
	/**
	 * Creates a {@link DataPool} with generated Id and adds it to the given
	 * project.
	 * 
	 * <p>
	 * <b>Note:</b> The changes to the project and the new {@code DataPool} are
	 * persisted.
	 * 
	 * @param project
	 *            to add the created pool
	 * @param label
	 *            of the new pool
	 * @return new {@link DataPool}
	 */
	public DataPool createPeristentDataPoolForProjectWithGeneratedId(Project project, String label) {
		final DataPool datapool = new DataPool(createId(Vocabulary.DataPool), label, project);
		project.addDataPool(datapool);
		dataPoolRepository.save(datapool);
		projectRepository.save(project);
		return datapool;
	}
	
	/**
	 * Saves given {@code DataPool}.
	 * 
	 * @param dataPool
	 *            which should be saved
	 * @return id of the pool
	 */
	public String saveDataPool(DataPool dataPool) {
		return dataPoolRepository.save(dataPool).orElseThrow(() -> new IllegalStateException("Could not save data pool."));
	}
	
	/**
	 * Finds all {@code DataPool}s.
	 * 
	 * <p>
	 * <b>Note:</b> The labels of the contained {@code DataBucket}s are not
	 * loaded.
	 * 
	 * @return list of persistent {@code DataPool}s
	 */
	public List<DataPool> findAllDataPools() {
		return dataPoolRepository.findAll();
	}

	/**
	 * Finds {@code DataPool} by id and all contained data buckets.
	 *
	 * <p>
	 * <b>Note:</b> Each found data pool refers to the project it belongs to.
	 * Thus, {@code DataPool#getProject()} is not null. However, the project is
	 * not fully loaded and should not be saved. To load the project, see
	 * {@link PersistenceService#findProjectById(String)}.
	 * 
	 * @param id
	 *            of the pool
	 * @return found {@code DataPool}; empty otherwise
	 */
	public Optional<DataPool> findDataPoolById(String id) {
		final Optional<DataPool> foundDataPool = dataPoolRepository.findById(id);
		loadAndAddDataBucketsIfPresent(foundDataPool);
		return foundDataPool;
	}

	private void loadAndAddDataBucketsIfPresent(final Optional<DataPool> foundDataPool) {
		if(!foundDataPool.isPresent()){
			return;
		}
		final List<DataBucket> loadedDataBuckets = foundDataPool.get().getDataBuckets()
			.stream()
			.map(bucket -> dataBucketRepository.findById(bucket.getId()))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
		foundDataPool.get().replaceDataBuckets(loadedDataBuckets);
	}
	
	/**
	 * Deletes given {@code DataPool}. This includes, the deletion of all
	 * contained {@code DataBucket}s.
	 * 
	 * @param dataPool
	 *            which should be deleted
	 */
	public void deleteDataPool(DataPool dataPool) {
		final Project project = dataPool.getProject();
		project.removeDataPoolById(dataPool.getId());
		projectRepository.save(project);
		dataBucketRepository.deleteAll(dataPool.getDataBuckets());
		dataPoolRepository.delete(dataPool);
	}
	
	private String createId(String uri) {
		final Hashids hashIds = new Hashids(uri);
		return appendSlashIfUriHasNoHashOrSlashEnding(uri) + hashIds.encode(Math.abs(rand.nextInt()));
	}
	
	private String appendSlashIfUriHasNoHashOrSlashEnding(String uri) {
		final boolean hasSlashOrHash = uri.endsWith("#") || uri.endsWith("/");
		return hasSlashOrHash ? uri : uri + "/";
	}
	
	/**
	 * Adds given data as new {@code DataBucket} to the given {@code DataPool}
	 * and returns a generated Id.
	 * 
	 * <p>
	 * <b>Note:</b> Changes made to given {@code DataPool} are persisted.
	 * Further, the created {@code DataBucket} is also persisted.
	 * 
	 * @param dataPool
	 *            of the new {@code DataBucket}
	 * 
	 * @param label
	 *            of the new {@code DataBucket}
	 * @param data
	 *            containing RDF
	 * @return created {@code DataBucket}
	 * @throws IllegalStateException
	 *             if the data could not be stored
	 */
	public DataBucket addDataAsNewDataBucketToDataPool(DataPool dataPool, String label, Model data) {
		final DataBucket bucket = new DataBucket(createId(Vocabulary.DataBucket), label);
		dataBucketRepository.save(bucket);
		dataBucketRepository.addDataToEntity(bucket, data).orElseThrow(() -> new IllegalStateException("Could not add data as new data bucket."));
		dataPool.addDataBucket(bucket);
		dataPoolRepository.save(dataPool);
		return bucket;
	}
	
	/**
	 * Replaces data of given {@code DataBucket}t with given data.
	 * 
	 * @param bucket
	 *            which data should be replaced
	 * @param data
	 *            containing RDF
	 * @return saved bucket
	 * @throws IllegalStateException
	 *             if the bucket data could not be stored
	 */
	public DataBucket replaceDataBucketWithData(DataBucket bucket, Model data) {
		dataBucketRepository.save(bucket);
		dataBucketRepository.addDataToEntity(bucket, data).orElseThrow(() -> new IllegalStateException("Could not replace data bucket."));
		return bucket;
	}

	/**
	 * Removes given {@link DataBucket}.
	 * 
	 * @param dataPool
	 *            of the {@code DataBucket}
	 * 
	 * @param dataBucket
	 *            which should be removed
	 * @return number of statements which have been deleted
	 */
	public long removeDataBucketFromDataPool(DataPool dataPool, DataBucket dataBucket) {
		dataPool.removeDataBucketById(dataBucket.getId());
		dataPoolRepository.save(dataPool);
		return dataBucketRepository.delete(dataBucket);
	}
	
	/**
	 * Finds data stored for given {@code DataBucket}.
	 * 
	 * @param bucket
	 *            which data should be returned
	 * @return the data if present; empty otherwise
	 */
	public Optional<Model> findDataOfDataBucket(DataBucket bucket) {
		return dataBucketRepository.findDataOfEntity(bucket);
	}
}
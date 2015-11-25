package de.unima.core.domain.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.hashids.Hashids;

import de.unima.core.domain.DataBucket;
import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.domain.Vocabulary;
import de.unima.core.domain.repository.DataBucketRepository;
import de.unima.core.domain.repository.DataPoolRepository;
import de.unima.core.domain.repository.ProjectRepository;
import de.unima.core.domain.repository.RepositoryRepository;
import de.unima.core.domain.repository.SchemaRepository;
import de.unima.core.storage.Store;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

public class RepositoryService {

	private final static String REPOSITORY_URI = "http://www.uni-mannheim.de/spa/Repository/single";
	
	private final Repository repository;
	private final Random rand;
	private final RepositoryRepository repositoryRepository;
	private final SchemaRepository schemaRepository;
	private final ProjectRepository projectRepository;
	private final DataPoolRepository dataPoolRepository;
	private final DataBucketRepository dataBucketRepository;
	
	
	private RepositoryService(Store store) {
		this.repository = new Repository(REPOSITORY_URI);
		this.rand = new Random();
		this.repositoryRepository = new RepositoryRepository(store);
		this.schemaRepository = new SchemaRepository(store);
		this.projectRepository = new ProjectRepository(store);
		this.dataPoolRepository = new DataPoolRepository(store);
		this.dataBucketRepository = new DataBucketRepository(store);
	}
	
	public static RepositoryService withDataInSharedMemory(){
		return new RepositoryService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
	}
	
	public static RepositoryService withDataInUniqueMemory(){
		return new RepositoryService(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	public static RepositoryService withDataInFolder(Path pathToFolder){
		return new RepositoryService(JenaTDBStore.withFolder(pathToFolder));
	}

	/**
	 * Creates a new {@link Project} with generated URI.
	 * 
	 * The project and changes to the repository are persisted and don't need to
	 * be saved explicitly after creation.
	 * 
	 * @param label
	 *            of the new project
	 * 
	 * @return new {@code Project} with generated id
	 */
	public Project createProjectWithGeneratedId(String label) {
		final Project project = new Project(createId(Vocabulary.Project), repository, label);
		repository.addProject(project);
		repositoryRepository.save(repository);
		projectRepository.save(project);
		return project;
	}
	
	/**
	 * Saves given project.
	 * 
	 * @param project which should be saved
	 * @return id of the project
	 * @throws IllegalStateException if project could not be saved
	 */
	public String saveProject(Project project){
		return projectRepository.save(project).orElseThrow(() -> new IllegalStateException("Could not save project."));
	}
	
	/**
	 * Deletes given project.
	 * 
	 * All schemas linked to this project are unlinked. Further,
	 * all contained data pools and buckets are removed.
	 * 
	 * @param project which should be deleted
	 * @return number of deleted statements
	 */
	public long deleteProject(Project project){
		final long totalNumberOfDeletedStatements = deleteDataBuckets(project) +
		deleteDataPools(project) +
		deleteProjectAndSchemaLinks(project);
		removeDataPoolsAndSchemasFromProjectEntity(project);
		repository.removeProject(project.getId());
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

	/**
	 * Adds given schema data as new schema to the given repository and returns
	 * a generated schema Id.
	 * 
	 * The label is attached to the newly created schema.
	 * @param label of the new schema
	 * @param data containing RDF
	 * @return created schema
	 * @throws IllegalStateException if schema data could not be stored
	 */
	public Schema addDataAsNewSchema(String label, Model data){
		final Schema schema = new Schema(createId(Vocabulary.Schema), label);
		schemaRepository.save(schema);
		schemaRepository.addDataToEntity(schema, data).orElseThrow(() -> new IllegalStateException("Could not add data to new schema."));
		repository.addSchema(schema);
		repositoryRepository.save(repository);
		return schema;
	}
	
	/**
	 * Replaces data of given schema with given data.
	 * 
	 * @param schema which data should be replaced
	 * @param data containing RDF
	 * @return saved schema
	 * @throws IllegalStateException if the schema data could not be stored
	 */
	public Schema replaceDataOfSchema(Schema schema, Model data){
		schemaRepository.save(schema);
		schemaRepository.addDataToEntity(schema, data).orElseThrow(() -> new IllegalStateException("Could not replace schema data."));
		if(!repository.findSchemaById(schema.getId()).isPresent()){
			repository.addSchema(schema);
			repositoryRepository.save(repository);
		}
		return schema;
	}

	/**
	 * Unlinks given schema from all affected projects and deletes the
	 * content.
	 * 
	 * @param schema which should be removed
	 * @return number of statements which have been deleted
	 */
	public long deleteSchema(Schema schema){
		unlinkSchemaFromProjects(repository, schema);
		repository.removeSchema(schema.getId());
		return schemaRepository.delete(schema);
	}

	private void unlinkSchemaFromProjects(Repository repository, Schema schema) {
		final List<Project> affected = repository.getProjects().stream().filter(project -> project.isSchemaLinked(schema.getId())).map(project -> {
			project.unlinkSchema(schema.getId());
			return project;
		}).collect(Collectors.toList());
		projectRepository.saveAll(affected);
	}
	
	/**
	 * Finds data stored for given schema.
	 * 
	 * @param schema which data should be returned
	 * @return the data if present; empty otherwise
	 */
	public Optional<Model> findDataForSchema(Schema schema){
		return schemaRepository.findDataOfEntity(schema);
	}
	
	/**
	 * Creates a new {@link DataPool} with generated Id and adds it to the given project.
	 * 
	 * @param project where to add the created pool
	 * @param label of the new pool
	 * @return new {@link DataPool}
	 */
	public DataPool createNewDataPoolForProjectWithGeneratedId(Project project, String label){
		final DataPool datapool = new DataPool(createId(Vocabulary.DataPool), label, project);
		project.addDataPool(datapool);
		return datapool;
	}
	
	/**
	 * Saves given {@code DataPool}.
	 * 
	 * @param dataPool which should be saved
	 * @return id of the pool
	 */
	public String saveDataPool(DataPool dataPool){
		return dataPoolRepository.save(dataPool).orElseThrow(() -> new IllegalStateException("Could not save data pool."));
	}
	
	/**
	 * Deletes given {@code DataPool}. This includes, the deletion
	 * of all contained {@code DataBucket}s.
	 * 
	 * @param dataPool which should be deleted
	 */
	public void deleteDataPool(DataPool dataPool){
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
	 * Adds given data as new {@code DataBucket} to the given {@code DataPool} and returns
	 * a generated Id.
	 * 
	 * @param label of the new data bucket
	 * @param data containing RDF
	 * @return created data bucket
	 * @throws IllegalStateException if the data could not be stored
	 */
	public DataBucket addDataAsNewDataBucketToDataPool(DataPool dataPool, String label, Model data){
		final DataBucket bucket = new DataBucket(createId(Vocabulary.DataBucket), label);
		dataBucketRepository.save(bucket);
		dataBucketRepository.addDataToEntity(bucket, data).orElseThrow(() -> new IllegalStateException("Could not add data as new data bucket."));
		dataPool.addDataBucket(bucket);
		dataPoolRepository.save(dataPool);
		return bucket;
	}
	
	/**
	 * Replaces data of given data bucket with given data.
	 * 
	 * @param bucket which data should be replaced
	 * @param data containing RDF
	 * @return saved bucket
	 * @throws IllegalStateException if the bucket data could not be stored
	 */
	public DataBucket replaceDataBucketWithData(DataBucket bucket, Model data){
		dataBucketRepository.save(bucket);
		dataBucketRepository.addDataToEntity(bucket, data).orElseThrow(() -> new IllegalStateException("Could not replace data bucket."));
		return bucket;
	}

	/**
	 * Deletes given data bucket.
	 * 
	 * @param dataBucket which should be removed
	 * @return number of statements which have been deleted
	 */
	public long deleteDataBucketFromDataPool(DataPool dataPool, DataBucket dataBucket){
		dataPool.removeDataBucketById(dataBucket.getId());
		dataPoolRepository.save(dataPool);
		return dataBucketRepository.delete(dataBucket);
	}

	/**
	 * Finds data stored for given {@code DataBucket}.
	 * 
	 * @param bucket which data should be returned
	 * @return the data if present; empty otherwise
	 */
	public Optional<Model> findDataOfDataBucket(DataBucket bucket){
		return dataBucketRepository.findDataOfEntity(bucket);
	}
}

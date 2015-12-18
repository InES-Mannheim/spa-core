package de.unima.core.persistence.local;

import java.nio.file.Path;
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
import de.unima.core.domain.service.PersistenceService;
import de.unima.core.storage.Store;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

/**
 * Implementation of the {@code PersistenceService} which stores entities
 * locally on disk or in memory.
 */
public class LocalPersistenceService implements PersistenceService {

	private final static String REPOSITORY_URI = "http://www.uni-mannheim.de/spa/Repository/single";
	
	private final Random rand;
	private final RepositoryRepository repositoryRepository;
	private final SchemaRepository schemaRepository;
	private final ProjectRepository projectRepository;
	private final DataPoolRepository dataPoolRepository;
	private final DataBucketRepository dataBucketRepository;
	
	private LocalPersistenceService(Store store) {
		this.repositoryRepository = new RepositoryRepository(store);
		this.schemaRepository = new SchemaRepository(store);
		this.projectRepository = new ProjectRepository(store);
		this.dataPoolRepository = new DataPoolRepository(store);
		this.dataBucketRepository = new DataBucketRepository(store);
		this.rand = new Random();
	}
	
	public static LocalPersistenceService withDataInSharedMemory(){
		return new LocalPersistenceService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
	}
	
	public static LocalPersistenceService withDataInUniqueMemory(){
		return new LocalPersistenceService(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	public static LocalPersistenceService withDataInFolder(Path pathToFolder){
		return new LocalPersistenceService(JenaTDBStore.withFolder(pathToFolder));
	}

	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#createPersistentProjectWithGeneratedId(java.lang.String)
	 */
	@Override
	public Project createPersistentProjectWithGeneratedId(String label) {
		final Repository repository = findOrCreateSingleRepository();
		final Project project = new Project(createId(Vocabulary.Project), label, repository);
		repository.addProject(project);
		repositoryRepository.save(repository);
		projectRepository.save(project);
		return project;
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findAllProjects()
	 */
	@Override
	public List<Project> findAllProjects(){
		return projectRepository.findAll();
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findProjectById(java.lang.String)
	 */
	@Override
	public Optional<Project> findProjectById(String id){
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
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#saveProject(de.unima.core.domain.model.Project)
	 */
	@Override
	public String saveProject(Project project){
		project.getDataPools().forEach(this::saveDataPool);
		return projectRepository.save(project).orElseThrow(() -> new IllegalStateException("Could not save project."));
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#deleteProject(de.unima.core.domain.model.Project)
	 */
	@Override
	public long deleteProject(Project project){
		final long totalNumberOfDeletedStatements = deleteDataBuckets(project) +
		deleteDataPools(project) +
		deleteProjectAndSchemaLinks(project);
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

	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#addDataAsNewSchema(java.lang.String, org.apache.jena.rdf.model.Model)
	 */
	@Override
	public Schema addDataAsNewSchema(String label, Model data){
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
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#replaceDataOfSchema(de.unima.core.domain.model.Schema, org.apache.jena.rdf.model.Model)
	 */
	@Override
	public Schema replaceDataOfSchema(Schema schema, Model data){
		final Repository repository = findOrCreateSingleRepository();
		schemaRepository.save(schema);
		schemaRepository.addDataToEntity(schema, data).orElseThrow(() -> new IllegalStateException("Could not replace schema data."));
		if(!repository.findSchemaById(schema.getId()).isPresent()){
			repository.addSchema(schema);
			repositoryRepository.save(repository);
		}
		return schema;
	}

	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#deleteSchema(de.unima.core.domain.model.Schema)
	 */
	@Override
	public long deleteSchema(Schema schema){
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
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findAllSchemas()
	 */
	@Override
	public List<Schema> findAllSchemas(){
		return schemaRepository.findAll();
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findSchemaById(java.lang.String)
	 */
	@Override
	public Optional<Schema> findSchemaById(String id){
		return schemaRepository.findById(id);
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findDataOfSchema(de.unima.core.domain.model.Schema)
	 */
	@Override
	public Optional<Model> findDataOfSchema(Schema schema){
		return schemaRepository.findDataOfEntity(schema);
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#createPeristentDataPoolForProjectWithGeneratedId(de.unima.core.domain.model.Project, java.lang.String)
	 */
	@Override
	public DataPool createPeristentDataPoolForProjectWithGeneratedId(Project project, String label){
		final DataPool datapool = new DataPool(createId(Vocabulary.DataPool), label, project);
		project.addDataPool(datapool);
		dataPoolRepository.save(datapool);
		projectRepository.save(project);
		return datapool;
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#saveDataPool(de.unima.core.domain.model.DataPool)
	 */
	@Override
	public String saveDataPool(DataPool dataPool){
		return dataPoolRepository.save(dataPool).orElseThrow(() -> new IllegalStateException("Could not save data pool."));
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findAllDataPools()
	 */
	@Override
	public List<DataPool> findAllDataPools(){
		return dataPoolRepository.findAll();
	}

	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findDataPoolById(java.lang.String)
	 */
	@Override
	public Optional<DataPool> findDataPoolById(String id){
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
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#deleteDataPool(de.unima.core.domain.model.DataPool)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#addDataAsNewDataBucketToDataPool(de.unima.core.domain.model.DataPool, java.lang.String, org.apache.jena.rdf.model.Model)
	 */
	@Override
	public DataBucket addDataAsNewDataBucketToDataPool(DataPool dataPool, String label, Model data){
		final DataBucket bucket = new DataBucket(createId(Vocabulary.DataBucket), label);
		dataBucketRepository.save(bucket);
		dataBucketRepository.addDataToEntity(bucket, data).orElseThrow(() -> new IllegalStateException("Could not add data as new data bucket."));
		dataPool.addDataBucket(bucket);
		dataPoolRepository.save(dataPool);
		return bucket;
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#replaceDataBucketWithData(de.unima.core.domain.model.DataBucket, org.apache.jena.rdf.model.Model)
	 */
	@Override
	public DataBucket replaceDataBucketWithData(DataBucket bucket, Model data){
		dataBucketRepository.save(bucket);
		dataBucketRepository.addDataToEntity(bucket, data).orElseThrow(() -> new IllegalStateException("Could not replace data bucket."));
		return bucket;
	}

	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#removeDataBucketFromDataPool(de.unima.core.domain.model.DataPool, de.unima.core.domain.model.DataBucket)
	 */
	@Override
	public long removeDataBucketFromDataPool(DataPool dataPool, DataBucket dataBucket){
		dataPool.removeDataBucketById(dataBucket.getId());
		dataPoolRepository.save(dataPool);
		return dataBucketRepository.delete(dataBucket);
	}
	
	/* (non-Javadoc)
	 * @see de.unima.core.persistence.local.RepoService#findDataOfDataBucket(de.unima.core.domain.model.DataBucket)
	 */
	@Override
	public Optional<Model> findDataOfDataBucket(DataBucket bucket){
		return dataBucketRepository.findDataOfEntity(bucket);
	}
}

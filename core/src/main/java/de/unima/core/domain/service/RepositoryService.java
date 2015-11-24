package de.unima.core.domain.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.hashids.Hashids;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.domain.Vocabulary;
import de.unima.core.domain.repository.ProjectRepository;
import de.unima.core.domain.repository.RepositoryRepository;
import de.unima.core.domain.repository.SchemaRepository;
import de.unima.core.storage.Store;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

public class RepositoryService {

	private final static String REPOSITORY_URI = "http://www.uni-mannheim.de/spa/Repository/single";
	
	private final Repository singleRepository;
	private final Random rand;
	private final RepositoryRepository repositoryRepository;
	private final SchemaRepository schemaRepository;
	private final ProjectRepository projectRepository;
	
	
	private RepositoryService(Store store) {
		this.singleRepository = new Repository(REPOSITORY_URI);
		this.rand = new Random();
		this.repositoryRepository = new RepositoryRepository(store);
		this.schemaRepository = new SchemaRepository(store);
		this.projectRepository = new ProjectRepository(store);
	}
	
	public static RepositoryService withDataInMemory(){
		return new RepositoryService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
	}
	
	public static RepositoryService withDataInFolder(Path pathToFolder){
		return new RepositoryService(JenaTDBStore.withFolder(pathToFolder));
	}

	/**
	 * Creates a new {@link Repository} with a generated URI.
	 * 
	 * The repository is <b>not</b> persisted and must be saved explicitly after
	 * creation.
	 * 
	 * @return new {@code Repository} with generated id
	 */
	public Repository createRepositoryWithGeneratedId() {
		return new Repository(createId(Vocabulary.Repository));
	}
	
	/**
	 * Creates a new {@link Project} with generated URI for given
	 * {@link Repository} and label.
	 * 
	 * The repository will be updated with given project. The project and
	 * changes to the repository are persisted and don't need to be saved
	 * explicitly after creation.
	 * 
	 * @param repository
	 *            where the new project should be added to
	 * @param label
	 *            of the new project
	 * @return new {@code Project} with generated id
	 */
	public Project createProjectWithGeneratedIdForRepository(Repository repository, String label) {
		final Project project = new Project(createId(Vocabulary.Project), repository, label);
		repository.addProject(project);
		repositoryRepository.save(repository);
		projectRepository.save(project);
		return project;
	}

	/**
	 * Saves given repository.
	 * 
	 * This action is cascading: Saving an repository also saves
	 * all associated schemas and projects.
	 * 
	 * @param repository which should be saved
	 * @return id of the repository; may be empty
	 * @throws NullPointerException if repository is null
	 * @throws IllegalArgumentException if id is not set
	 */
	public Optional<String> saveRepository(Repository repository) {
		schemaRepository.saveAll(repository.getSchemas());
		projectRepository.saveAll(repository.getProjects());
		return repositoryRepository.save(repository);
	}
	
	/**
	 * Deletes given repository.
	 * 
	 * This action is cascading: Deleting an repository also deletes
	 * all associated schemas and projects.
	 * 
	 * @param repository which should be deleted
	 * @return number of deleted statements
	 * @throws NullPointerException if repository is null
	 * @throws IllegalArgumentException if id is not set
	 */
	public Long deleteRepository(Repository repository){
		schemaRepository.deleteAll(repository.getSchemas());
		projectRepository.deleteAll(repository.getProjects());
		return repositoryRepository.delete(repository);
	}

	/**
	 * Adds given schema data as new schema to the given repository and returns
	 * a generated schema Id.
	 * 
	 * The label is attached to the newly created schema.
	 * 
	 * @param repository where the new schema should be attached
	 * @param label of the new schema
	 * @param data containing RDF
	 * @return created schema
	 * @throws IllegalStateException if schema data could not be stored
	 */
	public Schema addNewSchemaDataToRepository(Repository repository, String label, Model data){
		final Schema schema = new Schema(createId(Vocabulary.Schema), label);
		schemaRepository.save(schema);
		schemaRepository.addDataToSchema(schema, data).orElseThrow(() -> new IllegalStateException("Could not add data to new schema."));
		repository.addSchema(schema);
		repositoryRepository.save(repository);
		return schema;
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
	 * Replaces data of given schema with given data.
	 * 
	 * @param repository of the schema
	 * @param schema which data should be replaced
	 * @param data containing RDF
	 * @return saved schema
	 * @throws IllegalStateException if the schema data could not be stored
	 */
	public Schema replaceDataOfSchemaInRepository(Repository repository, Schema schema, Model data){
		schemaRepository.save(schema);
		schemaRepository.addDataToSchema(schema, data).orElseThrow(() -> new IllegalStateException("Could not save schema data."));
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
	 * @param repository from which the schema should be removed
	 * @param schema which should be removed
	 * @return number of statements which have been deleted
	 */
	public long deleteSchemaFromRepository(Repository repository, Schema schema){
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
		return schemaRepository.getDataForSchema(schema);
	}
}

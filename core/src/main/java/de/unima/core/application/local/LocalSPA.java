package de.unima.core.application.local;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.jena.rdf.model.Model;

import de.unima.core.application.SPA;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Entity;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.domain.service.PersistenceService;
import de.unima.core.io.AnyImporterSupport;
import de.unima.core.io.Importer;
import de.unima.core.io.ImporterSupport;
import de.unima.core.io.ImporterSupport.Key;
import de.unima.core.io.file.BPMN20ImporterImpl;
import de.unima.core.io.file.RDFImporterImpl;
import de.unima.core.io.file.XESImporter;
import de.unima.core.io.file.XSDImporter;
import de.unima.core.persistence.local.LocalPersistenceService;

public class LocalSPA implements SPA {
	
	private static final String LOCAL_INDIVIDUAL_NAMESPACE = "http://www.uni-mannheim/spa/local/bpmn/";

	private final PersistenceService persistenceService;
	private final ImporterSupport importerSupport;

	private LocalSPA(PersistenceService persistenceService, ImporterSupport importerSupport) {
		this.persistenceService = persistenceService;
		this.importerSupport = importerSupport;
	}
	
	public static SPA withDataInUniqueMemory(){
		return createWithDefaultImporters(LocalPersistenceService.withDataInUniqueMemory());
	}
	
	public static SPA withDataInSharedMemory(){
		return createWithDefaultImporters(LocalPersistenceService.withDataInSharedMemory());
	}
	
	public static SPA withDataInFolder(String fullPathToFolder){
		return createWithDefaultImporters(LocalPersistenceService.withDataInFolder(Paths.get(fullPathToFolder)));
	}
	
	private static SPA createWithDefaultImporters(PersistenceService persistenceService){
		final ImporterSupport importerSupport = new AnyImporterSupport();
		importerSupport.addImporter(new BPMN20ImporterImpl(LOCAL_INDIVIDUAL_NAMESPACE), "BPMN2");
		importerSupport.addImporter(new XSDImporter(), "XSD");
		importerSupport.addImporter(new XESImporter(), "XES");
		importerSupport.addImporter(new RDFImporterImpl(), "RDF");
		return new LocalSPA(persistenceService, importerSupport);
	}

	public Project createProject(String label) {
		return persistenceService.createPersistentProjectWithGeneratedId(label);
	}

	public List<Project> findAllProjects() {
		return persistenceService.findAllProjects();
	}

	public Optional<Project> findProjectById(String id) {
		return persistenceService.findProjectById(id);
	}

	public String saveProject(Project project) {
		return persistenceService.saveProject(project);
	}

	public void deleteProject(Project project) {
		persistenceService.deleteProject(project);
	}

	public void deleteSchema(Schema schema) {
		persistenceService.deleteSchema(schema);
	}

	public List<Schema> findAllSchemas() {
		return persistenceService.findAllSchemas();
	}

	public Optional<Schema> findSchemaById(String id) {
		return persistenceService.findSchemaById(id);
	}

	public DataPool createDataPool(Project project, String label) {
		return persistenceService.createPeristentDataPoolForProjectWithGeneratedId(project, label);
	}

	public String saveDataPool(DataPool dataPool) {
		return persistenceService.saveDataPool(dataPool);
	}

	public List<DataPool> findAllDataPools() {
		return persistenceService.findAllDataPools();
	}

	public Optional<DataPool> findDataPoolById(String id) {
		return persistenceService.findDataPoolById(id);
	}

	public void deleteDataPool(DataPool dataPool) {
		persistenceService.deleteDataPool(dataPool);
	}

	@Override
	public Schema importSchema(File input, String format, String label) {
		return importFile(input, format, data -> persistenceService.addDataAsNewSchema(label, data)); 
	}

	@Override
	public OutputStream exportSchema(Schema schema, String format) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataBucket importData(File input, String format, String label, DataPool dataPool) {
		return importFile(input, format, data -> persistenceService.addDataAsNewDataBucketToDataPool(dataPool, label, data));
	}
	
	private <T extends Entity<String>, R extends Model> T importFile(File input, String format, Function<R, T> dataToDomainObject) {
		final Optional<Importer<File, R>> importer = importerSupport.findImporterByKey(Key.of(format));
		return importer.map(imp -> imp.importData(input))
				.map(dataToDomainObject)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Format '%s' is not supported. Must be one of %s.", format, importerSupport.listKeysAsString())));
	}

	@Override
	public void removeDataBucket(DataPool dataPool, DataBucket dataBucket) {
		persistenceService.removeDataBucketFromDataPool(dataPool, dataBucket);
	}

	@Override
	public OutputStream exportData(DataBucket bucket) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getSupportedImportFormats() {
		return importerSupport.listKeysAsString();
	}
}

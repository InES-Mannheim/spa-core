package de.unima.core.domain.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unima.core.domain.DataBucket;
import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.io.impl.BPMN20FileImpl;
import de.unima.core.io.impl.BPMN20ImporterImpl;

public class StorageIntegrationTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageIntegrationTest.class);
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private RepositoryService persistentService;
	
	@Before
	public void setUp() throws IOException{
		this.persistentService = RepositoryService.withDataInFolder(temporaryFolder.newFolder().toPath());
	}
	
	@Test
	public void testStoreAndDelete10Schemas() throws IOException {
		final Model bpmn2Schema = loadBpmn2Schema();
		LOGGER.info(String.format("Storing BPMN 2 schema with %s statements. 10 times", bpmn2Schema.size()));
		final List<Schema> schemas = IntStream.range(0, 10).mapToObj(number -> persistentService.addDataAsNewSchema("Schema Nr. "+number, bpmn2Schema)).collect(Collectors.toList());
		schemas.forEach(schema -> assertThat(persistentService.findDataOfSchema(schema).isPresent(), is(true)));
		schemas.forEach(schema -> assertThat(persistentService.findDataOfSchema(schema).get().size(),is(bpmn2Schema.size())));
		
		schemas.forEach(persistentService::deleteSchema);
		schemas.forEach(schema -> assertThat(persistentService.findDataOfSchema(schema).isPresent(), is(false)));
	}
	
	@Test
	public void testStoreAndDeleteWithSingleProjectSchemAndBucket() throws IOException{
		LOGGER.info("Simple Usage scenario.");
		final Schema schema = persistentService.addDataAsNewSchema("BOMN 2.0 ontology", loadBpmn2Schema());
		final Project project = persistentService.createProjectWithGeneratedId("Test project with BPMN 2.0 schema");
		final Repository repository = project.getRepository();
		final DataPool dataPool = persistentService.createNewDataPoolForProjectWithGeneratedId(project, "Test data pool with some data.");
		final DataBucket bucket = persistentService.addDataAsNewDataBucketToDataPool(dataPool, "Partial process data which is consitent with the schema.", createProcessData());
		
		project.linkSchema(schema);
		persistentService.saveProject(project);
	
		assertThat(project.getRepository().getSchemas(), hasItem(schema));
		assertThat(project.getLinkedSchemas(), hasItem(schema));
		assertThat(project.getDataPools(), hasItem(dataPool));
		assertThat(dataPool.getDataBuckets(), hasItem(bucket));
		
		persistentService.deleteDataPool(dataPool);
		assertThat(project.getDataPools(), not(hasItem(dataPool)));
		assertThat(persistentService.findDataOfDataBucket(bucket).isPresent(), is(false));

		persistentService.deleteSchema(schema);
		assertThat(project.getLinkedSchemas(), not(hasItem(schema)));
		
		persistentService.deleteProject(project);
		assertThat(repository.getProjects(), not(hasItem(project)));
	}
	
	@Test
	public void Bpmn2ImportIntegrationTest() throws IOException{
		LOGGER.info("BPM 2 import test.");
		// Import schema as BPMN 2.0
		final Schema schema = persistentService.addDataAsNewSchema("BPMN 2.0", loadBpmn2Schema());
		// Create new project with specified schema
		final Project project = persistentService.createProjectWithGeneratedId("Test Project");
		project.linkSchema(schema);
		persistentService.saveProject(project);
		// Create data pool in project
		final DataPool dataPool = persistentService.createNewDataPoolForProjectWithGeneratedId(project, "Sample Data Pool");
		final BPMN20ImporterImpl importer = new BPMN20ImporterImpl("http://spa.org/TestProject/SampleDataPool#");
		// Import data as new data bucket
		final OntModel importedData = importer.importData(new BPMN20FileImpl(getFilePath("example-spa.bpmn").toString()));
		final DataBucket bucket = persistentService.addDataAsNewDataBucketToDataPool(dataPool, "Example SPA process", importedData);
		// Load data according to data schema; not consistent with data model
		final Model m = persistentService.findDataOfDataBucket(bucket).get();
		
		// assert that statements are loaded
		final List<String> expectedStringStatements = Lists.newArrayList(
				"[http://spa.org/TestProject/SampleDataPool#StartEvent_1, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://dkm.fbk.eu/index.php/BPMN2_Ontology#startEvent]"
				,"[http://spa.org/TestProject/SampleDataPool#StartEvent_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#id, \"StartEvent_1\"]"
				,"[http://spa.org/TestProject/SampleDataPool#StartEvent_1, http://spa.org/TestProject/SampleDataPool#isInterrupting, \"true\"^^http://www.w3.org/2001/XMLSchema#boolean]"
				,"[http://spa.org/TestProject/SampleDataPool#Process_1, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://dkm.fbk.eu/index.php/BPMN2_Ontology#process]"
				,"[http://spa.org/TestProject/SampleDataPool#Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#id, \"Process_1\"]"
				,"[http://spa.org/TestProject/SampleDataPool#Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isExecutable, \"false\"^^http://www.w3.org/2001/XMLSchema#boolean]"
				,"[http://spa.org/TestProject/SampleDataPool#Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isClosed, \"false\"^^http://www.w3.org/2001/XMLSchema#boolean]"
				,"[http://spa.org/TestProject/SampleDataPool#Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#has_startEvent, http://spa.org/TestProject/SampleDataPool#StartEvent_1]"
				,"[http://spa.org/TestProject/SampleDataPool#Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#processType, \"None\"]");
		m.listStatements().toList().stream().map(Object::toString).forEach(statement -> assertThat(expectedStringStatements.contains(statement.toString()), is(true)));
	}

	private Model createProcessData() throws IOException {
		return loadFileAsModel("scanMailProcessModel.owl");
	}

	private Model loadBpmn2Schema() throws IOException {
		return loadFileAsModel("BPMN_2.0_ontology.owl");
	}

	private Model loadFileAsModel(final String fileName) throws IOException {
		final Path path = getFilePath(fileName);
		final InputStream inputStreamForFile = Files.newInputStream(path, StandardOpenOption.READ);
		return ModelFactory.createDefaultModel().read(inputStreamForFile, null);
	}

	private Path getFilePath(final String fileName) {
		return Paths.get(this.getClass().getResource("/"+fileName).getFile());
	}
}

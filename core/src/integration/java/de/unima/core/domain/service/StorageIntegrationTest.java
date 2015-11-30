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
		schemas.forEach(schema -> assertThat(persistentService.findDataForSchema(schema).isPresent(), is(true)));
		schemas.forEach(schema -> assertThat(persistentService.findDataForSchema(schema).get().size(),is(bpmn2Schema.size())));
		
		schemas.forEach(persistentService::deleteSchema);
		schemas.forEach(schema -> assertThat(persistentService.findDataForSchema(schema).isPresent(), is(false)));
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

	private Model createProcessData() throws IOException {
		return loadFileAsModel("scanMailProcessModel.owl");
	}

	private Model loadBpmn2Schema() throws IOException {
		return loadFileAsModel("BPMN_2.0_ontology.owl");
	}

	private Model loadFileAsModel(final String fileName) throws IOException {
		final Path path = Paths.get(this.getClass().getResource("/"+fileName).getFile());
		final InputStream inputStreamForFile = Files.newInputStream(path, StandardOpenOption.READ);
		return ModelFactory.createDefaultModel().read(inputStreamForFile, null);
	}
}

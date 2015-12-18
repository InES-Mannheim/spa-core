package de.unima.core.application.local;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.unima.core.application.SPA;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.domain.service.PersistenceService;
import de.unima.core.persistence.local.LocalPersistenceService;

public class LocalSPATest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	private SPA spa;
	private PersistenceService service;

	@Before
	public void setUp(){
		this.spa = LocalSPA.withDataInSharedMemory();
		this.service = LocalPersistenceService.withDataInSharedMemory();
	}
	
	@Test
	public void whenFileFormatIsNotSupportedThenAnIllegalArgumentExceptionShouldBeThrown(){
		expected.expect(IllegalArgumentException.class);
		spa.importSchema(new File(""), "Nope", "na");
	}
	
	@Test
	public void whenOntologyIsImportedFromXsdASchemaShouldBeCreated(){
		final Schema schema = spa.importSchema(getFilePath("xml/xes.xsd").toFile(), "XSD", "XES");
		assertThat(schema, is(not(nullValue())));
		assertThat(schema.getLabel(), is("XES"));
	}
	
	@Test
	public void whenXmlIsImportedADataBucketShouldBeCreated(){
		final Schema schema = spa.importSchema(getFilePath("xml/xes.xsd").toFile(), "XSD", "XES");
		final Project project = spa.createProject("Test");
		project.linkSchema(schema);
		spa.saveProject(project);
		final DataPool pool = spa.createDataPool(project, "Pool1");
		final DataBucket bucket = spa.importData(getFilePath("running-example.xes").toFile(), "XES", "example xes", pool);
		
		assertThat(bucket.getLabel(), is(equalTo("example xes")));
		final Optional<Model> model = service.findDataOfDataBucket(bucket);
		assertThat(model.isPresent(), is(true));
	}
	
	@Test
	public void whenBpmnIsImportedADataBucketShouldBeCreated(){
		final Schema schema = spa.importSchema(getFilePath("BPMN_2.0_ontology.owl").toFile(), "RDF", "BPMN2 ontology");
		final Project project = spa.createProject("Test Project");
		project.linkSchema(schema);
		spa.saveProject(project);
		final DataPool dataPool = spa.createDataPool(project, "Sample Data Pool");
		final DataBucket bucket = spa.importData(getFilePath("example-spa.bpmn").toFile(), "BPMN2", "Bucket 1", dataPool);
		final Model foundData = service.findDataOfDataBucket(bucket).get();
		final List<String> expectedStringStatements = Lists.newArrayList(
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://dkm.fbk.eu/index.php/BPMN2_Ontology#process]"
				,"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isExecutable, \"false\"^^http://www.w3.org/2001/XMLSchema#boolean]"
				,"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isClosed, \"false\"^^http://www.w3.org/2001/XMLSchema#boolean]"
				,"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#id, \"Process_1\"]"
				,"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#has_startEvent, http://www.uni-mannheim/spa/local/bpmn/StartEvent_1]"
				,"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#processType, \"None\"]"
				,"[http://www.uni-mannheim/spa/local/bpmn/StartEvent_1, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://dkm.fbk.eu/index.php/BPMN2_Ontology#startEvent]"
				,"[http://www.uni-mannheim/spa/local/bpmn/StartEvent_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#id, \"StartEvent_1\"]"
				,"[http://www.uni-mannheim/spa/local/bpmn/StartEvent_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isInterrupting, \"true\"^^http://www.w3.org/2001/XMLSchema#boolean]");
		foundData.listStatements().toList().stream().map(Object::toString).forEach(statement -> assertThat(expectedStringStatements.contains(statement.toString()), is(true)));
	}
	
	protected Model loadFileAsModel(final String fileName) throws IOException {
		final Path path = getFilePath(fileName);
		final InputStream inputStreamForFile = Files.newInputStream(path, StandardOpenOption.READ);
		return ModelFactory.createDefaultModel().read(inputStreamForFile, null);
	}

	protected Path getFilePath(final String fileName) {
		System.out.println(this.getClass().getResource("/"+fileName));
		return Paths.get(this.getClass().getResource("/"+fileName).getFile());
	}
}

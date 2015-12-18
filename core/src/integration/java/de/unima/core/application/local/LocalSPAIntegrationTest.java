package de.unima.core.application.local;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Throwables;

import de.unima.core.BaseIntegrationTest;
import de.unima.core.application.SPA;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.domain.service.PersistenceService;
import de.unima.core.persistence.local.LocalPersistenceService;

public class LocalSPAIntegrationTest extends BaseIntegrationTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private SPA spa;
	private PersistenceService service;

	@Before
	public void setUp() {
		this.spa = LocalSPA.withDataInSharedMemory();
		this.service = LocalPersistenceService.withDataInSharedMemory();
	}
	
	@Test
	public void whenOntologyIsImportedFromXsdASchemaShouldBeCreated() {
		final Schema schema = spa.importSchema(getFilePath("xml/xes.xsd").toFile(), "XSD", "XES");
		assertThat(schema, is(not(nullValue())));
		assertThat(schema.getLabel(), is("XES"));
	}

	@Test
	public void whenXmlIsImportedADataBucketShouldBeCreated() {
		final Schema schema = spa.importSchema(getFilePath("xml/xes.xsd").toFile(), "XSD", "XES");
		final Project project = spa.createProject("Test");
		project.linkSchema(schema);
		spa.saveProject(project);
		final DataPool pool = spa.createDataPool(project, "Pool1");
		final DataBucket bucket = spa.importData(getFilePath("running-example.xes").toFile(), "XES", "example xes",
				pool);

		assertThat(bucket.getLabel(), is(equalTo("example xes")));
		final Optional<Model> model = service.findDataOfDataBucket(bucket);
		assertThat(model.isPresent(), is(true));
	}

	@Test
	public void whenBpmnIsImportedADataBucketShouldBeCreated() {
		final Schema schema = spa.importSchema(getFilePath("BPMN_2.0_ontology.owl").toFile(), "RDF", "BPMN2 ontology");
		final Project project = spa.createProject("Test Project");
		project.linkSchema(schema);
		spa.saveProject(project);
		final DataPool dataPool = spa.createDataPool(project, "Sample Data Pool");
		final DataBucket bucket = spa.importData(getFilePath("example-spa.bpmn").toFile(), "BPMN2", "Bucket 1",
				dataPool);
		final Model foundData = service.findDataOfDataBucket(bucket).get();
		final List<String> expectedStringStatements = Lists.newArrayList(
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://dkm.fbk.eu/index.php/BPMN2_Ontology#process]",
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isExecutable, \"false\"^^http://www.w3.org/2001/XMLSchema#boolean]",
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isClosed, \"false\"^^http://www.w3.org/2001/XMLSchema#boolean]",
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#id, \"Process_1\"]",
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#has_startEvent, http://www.uni-mannheim/spa/local/bpmn/StartEvent_1]",
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#processType, \"None\"]",
				"[http://www.uni-mannheim/spa/local/bpmn/StartEvent_1, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://dkm.fbk.eu/index.php/BPMN2_Ontology#startEvent]",
				"[http://www.uni-mannheim/spa/local/bpmn/StartEvent_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#id, \"StartEvent_1\"]",
				"[http://www.uni-mannheim/spa/local/bpmn/StartEvent_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#isInterrupting, \"true\"^^http://www.w3.org/2001/XMLSchema#boolean]");
		foundData.listStatements().toList().stream().map(Object::toString)
				.forEach(statement -> assertThat(expectedStringStatements, hasItem(statement)));
	}

	@Test
	public void rdfWhichHasBeenImportedMustMatchExportedRdf() throws IOException {
		final File exportLoaction = folder.newFile("tree.rdf");

		final Schema schema = spa.importSchema(getFilePath("tree.rdf").toFile(), "RDF", "Beckett");
		spa.exportSchema(schema, "RDF", exportLoaction);

		final Model exportedModel = ModelFactory.createDefaultModel().read(new FileInputStream(exportLoaction), null);
		final List<String> expectedStringStatements = service.findDataOfSchema(schema).get().listStatements().toList()
				.stream().map(Object::toString).collect(Collectors.toList());

		exportedModel.listStatements().toList().stream().map(Object::toString)
				.forEach(statement -> assertThat(expectedStringStatements, hasItem(statement.toString())));
	}

	@Test
	public void xesWhichHasBeenExportedMustMatchExportedXes() throws IOException {
		final Project project = spa.createProject("Test Project");
		spa.saveProject(project);
		final DataPool dataPool = spa.createDataPool(project, "Sample Data Pool");
		final DataBucket bucket = spa.importData(getFilePath("running-example.xes").toFile(), "XES", "running example",
				dataPool);

		final File exportLoaction = folder.newFile("example.xes");
		spa.exportData(bucket, "XES", exportLoaction);

		final List<XTrace> createdEvents = readLogFromFile(exportLoaction);
		final List<XTrace> expectedEvents = readLogFromFile(getFilePath("running-example.xes").toFile());

		assertThat(createdEvents.size(), is(expectedEvents.size()));
	}

	private XLog readLogFromFile(File xesFile) {
		for (XParser parser : XParserRegistry.instance().getAvailable()) {
			if (parser.canParse(xesFile)) {
				try {
					return parser.parse(xesFile).get(0);
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}
		}
		return null;
	}
}

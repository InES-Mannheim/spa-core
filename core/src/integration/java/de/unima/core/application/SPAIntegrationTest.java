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
package de.unima.core.application;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Throwables;

import de.unima.core.BaseIntegrationTest;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.persistence.PersistenceService;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

public class SPAIntegrationTest extends BaseIntegrationTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private SPA spa;
	private PersistenceService service;

	@Before
	public void setUp() {
		this.spa = SPABuilder.local().sharedMemory().build();
		this.service = new PersistenceService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
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
				"[http://www.uni-mannheim/spa/local/bpmn/Process_1, http://dkm.fbk.eu/index.php/BPMN2_Ontology#has_flowNode, http://www.uni-mannheim/spa/local/bpmn/StartEvent_1]",
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
	
	@Ignore("Fails as exported bpmn does not contain all information of the orignal bpmn")
	@Test
	public void bpmnWhichHasBeenImportedMustMatchExportedBPMN() throws IOException {
		final Schema schema = spa.importSchema(getFilePath("BPMN_2.0_ontology.owl").toFile(), "RDF", "BPMN2 ontology");
		final Project project = spa.createProject("Test Project");
		project.linkSchema(schema);
		spa.saveProject(project);
		final DataPool dataPool = spa.createDataPool(project, "Sample Data Pool");
		final DataBucket bucket = spa.importData(getFilePath("example-spa.bpmn").toFile(), "BPMN2", "Bucket 1", dataPool);
		
		final File exportLocation = folder.newFile("example-spa.bpmn");
		spa.exportData(bucket, "BPMN2", exportLocation);
		
		final BpmnModelInstance exportedModel = readBpmnFromFile(exportLocation);
		final BpmnModelInstance originalModel = readBpmnFromFile(getFilePath("example-spa.bpmn").toFile());
		
		assertThat(exportedModel.getDocument(), is(equalTo(originalModel.getDocument())));
	}
	
	private BpmnModelInstance readBpmnFromFile(File bpmn){
		return Bpmn.readModelFromFile(bpmn);
	}
	
	@Test
	public void whenOneSchemaIsStoredOnlyOneSchemaShouldBeReturnedFromFindAllSchemas(){
		final SPA spa = SPABuilder.local().uniqueMemory().build();
		spa.importSchema(getFilePath("BPMN_2.0_ontology.owl").toFile(), "RDF", "BPMN2 ontology");
		final List<Schema> allSchemas = spa.findAllSchemas();
		assertThat(allSchemas.size(), is(1));
	}
}

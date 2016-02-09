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
package example;

import java.util.Optional;

import org.apache.jena.rdf.model.Model;

import de.unima.core.application.SPA;
import de.unima.core.application.SPABuilder;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.persistence.PersistenceService;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

public class BpmnExamples extends BaseExample {
	public static void main(String[] args) {
		final DataBucket bucket = loadBpmnDataIntoDataBucket();
		lookIntoDataOfDataBucket(bucket);
	}

	private static DataBucket loadBpmnDataIntoDataBucket() {
		// Shared memory is accessible over the complete runtime
		final SPA spa = SPABuilder.local().sharedMemory().build();
		// Import schema for BPMN 2.0
		final Schema schema = spa.importSchema(getFilePath("BPMN_2.0_ontology.owl").toFile(), "RDF", "BPMN 2 Schema");
		// Create new project with specified schema
		final Project project = spa.createProject("Test Project");
		project.linkSchema(schema);
		spa.saveProject(project);
		// Create data pool in project
		final DataPool dataPool = spa.createDataPool(project, "Sample Data Pool");
		// Import data as new data bucket
		final DataBucket importedData = spa.importData(getFilePath("example-spa.bpmn").toFile(), "BPMN2", "Example Bucket", dataPool);
		return importedData;
	}

	private static void lookIntoDataOfDataBucket(DataBucket bucket) {
		// Persistence service provides direct access to ontological data
		final PersistenceService service = new PersistenceService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
		// Read data for bucket
		final Optional<Model> model = service.findDataOfDataBucket(bucket);
		// If data is present, print it
		model.ifPresent(data -> System.out.println(data));
	}
}

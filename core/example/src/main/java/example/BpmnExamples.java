package example;

import java.util.Optional;

import org.apache.jena.rdf.model.Model;

import de.unima.core.application.SPA;
import de.unima.core.application.local.LocalSPA;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.domain.service.PersistenceService;
import de.unima.core.persistence.local.LocalPersistenceService;

public class BpmnExamples extends BaseExample {
	public static void main(String[] args) {
		final DataBucket bucket = loadBpmnDataIntoDataBucket();
		lookIntoDataOfDataBucket(bucket);
	}

	private static DataBucket loadBpmnDataIntoDataBucket() {
		// Shared memory is accessible over the complete runtime
		final SPA spa = LocalSPA.withDataInSharedMemory();
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
		final PersistenceService service = LocalPersistenceService.withDataInSharedMemory();
		// Read data for bucket
		final Optional<Model> model = service.findDataOfDataBucket(bucket);
		// If data is present, print it
		model.ifPresent(data -> System.out.println(data));
	}
}
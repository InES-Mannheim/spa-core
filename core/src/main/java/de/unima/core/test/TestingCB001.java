package de.unima.core.test;


import org.apache.jena.ontology.OntModel;

import de.unima.core.domain.DataPool;
import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;
import de.unima.core.io.BPMN20File;
import de.unima.core.io.IOObject;
import de.unima.core.io.RDFFile;
import de.unima.core.io.impl.BPMN20FileImpl;
import de.unima.core.io.impl.BPMN20ImporterImpl;
import de.unima.core.io.impl.IOObjectImpl;
import de.unima.core.io.impl.RDFFileImpl;
import de.unima.core.io.impl.RDFImporterImpl;

public class TestingCB001 {

	public static void main(String[] args) {
		
		// Create repository
		Repository r = new RepositoryImpl();
		
		// Create scheme; specify how scheme can be loaded cf. importer
		IOObject<RDFFile> scheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("schema/BPMN_2.0_ontology.owl"), new RDFImporterImpl());
	
		// Register scheme as "BPMN2.0"; define how data for the scheme can be imported
		r.registerDataScheme("BPMN2.0", scheme, new BPMN20ImporterImpl());
		
		// Create new project with specified scheme
		r.createProject("TestProject", "BPMN2.0");
		
		Project p = r.getProject("TestProject");
		
		// Create data pool in project
		p.createDataPool("SampleDataPool");
		
		DataPool dp = p.getDataPool("SampleDataPool");
		
		// Read process model from example schema; redundant information: model importer
		IOObject<BPMN20File> processModel = new IOObjectImpl<BPMN20File>(new BPMN20FileImpl("tmp/example-spa.bpmn"), new BPMN20ImporterImpl());
		
		// Add data model to pool
		dp.addDataModel("SampleBPMN2.0Model", processModel);
		
		// Get first data schema from repository
		DataScheme s = r.getDataScheme(p.getSchemeIDs().iterator().next());
		
		// Load data according to data schema; not consistent with data model
		OntModel m = s.getData();
		
		// print out data
		System.out.println(m.listStatements().toList().toString());
	}

}

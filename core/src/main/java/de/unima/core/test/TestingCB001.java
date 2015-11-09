package de.unima.core.test;


import de.unima.core.domain.DataPool;
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
		
		Repository r = new RepositoryImpl();
		
		IOObject<RDFFile> scheme = new IOObjectImpl<RDFFile>(new RDFFileImpl(), new RDFImporterImpl());
	
		r.registerDataScheme("BPMN2.0", scheme, new BPMN20ImporterImpl());
		
		r.createProject("TestProject", "BPMN2.0");
		
		Project p = r.getProject("TestProject");
		
		p.createDataPool("SampleDataPool");
		
		DataPool dp = p.getDataPool("SampleDataPool");
		
		IOObject<BPMN20File> processModel = new IOObjectImpl<BPMN20File>(new BPMN20FileImpl(), new BPMN20ImporterImpl());
		
		dp.addDataModel("SampleBPMN2.0Model", processModel);
		
	  
	}

}

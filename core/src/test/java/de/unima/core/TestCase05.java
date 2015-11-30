package de.unima.core;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

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

public class TestCase05 {

	private DataPool dataPool = null;
	
	@Before
	public void setUp() {
		Repository repository = new RepositoryImpl();
		IOObject<RDFFile> scheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("schema/BPMN_2.0_ontology.owl"), new RDFImporterImpl());
		repository.registerDataScheme("BPMN2.0", scheme, new BPMN20ImporterImpl());
		repository.createProject("TestProject", "BPMN2.0");
		Project project = repository.getProject("TestProject");
		project.createDataPool("SampleDataPool");
		this.dataPool = project.getDataPool("SampleDataPool");
	}
	
	@Test
	public void test01_addEmptyProcess() {
		IOObject<BPMN20File> processModel = new IOObjectImpl<BPMN20File>(new BPMN20FileImpl("tmp/example-spa.bpmn"), new BPMN20ImporterImpl());
		this.dataPool.addDataModel("Empty BPMN-2.0 Process", processModel);
				
		assertTrue(dataPool.updateDataPool());
		assertTrue(dataPool.isValid());
	}
	
	@Test
	public void test02_addMailProcess() {
		IOObject<BPMN20File> processModel = new IOObjectImpl<BPMN20File>(new BPMN20FileImpl("tmp/Mail Process.bpmn"), new BPMN20ImporterImpl());
		this.dataPool.addDataModel("Mail Process", processModel);
				
		assertTrue(dataPool.updateDataPool());
		assertTrue(dataPool.isValid());
	}
}

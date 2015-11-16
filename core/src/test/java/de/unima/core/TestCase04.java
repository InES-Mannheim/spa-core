package de.unima.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;
import de.unima.core.io.IOObject;
import de.unima.core.io.RDFFile;
import de.unima.core.io.impl.BPMN20ImporterImpl;
import de.unima.core.io.impl.IOObjectImpl;
import de.unima.core.io.impl.RDFFileImpl;
import de.unima.core.io.impl.RDFImporterImpl;

public class TestCase04 {

	private Project project = null;
	
	@Before
	public void setUp() {
		Repository repository = new RepositoryImpl();
		IOObject<RDFFile> scheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("schema/BPMN_2.0_ontology.owl"), new RDFImporterImpl());
		repository.registerDataScheme("BPMN2.0", scheme, new BPMN20ImporterImpl());
		repository.createProject("TestProject", "BPMN2.0");
		this.project = repository.getProject("TestProject");
	}
	
	@Test
	public void test01_createDataPool() {
		/*bollean created = */ this.project.createDataPool("SampleDataPool");
		DataPool dataPool = this.project.getDataPool("SampleDataPool");
		
		//assertTrue(created);
		
		assertNotEquals(dataPool, null);
		
		assertTrue(dataPool.isValid());
		assertTrue(dataPool.updateDataPool());
		assertTrue(dataPool.isValid());
	}
	
}

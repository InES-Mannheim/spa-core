package de.unima.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;
import de.unima.core.io.IOObject;
import de.unima.core.io.RDFFile;
import de.unima.core.io.impl.BPMN20ImporterImpl;
import de.unima.core.io.impl.IOObjectImpl;
import de.unima.core.io.impl.RDFFileImpl;
import de.unima.core.io.impl.RDFImporterImpl;

public class TestCase03 {

	private Repository repository = null;
	
	@Before
	public void setUp() {
		repository = new RepositoryImpl();
		IOObject<RDFFile> scheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("schema/BPMN_2.0_ontology.owl"), new RDFImporterImpl());
		this.repository.registerDataScheme("BPMN2.0", scheme, new BPMN20ImporterImpl());
	}
	
	@Test
	public void test01_createProject() {
		boolean created = this.repository.createProject("TestProject", "BPMN2.0");
		Project project = this.repository.getProject("TestProject");
		assertTrue(created);
		assertNotEquals(project, null);
		assertNotEquals(project.getRepository(), null);
		
		assertEquals(project.getDataPoolIDs().size(),0);
	}

}

package de.unima.core;

import org.junit.Before;
import org.junit.Test;

import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;
import de.unima.core.io.IOObject;
import de.unima.core.io.RDFFile;
import de.unima.core.io.impl.BPMN20ImporterImpl;
import de.unima.core.io.impl.IOObjectImpl;
import de.unima.core.io.impl.RDFFileImpl;
import de.unima.core.io.impl.RDFImporterImpl;

public class TestCase02 {

	private Repository repository = null;
	
	@Before
	public void setUp() {
		repository = new RepositoryImpl();
	}
	
	@Test
	public void test01_registerScheme() {
		IOObject<RDFFile> scheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("schema/BPMN_2.0_ontology.owl"), new RDFImporterImpl());
		this.repository.registerDataScheme("BPMN2.0", scheme, new BPMN20ImporterImpl());
	}
}

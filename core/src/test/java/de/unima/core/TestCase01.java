package de.unima.core;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;
import de.unima.core.io.impl.BPMN20ImporterImpl;
import de.unima.core.io.impl.IOObjectImpl;
import de.unima.core.io.impl.RDFFileImpl;
import de.unima.core.io.impl.RDFImporterImpl;

public class TestCase01 {
	
	@Test
	public void test01_setupRepository() {
		Repository repository = new RepositoryImpl();
	}

}

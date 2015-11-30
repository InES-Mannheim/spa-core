package de.unima.core;

import org.junit.Test;
import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;

public class TestCase01 {
	
	@Test
	public void test01_setupRepository() {
		Repository repository = new RepositoryImpl();
	}

}

package de.unima.core.application.local;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import de.unima.core.application.SPA;

public class LocalSPATest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private SPA spa;

	@Before
	public void setUp(){
		this.spa = LocalSPA.withDataInSharedMemory();
	}
	
	@Test
	public void whenFileFormatIsNotSupportedThenAnIllegalArgumentExceptionShouldBeThrown(){
		expected.expect(IllegalArgumentException.class);
		spa.importSchema(new File(""), "Nope", "na");
	}
}

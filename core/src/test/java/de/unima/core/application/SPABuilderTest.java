package de.unima.core.application;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SPABuilderTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void whenLocalUniqueMemoryIsUsedBuilderShouldAlwaysSucceed() {
		SPA spa = SPABuilder.local().uniqueMemory().build();
		assertThat(spa, is(notNullValue()));
	}

	@Test
	public void whenLocalSharedMemoryIsUsedBuilderShouldAlwaysSucceed() {
		SPA spa = SPABuilder.local().sharedMemory().build();
		assertThat(spa, is(notNullValue()));
	}
	
	@Test
	public void whenValidLocalFolderIsUsedBuilderShouldSucceed() throws IOException {
		SPA spa = SPABuilder.local().folder(folder.newFolder().toPath().toString()).build();
		assertThat(spa, is(notNullValue()));
	}
	
	@Test(expected = InvalidPathException.class)
	public void whenInvalidLocalFolderIsUsedBuilderShouldThrowException() throws IOException {
		SPABuilder.local().folder(folder.newFolder().toPath().toString().toString() + "\"").build();
	}
	
}

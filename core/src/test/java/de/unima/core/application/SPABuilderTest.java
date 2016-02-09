package de.unima.core.application;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.unima.core.application.SPABuilder.LocalBuilder.UniqueMemoryBuilder;

public class SPABuilderTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void whenNoNamespaceIsSetThenDefaultNamespaceIsUsed() {
		UniqueMemoryBuilder umBuilder = SPABuilder.local().uniqueMemory();
		umBuilder.build();
		assertThat(umBuilder.getNamespace(), equalTo("http://www.uni-mannheim/spa/local/bpmn/"));
	}
	
	@Test
	public void whenNamespaceIsSetThenNamespaceIsUsed() {
		String customNamespace = "http://some.namespace:8080/test/";
		UniqueMemoryBuilder umBuilder = SPABuilder.local().uniqueMemory().namespace(customNamespace);
		umBuilder.build();
		assertThat(umBuilder.getNamespace(), equalTo(customNamespace));
	}
	
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
		SPABuilder.local().folder(folder.newFolder().toPath().toString().toString() + "\0").build();
	}
	
}

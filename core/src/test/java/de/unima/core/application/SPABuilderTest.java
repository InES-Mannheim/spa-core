package de.unima.core.application;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SPABuilderTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void whenLocalUniqueMemoryIsUsedBuilderShouldAlwaysSucceed() {
		SPA spa = new SPABuilder().local().uniqueMemory().build();
		assertThat(spa, is(notNullValue()));
	}

	@Test
	public void whenLocalSharedMemoryIsUsedBuilderShouldAlwaysSucceed() {
		SPA spa = new SPABuilder().local().sharedMemory().build();
		assertThat(spa, is(notNullValue()));
	}
	
	@Test
	public void whenValidLocalFolderIsUsedBuilderShouldSucceed() throws IOException {
		SPA spa = new SPABuilder().local().folder(folder.newFolder().toPath().toString()).build();
		assertThat(spa, is(notNullValue()));
	}
	
	@Test(expected = InvalidPathException.class)
	public void whenInvalidLocalFolderIsUsedBuilderShouldThrowException() throws IOException {
		new SPABuilder().local().folder(folder.newFolder().toPath().toString().toString() + "\"").build();
	}
	
	@Test(expected = Exception.class)
	public void whenUnconfiguredRemoteVirtuosoBuilderIsUsedAnExceptionShouldBeThrown() {
		new SPABuilder().remote().virtuoso().build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenURLForRemoteVirtuosoBuilderIsEmptyThenThrowException() {
		new SPABuilder().remote().virtuoso().user("Alice").password("Secret").build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenURLForRemoteVirtuosoBuilderIsNotSetThenThrowException() {
		new SPABuilder().remote().virtuoso().url("").user("Alice").password("Secret").build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenURLForRemoteVirtuosoBuilderIsNotValidThenThrowIllegalArgumentException() {
		new SPABuilder().remote().virtuoso().url("http://malformed!url:someport/").user("Alice").password("").build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenUserForRemoteVirtuosoBuilderIsEmptyThenThrowException() {
		new SPABuilder().remote().virtuoso().url("http://localhost:8080/").password("Secret").build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void whenUserForRemoteVirtuosoBuilderIsNotSetThenThrowException() {
		new SPABuilder().remote().virtuoso().url("http://localhost:8080/").user("").password("Secret").build();
	}
	
	@Test(expected = NullPointerException.class)
	public void whenPasswordForRemoteVirtuosoBuilderIsNotSetThenThrowNullPointerException() {
		new SPABuilder().remote().virtuoso().url("http://localhost:8080/").user("Alice").build();
	}
	
	@Ignore
	@Test
	public void whenRemoteVirtuosoBuilderIsWellConfiguredBuilderSucceeds() {
		SPA spa = new SPABuilder().remote().virtuoso().url("http://localhost:8080/").user("Alice").password("Secret").build();
		assertThat(spa, is(notNullValue()));
	}
	
}

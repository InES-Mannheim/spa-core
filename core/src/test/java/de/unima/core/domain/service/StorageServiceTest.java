package de.unima.core.domain.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.unima.core.domain.Repository;
import de.unima.core.domain.service.StorageService;

public class StorageServiceTest {

	private StorageService service;

	@Before
	public void setUp(){
		this.service = new StorageService(null);
	}
	
	@Test
	public void whenANewRepositoryIsCreatedItsIdShouldBeGenerated(){
		final Repository repository = service.createRepositorWithGeneratedId();
		assertThat(repository.getId(), is(notNullValue()));
	}
	
	@Test
	public void generatedIdMustBeAnUri(){
		final Repository repository = service.createRepositorWithGeneratedId();
		String id = repository.getId();
		assertThat(id, startsWith("http"));
	}
	
	@Test
	public void whenAnEmptyRepositoryIsSavedItsIdMustBeSet(){
		final Repository repository = service.createRepositorWithGeneratedId();
		service.saveRepository(repository);
	}
}

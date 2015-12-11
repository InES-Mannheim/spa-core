package de.unima.core.persistence.local;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Repository;
import de.unima.core.domain.model.Schema;
import de.unima.core.persistence.local.RepositoryRepository;
import de.unima.core.storage.jena.JenaTDBStore;

public class RepositoryRepositoryTest {

	private RepositoryRepository repository;

	@Before
	public void setUp(){
		this.repository = new RepositoryRepository(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	@Test
	public void whenRepositoryIsSavedSchemasAndProjectsUrisMustBeSavedAlso(){
		final Repository repo = new Repository("http://www.test.de/Repository/1");
		repo.addProject(new Project("http://www.test.de/Project/1", "First project", repo));
		repo.addSchema(new Schema("http://www.test.de/Schema/1"));
		repository.save(repo);
		
		final long deletedStatements = repository.delete(repo);
		assertThat(deletedStatements, is(3l));
	}
	
	@Test
	public void whenRepositoryIsSavedSchemasAndProjectsUrisMustBeSavedAlso_100(){
		final Repository repo = new Repository("http://www.test.de/Repository/1");
		add100Projects(repo);
		add100Schemas(repo);
		repository.save(repo);
		
		final long deletedStatements = repository.delete(repo);
		assertThat(deletedStatements, is(201l));
	}
	
	final void add100Projects(Repository repo){
		IntStream.range(0, 100).forEach(number -> repo.addProject(new Project("http://www.test.de/Project/"+number, number+" project", repo)));
	}
	
	final void add100Schemas(Repository repo){
		IntStream.range(0, 100).forEach(number -> repo.addSchema(new Schema("http://www.test.de/Schema/"+number)));
	}
}
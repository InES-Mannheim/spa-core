package de.unima.core.domain.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.domain.service.RepositoryService;

public class RepositoryServiceTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	private RepositoryService service;

	@Before
	public void setUp(){
		this.service = RepositoryService.withDataInMemory();
	}
	
	@Test
	public void whenANewRepositoryIsCreatedItsIdShouldBeGenerated(){
		final Repository repository = service.createRepositoryWithGeneratedId();
		assertThat(repository.getId(), is(notNullValue()));
	}
	
	@Test
	public void generatedIdMustBeAnUri(){
		final Repository repository = service.createRepositoryWithGeneratedId();
		String id = repository.getId();
		assertThat(id, startsWith("http"));
	}
	
	@Test
	public void whenANullRepositoryShouldBeSavedThenThrowNullpointerException(){
		expected.expect(NullPointerException.class);
		service.saveRepository(null);
	}
	
	@Test
	public void whenAnRepositoryIsSavedItsIdShouldBeReturned(){
		final Repository repository = service.createRepositoryWithGeneratedId();
		Optional<String> id = service.saveRepository(repository);
		assertThat(id.isPresent(), is(true));
	}
	
	@Test
	public void whenASchemaWasRemovedItShouldNotBeContainedInRepository(){
		final Repository repository = service.createRepositoryWithGeneratedId();
		service.saveRepository(repository);
		final Schema schema = service.addNewSchemaDataToRepository(repository, "Test Schema", createModelWithOneStatement());
		assertThat(service.findDataForSchema(schema).get().size(), is(1l));
		service.deleteSchemaFromRepository(repository, schema);
		assertThat(service.findDataForSchema(schema).get().size(), is(0l));
	}
	
	@Test
	public void whenSchemaIsLinkedToProjectItShouldBeUnlinkedAfterDeletion(){
		final Repository repository = service.createRepositoryWithGeneratedId();
		service.saveRepository(repository);
		final Schema schema = service.addNewSchemaDataToRepository(repository, "Test Schema", createModelWithOneStatement());
		final Project project = new Project("http://www.test.de/Project/1", repository, "test");
		repository.addProject(project);
		project.linkSchema(schema);
		assertThat(project.getLinkedSchemas().size(), is(1));
		service.deleteSchemaFromRepository(repository, schema);
		assertThat(project.getLinkedSchemas().size(), is(0));
	}
	
	private Model createModelWithOneStatement() {
		final Model model = ModelFactory.createDefaultModel();
		model.createResource("http://www.test.de/House/1").addProperty(RDFS.label, "test label");
		return model;
	}
	
	@Test
	public void whenProjectIsCreatedItShouldBeLinkedToAProject(){
		final Repository repository = service.createRepositoryWithGeneratedId();
		final Project project = service.createProjectWithGeneratedIdForRepository(repository, "Test");
		assertThat(repository.getProjects().contains(project), is(true));
		assertThat(project.getRepository(), is(repository));
	}
	
}

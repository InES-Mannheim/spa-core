package de.unima.core.domain.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.unima.core.domain.Project;
import de.unima.core.domain.Schema;

public class RepositoryServiceTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	private RepositoryService service;

	@Before
	public void setUp(){
		this.service = RepositoryService.withDataInUniqueMemory();
	}
	
	@Test
	public void whenASchemaWasRemovedCorrespondingDataShouldNotBeContainedInRepository(){
		final Schema schema = service.addDataAsNewSchema("Test Schema", createModelWithOneStatement());
		assertThat(service.findDataForSchema(schema).get().size(), is(1l));
		service.deleteSchema(schema);
		assertThat(service.findDataForSchema(schema).isPresent(), is(false));
	}
	
	@Test
	public void whenProjectIsCreatedItShouldBeLinkedToAProject(){
		final Project project = service.createProjectWithGeneratedId("Test");
		assertThat(project.getRepository(), is(notNullValue()));
		assertThat(project.getRepository().getProjects().contains(project), is(true));
	}
	
	@Test
	public void whenSchemaIsLinkedToProjectItShouldBeUnlinkedAfterDeletion(){
		final Schema schema = service.addDataAsNewSchema("Test Schema", createModelWithOneStatement());
		final Project project = service.createProjectWithGeneratedId("Test");
		project.linkSchema(schema);
		assertThat(project.getLinkedSchemas().size(), is(1));
		service.deleteSchema(schema);
		assertThat(project.getLinkedSchemas().size(), is(0));
	}
	
	@Test
	public void whenDataOfSchemaIsReplacedItShouldContainTheNewData(){
		final Schema schema = service.addDataAsNewSchema("Test Schema", createModelWithOneStatement());
		final Model before = service.findDataForSchema(schema).get();
		
		service.replaceDataOfSchema(schema, createModelWithOneOtherStatement());
		final Model after = service.findDataForSchema(schema).get();
		
		assertThat(after.listStatements().next(), is(not(equalTo(before.listStatements().next()))));
	}
	
	private Model createModelWithOneOtherStatement() {
		final Model model = ModelFactory.createDefaultModel();
		model.createResource("http://www.test.de/House/2").addProperty(RDFS.label, "Second label");
		return model;
	}

	private Model createModelWithOneStatement() {
		final Model model = ModelFactory.createDefaultModel();
		model.createResource("http://www.test.de/House/1").addProperty(RDFS.label, "test label");
		return model;
	}
	
	
}

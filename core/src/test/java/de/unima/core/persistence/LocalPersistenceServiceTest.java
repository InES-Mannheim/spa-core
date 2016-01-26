/*******************************************************************************
 *    Copyright 2016 University of Mannheim
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.unima.core.persistence;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.storage.jena.JenaTDBStore;

public class LocalPersistenceServiceTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	private PersistenceService service;

	@Before
	public void setUp(){
		this.service = new PersistenceService(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	@Test
	public void whenASchemaWasRemovedCorrespondingDataShouldNotBeContainedInRepository(){
		final Schema schema = service.addDataAsNewSchema("Test Schema", createModelWithOneStatement());
		assertThat(service.findDataOfSchema(schema).get().size(), is(1l));
		service.deleteSchema(schema);
		assertThat(service.findDataOfSchema(schema).isPresent(), is(false));
	}
	
	@Test
	public void whenProjectIsCreatedItShouldBeLinkedToAProject(){
		final Project project = service.createPersistentProjectWithGeneratedId("Test");
		assertThat(project.getRepository(), is(notNullValue()));
		assertThat(project.getRepository().getProjects().contains(project), is(true));
	}
	
	@Test
	public void whenProjectIsLoadedAllLinkedSchemasAndDataPoolsShouldBeLoadedToo(){
		final Project project = service.createPersistentProjectWithGeneratedId("Test");
		final DataPool pool = service.createPeristentDataPoolForProjectWithGeneratedId(project, "Test");
		final DataBucket firstBucket = service.addDataAsNewDataBucketToDataPool(pool, "First bucket", ModelFactory.createDefaultModel());
		final DataBucket secondBucket = service.addDataAsNewDataBucketToDataPool(pool, "Second bucket", ModelFactory.createDefaultModel());
		final DataBucket thirdBucket = service.addDataAsNewDataBucketToDataPool(pool, "Third bucket", ModelFactory.createDefaultModel());
		service.saveDataPool(pool);
		
		final Schema firstSchema = service.addDataAsNewSchema("Schema 1", ModelFactory.createDefaultModel());
		final Schema secondSchema = service.addDataAsNewSchema("Schema 2", ModelFactory.createDefaultModel());
		project.linkSchema(firstSchema);
		project.linkSchema(secondSchema);
		service.saveProject(project);
		
		final Optional<Project> loadedProject = service.findProjectById(project.getId());
		
		assertThat(loadedProject.isPresent(), is(true));
		assertThat(loadedProject.get().getDataPools(), hasItem(pool));
		final DataPool loadedDatapool = loadedProject.get().getDataPools().get(0);
		assertThat(loadedDatapool.getProject(),is(loadedProject.get()));
		assertThat(loadedDatapool.getDataBuckets(), hasItem(firstBucket));
		assertThat(loadedDatapool.getDataBuckets(), hasItem(secondBucket));
		assertThat(loadedDatapool.getDataBuckets(), hasItem(thirdBucket));
		assertThat(loadedProject.get().getLinkedSchemas(), hasItem(firstSchema));
		assertThat(loadedProject.get().getLinkedSchemas(), hasItem(secondSchema));
	}
	
	@Test
	public void whenSchemaIsLinkedToProjectItShouldBeUnlinkedAfterDeletion(){
		final Schema schema = service.addDataAsNewSchema("Test Schema", createModelWithOneStatement());
		final Project project = service.createPersistentProjectWithGeneratedId("Test");
		
		project.linkSchema(schema);
		service.saveProject(project);
		assertThat(project.getLinkedSchemas().size(), is(1));
		
		service.deleteSchema(schema);
		final Project foundProject = service.findProjectById(project.getId()).get();
		assertThat(foundProject.getLinkedSchemas().size(), is(0));
	}
	
	@Test
	public void whenDataOfSchemaIsReplacedItShouldContainTheNewData(){
		final Schema schema = service.addDataAsNewSchema("Test Schema", createModelWithOneStatement());
		final Model before = service.findDataOfSchema(schema).get();
		
		service.replaceDataOfSchema(schema, createModelWithOneOtherStatement());
		final Model after = service.findDataOfSchema(schema).get();
		
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
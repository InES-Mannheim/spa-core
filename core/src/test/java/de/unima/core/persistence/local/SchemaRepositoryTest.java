package de.unima.core.persistence.local;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

import de.unima.core.domain.model.Schema;
import de.unima.core.persistence.local.SchemaRepository;
import de.unima.core.storage.jena.JenaTDBStore;

public class SchemaRepositoryTest {

	private SchemaRepository repository;

	@Before
	public void setUp(){
		repository = new SchemaRepository(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	@Test
	public void whenSchematicDataIsAddedForSchemaThenStoreItInAGraphWithIdOftheSchema(){
		final String id = "http://www.test.de/Schema/1";
		final Schema schema = new Schema(id, "First schema");
		final Optional<String> schemaId = repository.addDataToEntity(schema, createModelWithSomeData());
		
		final Optional<Boolean> schemaGraphIsEmpty = repository.getStore().readWithConnection(Connection -> Connection.as(Dataset.class).map(dataset -> 
			dataset.getNamedModel(id).isEmpty()
		)).get();
		
		assertThat(schemaId.isPresent(), is(true));
		assertThat(schemaId.get(), is(equalTo(id)));
		assertThat(schemaGraphIsEmpty.isPresent(), is(true));
		assertThat(schemaGraphIsEmpty.get(), is(false));
	}
	
	@Test
	public void whenSchemaIsDeletedCorrespondingDataShouldAlsoBeDeleted(){
		final String id = "http://www.test.de/Schema/1";
		final Schema schema = new Schema(id, "First schema");
		repository.addDataToEntity(schema, createModelWithSomeData());
		repository.delete(schema);
		final Optional<Boolean> schemaGraphIsEmpty = repository.getStore().readWithConnection(Connection -> Connection.as(Dataset.class).map(dataset -> 
			dataset.getNamedModel(id).isEmpty()
	    )).get();
		assertThat(schemaGraphIsEmpty.isPresent(), is(true));
		assertThat(schemaGraphIsEmpty.get(), is(true));
	}
	
	@Test
	public void whenSchemaDataShouldBeReadItMustBeInTheReturningModel(){
		final String id = "http://www.test.de/Schema/1";
		final Schema schema = new Schema(id, "First schema");
		repository.addDataToEntity(schema, createModelWithSomeData());
		final Optional<Model> model = repository.findDataOfEntity(schema);
		assertThat(model.isPresent(), is(true));
		assertThat(model.get().size(), is(1l));
		final boolean containsStatement = model.get().contains(ResourceFactory.createResource("http://www.test.de/Test/1"), RDFS.label, "test label");
		assertThat(containsStatement, is(true));
	}

	private Model createModelWithSomeData() {
		final Model model = ModelFactory.createDefaultModel();
		model.createResource("http://www.test.de/Test/1").addProperty(RDFS.label, "test label");
		return model;
	}
}

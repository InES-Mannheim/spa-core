package de.unima.core.persistence;

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

import de.unima.core.storage.Store;
import de.unima.core.storage.jena.JenaTDBStore;

public class PartialDataStoreTest {

	private HouseRepository repository;

	@Before
	public void setUp(){
		repository = new HouseRepository(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	@Test
	public void whenSchematicDataIsAddedForSchemaThenStoreItInAGraphWithIdOftheSchema(){
		final String id = "http://www.test.de/House/1";
		final House house = new House("http://www.test.de/House/1");
		final Optional<String> entityId = repository.addDataToEntity(house, createModelWithSomeData());
		
		final Optional<Boolean> schemaGraphIsEmpty = repository.getStore().readWithConnection(Connection -> Connection.as(Dataset.class).map(dataset -> 
			dataset.getNamedModel(id).isEmpty()
		)).get();
		
		assertThat(entityId.isPresent(), is(true));
		assertThat(entityId.get(), is(equalTo(id)));
		assertThat(schemaGraphIsEmpty.isPresent(), is(true));
		assertThat(schemaGraphIsEmpty.get(), is(false));
	}
	
	@Test
	public void whenSchemaIsDeletedCorrespondingDataShouldAlsoBeDeleted(){
		final String id = "http://www.test.de/House/1";
		final House house = new House(id);
		repository.addDataToEntity(house, createModelWithSomeData());
		repository.delete(house);
		final Optional<Boolean> schemaGraphIsEmpty = repository.getStore().readWithConnection(Connection -> Connection.as(Dataset.class).map(dataset -> 
			dataset.getNamedModel(id).isEmpty()
	    )).get();
		assertThat(schemaGraphIsEmpty.isPresent(), is(true));
		assertThat(schemaGraphIsEmpty.get(), is(true));
	}
	
	@Test
	public void whenSchemaDataShouldBeReadItMustBeInTheReturningModel(){
		final String id = "http://www.test.de/House/1";
		final House entity = new House(id);
		repository.addDataToEntity(entity, createModelWithSomeData());
		final Optional<Model> model = repository.findDataOfEntity(entity);
		assertThat(model.isPresent(), is(true));
		assertThat(model.get().size(), is(1l));
		final boolean containsStatement = model.get().contains(ResourceFactory.createResource("http://www.test.de/Test/1"), RDFS.label, "test label");
		assertThat(containsStatement, is(true));
	}
	
	@Test
	public void whenNamedModelForSchemaIsEmptyThenReturnEmptyOptional(){
		final String id = "http://www.test.de/House/1";
		final House entity = new House(id);
		repository.addDataToEntity(entity, createModelWithSomeData());
		repository.delete(entity);
		final Optional<Model> model = repository.findDataOfEntity(entity);
		assertThat(model.isPresent(), is(false));
	}

	private Model createModelWithSomeData() {
		final Model model = ModelFactory.createDefaultModel();
		model.createResource("http://www.test.de/Test/1").addProperty(RDFS.label, "test label");
		return model;
	}
	
	private static class House extends AbstractEntity<String>{
		public House(String id) {
			super(id);
		}
	}
	
	private static class HouseRepository extends PartialDataStore<House, String>{

		public HouseRepository(Store store) {
			super(store);
		}

		@Override
		protected Class<House> getEntityType() {
			return House.class;
		}

		@Override
		protected String getRdfClass() {
			return "http://www.test.de/House";
		}
		
	}
}

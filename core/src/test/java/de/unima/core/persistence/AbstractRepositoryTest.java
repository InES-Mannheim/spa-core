package de.unima.core.persistence;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.unima.core.storage.Store;
import de.unima.core.storage.jena.JenaTDBStore;

public class AbstractRepositoryTest {
	
	@Rule
	public ExpectedException expected = ExpectedException.none();

	private AbstractRepository<House, String> simpleHouseRepository;
	private HouseRepository houseWithWindowsRepository;

	@Before
	public void setUp(){
		this.simpleHouseRepository = new AbstractRepository<House, String>(JenaTDBStore.withUniqueMemoryLocation()){
			@Override
			protected Class<House> getEntityType() {
				return House.class;
			}

			@Override
			protected String getRdfClass() {
				return "http://www.test.de/House";
			}
		};
		this.houseWithWindowsRepository = new HouseRepository(JenaTDBStore.withUniqueMemoryLocation());
	}
	
	@Test
	public void whenAListOfEntitiesShouldBeSavedTheyShouldBeInTheDataSet(){
		final List<House> houses = create5Houses();
		simpleHouseRepository.saveAll(houses);
		final Optional<Boolean> containsHouses = simpleHouseRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return IntStream.range(0, 5).mapToObj(number -> dataset.getNamedModel("http://www.test.de/House/"+number+"/graph")).allMatch(model -> model.size() > 0);
		})).get();
		assertThat(containsHouses.get(), is(true));
	}
	
	@Test
	public void whenSavingAHouseWithIdAndNameItMustBeInTheDataSet(){
		final String id = "http://www.test.de/House/1";
		final String label = "First house that is saved";
		final House house = new House(id, label);
		
		simpleHouseRepository.save(house);
		
		final Optional<Boolean> containsStatement = simpleHouseRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return dataset.getNamedModel("http://www.test.de/House/1/graph").contains(ResourceFactory.createResource(id), RDFS.label, ResourceFactory.createTypedLiteral(label));
		})).get();
		assertThat(containsStatement.isPresent(), is(true));
		assertThat(containsStatement.get(), is(true));
	}
	
	@Test
	public void whenSavingAHouseItShouldBeSavedAsNamedModel(){
		final String id = "http://www.test.de/House/1";
		final String label = "First house that is saved";
		final House house = new House(id, label);
		
		simpleHouseRepository.save(house);
		
		final Optional<Boolean> containsStatement = simpleRepositoryContainsGraphOfEntity(id);
		assertThat(containsStatement.isPresent(), is(true));
		assertThat(containsStatement.get(), is(true));
	}

	private Optional<Boolean> simpleRepositoryContainsGraphOfEntity(String id) {
		return simpleHouseRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return dataset.containsNamedModel(id+"/graph");
		})).get();
	}
	
	@Test
	public void whenEntityIsSavedExistingModelShouldBeOverriden(){
		final String id = "http://www.test.de/House/1";
		final String label1 = "First house that is saved";
		final String label2 = "Label changed";
		final House house1 = new House(id, label1);
		final House house2 = new House(id, label2);
		
		simpleHouseRepository.save(house1);
		simpleHouseRepository.save(house2);
		
		final Model model = getGraphForEntityFromSimpleRepository(id);
		assertThat(model.size(), is(2l));
		assertThat(model.contains(ResourceFactory.createResource(id), RDFS.label, ResourceFactory.createTypedLiteral(label2)), is(true));
		assertThat(model.contains(ResourceFactory.createResource(id), RDFS.label, ResourceFactory.createTypedLiteral(label1)), is(false));
	}
	
	@Test
	public void whenEntityWithRelationShipIsSavedCorrespondingPropertiesMustBeCreated(){
		final String id = "http://www.test.de/House/1";
		houseWithWindowsRepository.save(new House(id, "First"));
		
		final Model model = houseWithWindowsRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> dataset.getNamedModel(id+"/graph"))).get().get();
		
		assertThat(model.size(), is(4l));
		assertThat(model.contains(ResourceFactory.createResource(id), ResourceFactory.createProperty("http://www.test.de/hasWindow"), ResourceFactory.createResource("http://www.test.de/Window/2")), is(true));
		assertThat(model.contains(ResourceFactory.createResource(id), ResourceFactory.createProperty("http://www.test.de/hasWindow"), ResourceFactory.createResource("http://www.test.de/Window/1")), is(true));
	}
	
	@Test
	public void whenIdIsNotSetThenThrowIllegalArgumentException(){
		expected.expect(IllegalArgumentException.class);
		simpleHouseRepository.save(new House());
	}
	
	@Test
	public void whenEntityIsNullThenThrowNullpointerException(){
		expected.expect(NullPointerException.class);
		simpleHouseRepository.save(null);
	}
	
	@Test
	public void whenEntityShouldBeDeletedCorresponingGraphShouldBeRemoved(){
		final String id = "http://www.test.de/House/1";
		final House house = new House(id);
		simpleHouseRepository.save(house);
		
		final Model beforeModel = getGraphForEntityFromSimpleRepository(id);
		assertThat(beforeModel.contains(ResourceFactory.createResource(id), RDF.type), is(true));
		
		final Long numberOfDeletedStatements = simpleHouseRepository.delete(house);
		
		final Model afterModel = getGraphForEntityFromSimpleRepository(id);
		assertThat(afterModel.contains(ResourceFactory.createResource(id), RDF.type), is(false));
		assertThat(numberOfDeletedStatements, is(1l));
	}
	
	@Test
	public void whenEntityShouldBeDeletedButWasNotSavedTheNumberOfDeletedStatementsShouldBe0(){
		final Long numberOfDeletedStatements = simpleHouseRepository.delete(new House("http://www.test.de/House/1"));
		assertThat(numberOfDeletedStatements, is(0l));
	}
	
	@Test
	public void whenEntityShouldBeDeletedItMustNotBeNull(){
		expected.expect(NullPointerException.class);
		expected.expectMessage(containsString("Entity must not be null."));
		simpleHouseRepository.delete(null);
	}
	
	@Test
	public void whenListOfEntitiesIsDeletedThenCorrespondingNamedGraphsShouldBeEmpty(){
		final List<House> fiveHouses = create5Houses();
		simpleHouseRepository.saveAll(fiveHouses);
		final long numberOfDeletedStatements = simpleHouseRepository.deleteAll(fiveHouses);
		assertThat(numberOfDeletedStatements, is(5l));
		final boolean allNamedModelsEmpty = fiveHouses.stream().map(house -> getGraphForEntityFromSimpleRepository(house.getId())).allMatch(model -> model.size() == 0);
		assertThat(allNamedModelsEmpty, is(true));
	}
	
	private Model getGraphForEntityFromSimpleRepository(final String id) {
		return simpleHouseRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return dataset.getNamedModel(id+"/graph");
		})).get().get();
	}
	
	private List<House> create5Houses() {
		return IntStream.range(0, 5).mapToObj(House::new).collect(Collectors.toList());
	}
	
	private static class HouseRepository extends AbstractRepository<House, String>{
		
		
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
		
		@Override
		protected void adaptTransformation() {
			transformation.with("windows", Window.class).asResources("http://www.test.de/hasWindow", AbstractEntity::getId);
		}
	}
	
	private static class House extends AbstractEntity<String>{
		private final List<Window> windows;
		
		public House(int id){
			this("http://www.test.de/House/"+id);
		}
		
		public House(){
			this(null);
		}

		public House(String id) {
			this(id, null);
		}
		
		public House(String id, String name) {
			super(id, name);
			windows = Lists.newArrayList(new Window("http://www.test.de/Window/1", "First"),new Window("http://www.test.de/Window/2", "Second"));
		}
		
		public List<Window> getWindows() {
			return windows;
		}
	}
	
	private static class Window extends AbstractEntity<String>{
		public Window(String id){
			super(id);
		}
		
		public Window(String id, String name){
			super(id, name);
		}
	}
}

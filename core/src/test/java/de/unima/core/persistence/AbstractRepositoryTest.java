package de.unima.core.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

public class AbstractRepositoryTest {

	private AbstractRepository<House, String> simpleHouseRepository;
	private HouseRepository houseWithWindowsRepository;

	@Before
	public void setUp(){
		this.simpleHouseRepository = new AbstractRepository<House, String>(Optional.<Path>empty()){
			@Override
			protected Class<House> getEntityType() {
				return House.class;
			}

			@Override
			protected String getRdfClass() {
				return "http://www.test.de/House";
			}
		};
		this.houseWithWindowsRepository = new HouseRepository(Optional.<Path>empty());
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
		
		final Optional<Boolean> containsStatement = simpleHouseRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return dataset.containsNamedModel("http://www.test.de/House/1/graph");
		})).get();
		assertThat(containsStatement.isPresent(), is(true));
		assertThat(containsStatement.get(), is(true));
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
		
		final Model model = simpleHouseRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return dataset.getNamedModel("http://www.test.de/House/1/graph");
		})).get().get();
		assertThat(model.size(), is(2l));
		assertThat(model.contains(ResourceFactory.createResource(id), RDFS.label, ResourceFactory.createTypedLiteral(label2)), is(true));
		assertThat(model.contains(ResourceFactory.createResource(id), RDFS.label, ResourceFactory.createTypedLiteral(label1)), is(false));
	}
	
	@Test
	public void whenEntityWithRelationShipIsSavedCorrespondingPropertiesMustBeCreated(){
		String id = "http://www.test.de/House/1";
		houseWithWindowsRepository.save(new House(id, "First"));
		
		final Model model = houseWithWindowsRepository.getStore().readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return dataset.getNamedModel("http://www.test.de/House/1/graph");
		})).get().get();
		assertThat(model.size(), is(4l));
		assertThat(model.contains(ResourceFactory.createResource(id), ResourceFactory.createProperty("http://www.test.de/hasWindow"), ResourceFactory.createResource("http://www.test.de/Window/2")), is(true));
		assertThat(model.contains(ResourceFactory.createResource(id), ResourceFactory.createProperty("http://www.test.de/hasWindow"), ResourceFactory.createResource("http://www.test.de/Window/1")), is(true));
	}
	
	private static class HouseRepository extends AbstractRepository<House, String>{
		
		
		public HouseRepository(Optional<Path> pathToRepository) {
			super(pathToRepository);
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

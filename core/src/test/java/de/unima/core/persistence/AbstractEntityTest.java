package de.unima.core.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.jena.ontology.OntModel;
import org.junit.Before;
import org.junit.Test;

public class AbstractEntityTest {

	private TestEntity entityWithFakeStore;
	private TestEntity entityWithEmptyStore;

	@Before
	public void setUp(){
		entityWithFakeStore = new TestEntity(Store.fake());
		entityWithEmptyStore = new TestEntity(Store.empty());
	}
	
	@Test
	public void whenEntityShouldBeLoadedAndStoreIsFakeThenReturnTrue(){
		final boolean isSaved = entityWithFakeStore.save();
		assertThat(isSaved, is(true));
	}
	
	@Test
	public void itShouldBeSavedAndStoreIsFakeThenReturnTrue(){
		final boolean isLoaded = entityWithFakeStore.load();
		assertThat(isLoaded, is(true));
	}
	
	@Test
	public void itShouldBeLoadedButStoreWasEmptyThenReturnFalse(){
		final boolean isLoaded = entityWithEmptyStore.load();
		assertThat(isLoaded, is(false));
	}
	
	@Test
	public void itShouldBeSavedButStoreWasEmptyThenReturnFalse(){
		final boolean isSaved = entityWithEmptyStore.save();
		assertThat(isSaved, is(false));
	}
	
	private final class TestEntity extends AbstractEntity<String>{
		
		private OntModel data;
		
		public TestEntity(Store<String> store) {
			super(store);
		}

		@Override
		public String getId() {
			return "http://test.de";
		}

		@Override
		public OntModel getData() {
			return data;
		}

		@Override
		protected boolean setData(OntModel data) {
			this.data = data;
			return true;
		}
		
	}
}

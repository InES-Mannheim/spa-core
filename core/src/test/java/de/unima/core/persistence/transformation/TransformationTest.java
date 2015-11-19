package de.unima.core.persistence.transformation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.unima.core.persistence.AbstractEntity;

public class TransformationTest {

	private Transformation<House> housetransformationWithoutId;
	private Transformation<House>.SubjectMapping housetransformationWithId;

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Before
	public void setup(){
		housetransformationWithoutId = Transformation.map(House.class);
		housetransformationWithId = Transformation.map(House.class).to(Vocabulary.House).withId("id").asLiteral(Vocabulary.id);
	}
	
	@Test
	public void withShouldReturnATransformationInstanceWhichIsNotNull(){
		assertThat(housetransformationWithoutId, is(notNullValue()));
	}

	@Test
	public void toShouldReturnAMappingInstanceWhichIsNotNull(){
		final Transformation<House>.SubjectMapping mapping = housetransformationWithoutId.to(Vocabulary.House);
		assertThat(mapping, is(notNullValue()));
	}
	
	@Test
	public void getShouldReturnAFunctionFromHouseToModel(){
		final Function<House, Model> houseToModel = housetransformationWithId.get();
		assertThat(houseToModel, is(notNullValue()));
	} 
	
	@Test
	public void transformerShouldReturnTypeStatement(){
		final Function<House, Model> houseToModel = housetransformationWithId.get();
		final House house = new House();
		final Model model = houseToModel.apply(house);
		assertThat(model, is(notNullValue()));
		assertThat(model.contains(null, RDF.type, ResourceFactory.createResource(Vocabulary.House)), is(true));
	} 
	
	@Test
	public void withShouldReturnNonNullObjectMapping(){
		final Transformation<House>.PredicateAndObjectMapping<String> mapper = housetransformationWithoutId.to(Vocabulary.House).withString("name");
		assertThat(mapper, is(notNullValue()));
	}
	
	@Test
	public void asShouldReturnSubjectMappingAgain(){
		final Transformation<House>.SubjectMapping mapper = housetransformationWithoutId.to(Vocabulary.House).withString("name").asLiteral(Vocabulary.name);
		assertThat(mapper, is(notNullValue()));
	}
	
	@Test
	public void transformerShouldContainNameValueAfterMapping(){
		final Function<House, Model> houseToModel = housetransformationWithId.withString("name").asLiteral(Vocabulary.name).get();
		final House house = new House();
		final Model model = houseToModel.apply(house);
		assertThat(model.contains(null, ResourceFactory.createProperty(Vocabulary.name), "test"), is(true));
	}
	
	@Test
	public void transformerShouldContaiIntegerValueAfterMapping(){
		final Function<House, Model> houseToModel = housetransformationWithId.withInteger("number").asLiteral(Vocabulary.number).get();
		final House house = new House();
		final Model model = houseToModel.apply(house);
		assertThat(model.contains(null, ResourceFactory.createProperty(Vocabulary.number), ResourceFactory.createTypedLiteral(34)), is(true));
	}
	
	@Test
	public void transformerShouldTransformHouseWithLiteralsIntoRdf(){
		final Model model = housetransformationWithId
				.withInteger("number")
				.asLiteral(Vocabulary.number)
				.withString("name")
				.asLiteral(Vocabulary.name)
				.get().apply(new House());
		assertThat(model.contains(null, ResourceFactory.createProperty(Vocabulary.name), "test"), is(true));
		assertThat(model.contains(null, ResourceFactory.createProperty(Vocabulary.number), ResourceFactory.createTypedLiteral(34)), is(true));
	}
	
	@Test
	public void whenIdFieldIsNotSpecifiedThenThrowAnIllegalStateException(){
		expected.expect(IllegalStateException.class);
		housetransformationWithoutId.to(Vocabulary.House).get(); 
	}
	
	@Test
	public void whenIdFieldIsSpecifiedThenCreateTheTransformation(){
		final Function<House, Model> houseToModel = housetransformationWithoutId.to(Vocabulary.House).withId("id").asLiteral(Vocabulary.id).get();
		assertThat(houseToModel.apply(new House()), is(notNullValue()));
	}
	
	@Test
	public void whenIdFieldIsNotNullThenUseValue(){
		final Function<House, Model> houseToModel = housetransformationWithId.get();
		final House house = new House();
		final String id = house.getId();
		final Model model = houseToModel.apply(house);
		final List<String> subjectsInModel = model.listSubjects().toList().stream().map(String::valueOf).collect(Collectors.toList());
		
		assertThat(subjectsInModel, hasItem(id));
	}

	@Test
	public void whenIdFieldIsNullThenThrowIllegalStateException(){
		expected.expect(IllegalStateException.class);
		housetransformationWithoutId.to(Vocabulary.House).withId("nullId").asLiteral(Vocabulary.id).get().apply(new House());
		
	}

	@Test
	public void whenFieldIsNullThenStatementShouldNotBeCreated(){
		final Function<House, Model> houseToModel = housetransformationWithId.withString("nullId").asLiteral(Vocabulary.nullId).get();
		final Model model = houseToModel.apply(new House());
		assertThat(model.contains(null, ResourceFactory.createProperty(Vocabulary.nullId)), is(false));
	}
	
	@Test
	public void whenAFieldShouldBeReadButCannotBeFoundThenThrowAnIllegalStateException(){
		expected.expect(IllegalStateException.class);
		housetransformationWithId.withString("nope").asLiteral(Vocabulary.nullId).get().apply(new House());
	}

	private final static class House extends AbstractEntity<String> {
		
		private String name;
		private int number;
		
		private String nullId;

		public House() {
			this("test",34);
		}
		
		public House(String name, int number) {
			super(Vocabulary.House+"/instance/"+Objects.hash(name, number));
			this.name = name;
			this.number = number;
		}
	}
	
	private final static class Vocabulary{
		final static String NS = "http://test.de/namespace#";
		final static String House = NS + "House";
		final static String name = NS + "name";
		final static String number = NS + "number";
		final static String id = NS + "id";
		final static String nullId = NS + "nullId";
	}
}

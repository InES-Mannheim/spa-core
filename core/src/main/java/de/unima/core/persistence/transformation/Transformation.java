package de.unima.core.persistence.transformation;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.hashids.Hashids;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
/**
 * Describes the transformation between a domain class into the Jena RDF model.
 * 
 * @author Gregor Trefs
 *
 * TODO: Ids
 * TODO: Lists
 */
public class Transformation<T> {

	private Class<T> domainClass;

	private Transformation(Class<T> domainClass) {
		this.domainClass = domainClass;
	}

	public static <T> Transformation<T> map(Class<T> domainClass) {
		return new Transformation<T>(domainClass);
	}

	public SubjectMapping to(String rdfClass) {
		return new SubjectMapping(domainClass, rdfClass);
	}

	public class SubjectMapping {

		private final Class<T> domainClass;
		private final String rdfClass;
		private final List<Function<T, Function<Resource, List<Statement>>>> partialTransformers;
		private Optional<String> idField = Optional.empty(); 

		public SubjectMapping(Class<T> domainClass, String rdfClass) {
			this.domainClass = domainClass;
			this.rdfClass = rdfClass;
			this.partialTransformers = new LinkedList<>();
		}

		public Function<T, Model> get() {
			checkIdIsSet();
			return domainClass -> {
				final Model model = ModelFactory.createDefaultModel();
				final Resource type = createResource(rdfClass);
				final Resource subject = model.createResource(createInstanceUriForRdfClass(rdfClass), type);
				final List<Statement> statements = partialTransformers.stream()
						.parallel()
						.collect(() -> new LinkedList<Statement>(), 
								(list, partialTransformer) -> list.addAll(partialTransformer.apply(domainClass).apply(subject)), 
								(first, second) -> first.addAll(second));
				model.add(statements);
				return model;
			};
		}

		private void checkIdIsSet() {
			idField.orElseThrow(() -> new IllegalStateException("Transformation cannot be created as no field of the domain class is defined as id."));
		}

		private String createInstanceUriForRdfClass(String rdfClass) {
			final Hashids hashids = new Hashids(domainClass.getName());
			final Random random = new Random();
			return addSlashIfItDoesNotEndWithHashOrSlash(rdfClass) + hashids.encode(Math.abs(random.nextInt()));
		}

		private String addSlashIfItDoesNotEndWithHashOrSlash(String rdfClass) {
			boolean endsWithSlashOrHash = rdfClass.endsWith("/") || rdfClass.endsWith("#");
			return endsWithSlashOrHash ? rdfClass : rdfClass + "/";
		}

		void addPartialTransformaer(Function<T, Function<Resource, List<Statement>>> partialTransformator) {
			partialTransformers.add(partialTransformator);
		}
		
		public PredicateAndObjectMapping<String> withId(String fieldName){
			this.idField = Optional.of(fieldName);
			return withString(fieldName);
		}
		
		public PredicateAndObjectMapping<String> withString(String fieldName) {
			return with(fieldName, String.class);
		}

		public PredicateAndObjectMapping<Integer> withInteger(String fieldName) {
			return with(fieldName, Integer.class);
		}
		
		public <S> PredicateAndObjectMapping<S> with(String fieldName, Class<S> fieldType){
			return new PredicateAndObjectMapping<>(this, fieldName, fieldType);
		}
	}

	public class PredicateAndObjectMapping<S> {

		private final SubjectMapping subjectMapping;
		private final Class<S> fieldType;
		private final String domainClassFieldName;

		public PredicateAndObjectMapping(SubjectMapping subjectMapping, String domainClassFieldName,
				Class<S> fieldType) {
			this.subjectMapping = subjectMapping;
			this.fieldType = fieldType;
			this.domainClassFieldName = domainClassFieldName;
		}

		public Transformation<T>.SubjectMapping asLiteral(String predicate) {
			subjectMapping.addPartialTransformaer(domainClass -> subject -> { 
				return ImmutableList.of((createStatement(subject, createProperty(predicate), createTypedLiteral(readField(domainClass)))));
			});
			return subjectMapping;
		}

		private S readField(T domainClass) {
			try {
				final Object value = FieldUtils.readField(domainClass, domainClassFieldName, true);
				checkArgument(fieldType.isInstance(value), "Field " + domainClassFieldName + " of "
						+ domainClass.getClass().getName() + " cannot be read as " + fieldType.getName() + ". "
						+ "Please check if you selected the correct field type while building this transformation or if the field is null.");
				return fieldType.cast(value);
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}

	}
}

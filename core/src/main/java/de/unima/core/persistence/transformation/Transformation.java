package de.unima.core.persistence.transformation;

import static de.unima.core.persistence.transformation.Transformation.TransformationSupport.readFieldAsType;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import com.google.common.base.Throwables;

/**
 * Transformation DSL to transform Java objects and into RDF.
 */
public class Transformation<T> {

	/**
	 * Creates a new {@code Transformation} for the given type.
	 * 
	 * @param clazz
	 *            whose instances should be transformed
	 * @return {@code Transformation} for given type
	 */
	public static <T> Transformation<T> map(Class<T> clazz) {
		return new Transformation<T>();
	}

	/**
	 * Creates a new {@code SubjectMapping} to the given rdf class
	 * 
	 * @param rdfClass as stringified URI (e.g. "http://www.namespace.com/Test").
	 */
	public SubjectMapping to(String rdfClass) {
		return new SubjectMapping(rdfClass);
	}

	/**
	 * Mapping of a Java instance to an RDF subject.
	 *
	 */
	public class SubjectMapping {

		private final String rdfClass;
		private final List<Function<T, Function<Resource, List<Statement>>>> partialTransformers;

		private Optional<String> idField = Optional.empty();

		public SubjectMapping(String rdfClass) {
			this.rdfClass = rdfClass;
			this.partialTransformers = new LinkedList<>();
		}

		/**
		 * Returns function for transforming an supplied instance of
		 * a Java class into an RDF graph.
		 * 
		 * @return function transforming a class into a graph
		 */
		public Function<T, Model> get() {
			checkIdFieldIsSpecified();
			return instance -> {
				final Model graph = ModelFactory.createDefaultModel();
				final Resource rdfType = createResource(rdfClass);
				final Resource subject = graph.createResource(readId(instance), rdfType);
				final List<Statement> statements = partialTransformers.stream().parallel()
						.collect(() -> new LinkedList<Statement>(),
								(list, partialTransformer) -> list
										.addAll(partialTransformer.apply(instance).apply(subject)),
						(first, second) -> first.addAll(second));
				graph.add(statements);
				return graph;
			};
		}

		private void checkIdFieldIsSpecified() {
			idField.orElseThrow(() -> new IllegalStateException(
					"Transformation cannot be created because no field of the class is defined as id."));
		}

		private String readId(T transformationInstance) {
			return readFieldAsType(transformationInstance, idField.get(), String.class).orElseThrow(
					() -> new IllegalStateException(String.format("Id field '%s' must not be null.", idField.get())));
		}

		void addPartialTransformer(Function<T, Function<Resource, List<Statement>>> partialTransformator) {
			partialTransformers.add(partialTransformator);
		}

		/**
		 * Specify the field of the Java class which contains the stringified Id as URI.
		 * 
		 * @param fieldName which contains the id value
		 * @return {@code PredicateAndObjectMapping} for building the graph
		 */
		public PredicateAndObjectMapping<String> withId(String fieldName) {
			this.idField = Optional.of(fieldName);
			return withString(fieldName);
		}

		/**
		 * Specify a Java class field of type string to be mapped to RDF.
		 * 
		 * @param fieldName which contains a string value
		 * @return {@code PredicateAndObjectMapping} for building the graph
		 */
		public PredicateAndObjectMapping<String> withString(String fieldName) {
			return with(fieldName, String.class);
		}

		/**
		 * Specify a Java class field of type integer to be mapped to RDF.
		 * 
		 * @param fieldName which contains a integer value
		 * @return {@code PredicateAndObjectMapping} for building the graph
		 */
		public PredicateAndObjectMapping<Integer> withInteger(String fieldName) {
			return with(fieldName, Integer.class);
		}

		/**
		 * Specify a Java class field of given type to be mapped to RDF.
		 * 
		 * @param fieldName which contains the value
		 * @param fieldType of the field
		 * @return {@code PredicateAndObjectMapping} for building the graph
		 */
		public <S> PredicateAndObjectMapping<S> with(String fieldName, Class<S> fieldType) {
			return new PredicateAndObjectMapping<>(this, fieldName, fieldType);
		}
	}

	/**
	 * Mapping between a field of a Java instance to predicates and objects
	 *
	 * @param <S> type of the field
	 */
	public class PredicateAndObjectMapping<S> {

		private final SubjectMapping caller;
		private final Class<S> fieldType;
		private final String fieldName;

		public PredicateAndObjectMapping(SubjectMapping caller, String fieldName, Class<S> fieldType) {
			this.caller = caller;
			this.fieldType = fieldType;
			this.fieldName = fieldName;
		}

		/**
		 * Map the field to a literal with given predicate.
		 * 
		 * @param predicate to reference the literal
		 * @return {@code SubjectMapping} for building the graph
		 */
		public Transformation<T>.SubjectMapping asLiteral(String predicate) {
			caller.addPartialTransformer(
					type -> subject -> readFieldAsType(type, fieldName, fieldType)
							.map(fieldValue -> Collections.singletonList((createStatement(subject,
									createProperty(predicate), createTypedLiteral(fieldValue)))))
					.orElse(Collections.emptyList()));
			return caller;
		}

	}

	static class TransformationSupport {
		static <R> Optional<R> readFieldAsType(Object target, String name, Class<R> type) {
			try {
				final Object value = FieldUtils.readField(target, name, true);
				return Optional.ofNullable(type.cast(value));
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			} catch (ClassCastException e) {
				final String message = String.format(
						"Field %s of %s cannot be read as %s. "
								+ "Please check if you provided the correct field type while building this transformation.",
						name, target.getClass().getName(), type.getName());
				throw new IllegalStateException(message, e);
			} catch (IllegalArgumentException e) {
				final String message = String.format(
						"%s does not have field %s. Make sure that the name of the field is spelled properly.",
						target.getClass().getName(), name);
				throw new IllegalStateException(message, e);
			}
		}
	}
}

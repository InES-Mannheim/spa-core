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

import static de.unima.core.persistence.Transformation.TransformationSupport.readFieldAsType;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import com.google.common.base.Throwables;

/**
 * Transformation DSL to translate Java instances into RDF.
 * 
 * @param <T> instance type
 */
public class Transformation<T> {

	/**
	 * Creates a new {@code Transformation} for the given type.
	 * 
	 * @param clazz
	 *            whose instances should be transformed
	 * @param <T>
	 *            type of the class
	 * @return {@code Transformation} for given type
	 */
	public static <T> Transformation<T> map(Class<T> clazz) {
		return new Transformation<T>();
	}

	/**
	 * Creates a new {@code SubjectMapping} to the given RDF class
	 * 
	 * @param rdfClass as stringified URI (e.g. "http://www.namespace.com/Test").
	 * @return subject mapping DSL 
	 */
	public SubjectMapping to(String rdfClass) {
		return new SubjectMapping(rdfClass);
	}

	/**
	 * Mapping of a Java instance to an RDF subject.
	 */
	public class SubjectMapping {

		private final String rdfClass;
		private final List<Function<T, Function<Resource, List<Statement>>>> partialTransformers;

		private Optional<String> idField = Optional.empty();
		private Function<Model, T> constructor;

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
					"Transformation cannot be created because id field is undefined."));
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
		 * @return {@code SubjectMapping} for building the graph
		 */
		public SubjectMapping withId(String fieldName) {
			this.idField = Optional.of(fieldName);
			return this;
		}

		/**
		 * Specify a Java class field of type String to be mapped to RDF.
		 * 
		 * @param fieldName which contains a string value
		 * @return {@code PredicateAndObjectMapping} for building the graph
		 */
		public PredicateAndObjectMapping<String> withString(String fieldName) {
			return with(fieldName, String.class);
		}

		/**
		 * Specify a Java class field of type Integer to be mapped to RDF.
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
		 * @param <S> type of the field
		 * @return {@code PredicateAndObjectMapping} for building the graph
		 */
		public <S> PredicateAndObjectMapping<S> with(String fieldName, Class<S> fieldType) {
			return new PredicateAndObjectMapping<>(this, fieldName, fieldType);
		}
		
		/**
		 * Uses the provided constructor function to create instances from RDF.
		 * 
		 * @param constructor which is used to create instances from RDF
		 */
		public void createEntityWith(Function<Model, T> constructor) {
			this.constructor = constructor;
		}

		/**
		 * Returns a constructor for creating entities from a model.
		 * 
		 * @return constructor function if defined; empty otherwise
		 */
		public Optional<Function<Model, T>> inverse() {
			return Optional.ofNullable(constructor);
		}
        
	}

	/**
	 * Mapping between a field of a Java instance to predicates and objects.
	 * 
	 * If a field 'test' should be converted, a getter named
	 * 'getTest' is tried to be invoked first. If such a getter is not found,
	 * the field is accessed directly. If this also fails, an
	 * {@code IllegalStateException} is thrown.
	 *
	 * @param <S>
	 *            type of the field
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
		 * Maps the field to a literal with given predicate.
		 * 
		 * @param predicate to reference the literal
		 * @return {@code SubjectMapping} for building the graph
		 */
		public Transformation<T>.SubjectMapping asLiteral(String predicate) {
			caller.addPartialTransformer(
					type -> subject -> readFieldAsType(type, fieldName, fieldType)
							.map(fieldValue -> Collections.singletonList((createStatement(subject,
									createProperty(predicate), 
									createTypedLiteral(fieldValue)))))
							.orElse(Collections.emptyList()));
			return caller;
		}
		
		/**
		 * Reads the field as collection of members with type {@code S}.
		 * 
		 * The {@code memberToResource} function is used to generate the actual
		 * Uris from the collection members. For each converted resource, a
		 * statement from the subject to the resource with given predicate is
		 * added.
		 * 
		 * @param predicate used in the resulting RDF statements
		 * @param memberToResource maps members of the collection to resources
		 * @return {@code SubjectMapping} for building the graph
		 */
		@SuppressWarnings("unchecked")
		public Transformation<T>.SubjectMapping asResources(String predicate, Function<S, String> memberToResource) {
			caller.addPartialTransformer(
					type -> subject -> readFieldAsType(type, fieldName, Collection.class)
					        .map(collection -> ((Collection<S>) collection).stream() // S is assumed to be the type of the members in the collection
					        			.map(memberToResource)
										.map(resource -> createStatement(subject, createProperty(predicate), createResource(resource)))
										.collect(Collectors.toList()))
					        .orElse(Collections.emptyList()));
			return caller;
		}
		
		/**
		 * Maps the field to a resource with given predicate.
		 * 
		 * @param predicate to reference the resource
		 * @param fieldToResource - how to derive a URI from value of the field
		 * @return {@code SubjectMapping} for building the graph
		 */
		public Transformation<T>.SubjectMapping asResource(String predicate, Function<S, String> fieldToResource){
			caller.addPartialTransformer(
					type -> subject -> readFieldAsType(type, fieldName, fieldType)
					        .map(fieldValue -> Collections.singletonList(createStatement(subject, 
					        		createProperty(predicate), 
					        		createResource(fieldToResource.apply(fieldValue)))))
					        .orElse(Collections.emptyList()));
			return caller;
		}

	}

	static class TransformationSupport {
		static <R> Optional<R> readFieldAsType(Object target, String name, Class<R> type) {
			try {
				final Method accessibleMethod = MethodUtils.getAccessibleMethod(target.getClass(), getGetterName(name));
				final Object value = Optional.ofNullable(accessibleMethod).map(method -> {
					return invokeWithoutException(target, method);
				}).orElseGet(() -> readFieldWithoutException(target, name));
				return Optional.ofNullable(type.cast(value));
			} catch (ClassCastException e) {
				final String message = String.format(
						"Field %s of %s cannot be read as %s. "
								+ "Please check if you provided the correct field type while building this transformation.",
						name, target.getClass().getName(), type.getName());
				throw new IllegalStateException(message, e);
			} catch (IllegalArgumentException e) {
				final String message = String.format(
						"%s does not have getter for field %s nor is it directly accessible. Make sure that the name of the field is spelled properly.",
						target.getClass().getName(), name);
				throw new IllegalStateException(message, e);
			}
		}
		
		static String getGetterName(String fieldName) {
			return "get"+WordUtils.capitalize(fieldName);
		}
		
		static Object invokeWithoutException(Object target, Method method) {
			try {
				return method.invoke(target);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		}
		
		static Object readFieldWithoutException(Object target, String name) {
			try {
				return FieldUtils.readField(target, name, true);
			} catch (IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}
	}
	
}

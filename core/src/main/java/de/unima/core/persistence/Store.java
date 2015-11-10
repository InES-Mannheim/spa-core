package de.unima.core.persistence;

import java.util.Optional;

import org.apache.jena.ontology.OntModel;

/**
 * Abstraction over the underlying triple store.
 * 
 * @param <T> id type
 */
public interface Store<T> {

	public boolean save(Entity<T> entity);

	public Optional<OntModel> load(Entity<T> entity);

	/**
	 * Store implementation which pretends to store given entity but
	 * does nothing.
	 * 
	 * @return fake store implementation
	 */
	public static <T> Store<T> fake() {
		return new EMPTY<T>();
	}
	
	final static class EMPTY<T extends Object> implements Store<T> {
		@Override
		public boolean save(Entity<T> entity) {
			return true;
		}
		@Override
		public Optional<OntModel> load(Entity<T> entity) {
			return Optional.empty();
		}
		
	}

}

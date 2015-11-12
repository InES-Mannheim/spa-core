package de.unima.core.persistence;

import java.util.Optional;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;

/**
 * Abstraction over the underlying triple store.
 * 
 * @param <T> id type
 */
public interface Store<T> {

	public boolean save(Entity<T> entity);

	public Optional<OntModel> load(Entity<T> entity);

	/**
	 * Store implementation which stores everything and returns anything.
	 * 
	 * @return fake store implementation
	 */
	public static <T> Store<T> fake() {
		return new Store<T>() {
			@Override
			public boolean save(Entity<T> entity) {
				return true;
			}
			@Override
			public Optional<OntModel> load(Entity<T> entity) {
				return Optional.of(new OntModelImpl(OntModelSpec.OWL_DL_MEM));
			}
			
		};
	}
	
	/**
	 * Store implementation which stores nothing and returns nothing.
	 * 
	 * @return fake store implementation
	 */
	public static <T> Store<T> empty() {
		return new Store<T>(){
			@Override
			public boolean save(Entity<T> entity) {
				return false;
			}

			@Override
			public Optional<OntModel> load(Entity<T> entity) {
				return Optional.empty();
			}
		};
	}
}

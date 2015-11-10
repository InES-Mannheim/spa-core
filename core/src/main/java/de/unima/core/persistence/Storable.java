package de.unima.core.persistence;

import org.apache.jena.ontology.OntModel;

/**
 * Active Graph pattern for RDF.
 *
 * @param <T> id type (e.g. {@code String}, ...)
 */
public interface Storable<T> {
	
	/**
	 * Load this {@code Storable} from the {@code Store}.
	 * 
	 * @return true if loading was successful; false otherwise
	 * @see #getData()
	 */
	public boolean load();
	
	/**
	 * Save this {@code Storeable} in the {@code Store}.
	 * 
	 * @return true if saving was successful; false otherwise
	 */
	public boolean save();
	
	/**
	 * Get the data as {@link OntModel}.
	 * 
	 * @return {@code OntModel} containing entity information.
	 */
	public OntModel getData();
}

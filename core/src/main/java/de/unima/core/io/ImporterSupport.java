package de.unima.core.io;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;

/**
 * Support interface for importers.
 *
 */
public interface ImporterSupport {

	/**
	 * Adds a new importer.
	 * 
	 * @param importer which should be added
	 * @param key for the importer 
	 * @return key of the new importer
	 */
	Key addImporter(Importer<?, ?> importer, String key);
	
	/**
	 * Removes importer.
	 * 
	 * @param key of the importer
	 * @param <T> input format
	 * @param <R> output format
	 * @return removed importer or empty if not found
	 */
	<T,R extends Model> Optional<Importer<T,R>> removeImporter(Key key);
	
	/**
	 * Finds importer.
	 * 
	 * @param key of the importer
	 * @param <T> input format
	 * @param <R> output format
	 * @return found importer or empty otherwise
	 */
	<T,R extends Model> Optional<Importer<T,R>> findImporterByKey(Key key);
	
	/**
	 * Determines if there is an importer with given key
	 * 
	 * @param key
	 *            which should be checked
	 * @return true if key is present, false otherwise
	 */
	default boolean containsKey(String key){
		return listKeysAsString().contains(key);
	}
	
	/**
	 * Lists all keys.
	 * 
	 * @return list of keys as String
	 */
	List<String> listKeysAsString();
}

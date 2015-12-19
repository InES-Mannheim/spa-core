package de.unima.core.io;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;

/**
 * Support interface for exporters.
 * @param <T>
 *
 */
public interface ExporterSupport<R, T extends Model, S extends Exporter<T,R>> {

	/**
	 * Adds a new exporter.
	 * 
	 * @param exporter which should be added
	 * @param key for the exporter 
	 * @return key of the new exporter
	 */
	Key addExporter(S exporter, String key);
	
	/**
	 * Removes exporter.
	 * 
	 * @param key of the exporter
	 * @return removed exporter or empty if not found
	 */
	Optional<S> removeExporter(Key key);
	
	/**
	 * Finds exporter.
	 * 
	 * @param key of the exporter
	 * @return found exporter or empty otherwise
	 */
	Optional<S> findExporterByKey(Key key);
	
	/**
	 * Determines if there is an exporter with given key
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

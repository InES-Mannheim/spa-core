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
	 * @param key TODO
	 * @param T source type
	 * @return key of the new importer
	 */
	Key addImporter(Importer<?, ?> importer, String key);
	
	/**
	 * Removes importer.
	 * 
	 * @param id of the importer
	 * @param T source type
	 * @return removed importer or empty if not found
	 */
	<T,R extends Model> Optional<Importer<T,R>> removeImporter(Key key);
	
	/**
	 * Finds importer.
	 * 
	 * @param id of the importer
	 * @param T source type
	 * @return found importer or empty otherwise
	 */
	<T,R extends Model> Optional<Importer<T,R>> findImporterById(Key id);
	
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
	
	public static final class Key {
		
		private final String key;
		
		public Key(String key) {
			this.key = key;
		}
		
		public static Key of(String id){
			return new Key(id);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) return false;
			if(! obj.getClass().isAssignableFrom(Key.class)) return false;
			return key.equals(((Key) obj).key);
		}
		
		@Override
		public int hashCode() {
			return key.hashCode();
		}
		
		@Override
		public String toString() {
			return key;
		}
	}
}

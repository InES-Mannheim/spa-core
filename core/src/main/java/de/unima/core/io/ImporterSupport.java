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

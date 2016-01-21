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

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;

public class AnyImporterSupport implements ImporterSupport {

	private final HashMap<Key, Importer<?,?>> importers = new HashMap<>();
	
	@Override
	public Key addImporter(Importer<?,?> importer, String key) {
		final Key keyOfImporter = new Key(key);
		this.importers.put(keyOfImporter, importer);
		return keyOfImporter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends Model> Optional<Importer<T,R>> removeImporter(Key key) {
		return Optional.ofNullable((Importer<T, R>) importers.remove(key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, R extends Model> Optional<Importer<T,R>> findImporterByKey(Key key) {
		return Optional.ofNullable((Importer<T,R>) importers.get(key));
	}

	@Override
	public List<String> listKeysAsString() {
		final List<String> keysAsString = Lists.newArrayList();
		for(Key key: importers.keySet()){
			keysAsString.add(key.toString());
		}
		return keysAsString;
	}

}

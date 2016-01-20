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
package de.unima.core.io.file;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;

import de.unima.core.io.ExporterSupport;
import de.unima.core.io.Key;

public class FileBasedExporterSupport implements ExporterSupport<File, Model, FileBasedExporter<Model>> {

	private final HashMap<Key, FileBasedExporter<Model>> exporters = new HashMap<>();
	
	@Override
	public Key addExporter(FileBasedExporter<Model> exporter, String key) {
		final Key keyOfExporter = new Key(key);
		this.exporters.put(keyOfExporter, exporter);
		return keyOfExporter;
	}

	@Override
	public Optional<FileBasedExporter<Model>> removeExporter(Key key) {
		return Optional.ofNullable(exporters.remove(key));
	}

	@Override
	public Optional<FileBasedExporter<Model>> findExporterByKey(Key key) {
		return Optional.ofNullable(exporters.get(key));
	}

	@Override
	public List<String> listKeysAsString() {
		final List<String> keysAsString = Lists.newArrayList();
		for(Key key: exporters.keySet()){
			keysAsString.add(key.toString());
		}
		return keysAsString;
	}

}

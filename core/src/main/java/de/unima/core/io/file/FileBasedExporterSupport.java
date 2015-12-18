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

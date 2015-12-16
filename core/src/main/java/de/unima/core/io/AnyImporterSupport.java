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

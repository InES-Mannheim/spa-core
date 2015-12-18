package de.unima.core.io.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.jena.rdf.model.Model;

import com.google.common.base.Throwables;

import de.unima.core.io.Exporter;

public interface FileBasedExporter<T extends Model> extends Exporter<T, File> {
	
	@Override
	public default File export(T model) {
		try {
			final File file = Files.createTempFile("exported", null).toFile();
			exportToFile(model, file);
			return file;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	
	File exportToFile(T data, File location);
}

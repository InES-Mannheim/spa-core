package de.unima.core.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.jena.rdf.model.Model;

import com.google.common.base.Throwables;

public class RdfExporter implements FileBasedExporter<Model>{

	@Override
	public File export(Model model) {
		try {
			final File file = Files.createTempFile("exported-jena-model", null).toFile();
			exportToFile(model, file);
			return file;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public File exportToFile(Model data, File location) {
		try(FileOutputStream fos = new FileOutputStream(location)){
			data.write(fos);
			return location;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

}

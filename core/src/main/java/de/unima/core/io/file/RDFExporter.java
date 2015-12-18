package de.unima.core.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import com.google.common.base.Throwables;

public class RDFExporter implements FileBasedExporter<Model>{

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

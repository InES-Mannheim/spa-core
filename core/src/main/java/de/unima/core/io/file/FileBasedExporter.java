package de.unima.core.io.file;

import java.io.File;

import org.apache.jena.rdf.model.Model;

import de.unima.core.io.Exporter;

public interface FileBasedExporter<T extends Model> extends Exporter<T, File> {
	File exportToFile(T data, File location);
}

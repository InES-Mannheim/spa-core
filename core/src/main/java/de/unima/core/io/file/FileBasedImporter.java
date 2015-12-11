package de.unima.core.io.file;

import java.io.File;

import org.apache.jena.rdf.model.Model;

import de.unima.core.io.Importer;

/**
 * Base interface for file based {@link Importer}. 
 */
public interface FileBasedImporter<R extends Model> extends Importer<File, R> {
}

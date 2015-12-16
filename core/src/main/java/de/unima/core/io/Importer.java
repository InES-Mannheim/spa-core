package de.unima.core.io;

import org.apache.jena.rdf.model.Model;

/**
 * {@code Importer} provides means to convert data into RDF.
 * 
 * @param <T> source of the importer
 */
public interface Importer<T,R extends Model> {
	public R importData(T dataSource);
}

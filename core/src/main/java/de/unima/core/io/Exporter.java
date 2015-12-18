package de.unima.core.io;

import org.apache.jena.rdf.model.Model;

/**
 * Provides means to export data.
 * 
 * @param <T> RDF model data.
 * @param <R> data format.
 */
public interface Exporter<T extends Model,R> {
	public R export(T model);
}

package de.unima.core.io;

import org.apache.jena.ontology.OntModel;

/**
 * {@code Importer} provides means to convert data into RDF.
 * 
 * @param <T> source of the importer
 */
public interface Importer<T> {
	public OntModel importData(T dataSource);
	public String getID();
}

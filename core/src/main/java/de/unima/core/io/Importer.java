package de.unima.core.io;

import org.apache.jena.ontology.OntModel;

public interface Importer<T> {
	public OntModel importData(T dataSource);
	public String getID();
}

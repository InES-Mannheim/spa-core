package de.unima.core.io;

import org.apache.jena.ontology.OntModel;

public interface Importer<S extends DataSource> {
	public OntModel importData(S ds);
	public String getID();
}

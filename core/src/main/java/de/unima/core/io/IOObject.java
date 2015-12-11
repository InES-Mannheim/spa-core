package de.unima.core.io;

import org.apache.jena.ontology.OntModel;

public interface IOObject<S extends DataSource> {
	public OntModel getData();
	public void setExporter(Exporter<S> e);
}
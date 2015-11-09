package de.unima.core.persistence;

import org.apache.jena.ontology.OntModel;

import de.unima.core.io.Importer;

public interface Storable {
	
	public boolean load();
	public boolean store();
	public String getID();
	public OntModel getData();
	public void setStore(Store s);
}

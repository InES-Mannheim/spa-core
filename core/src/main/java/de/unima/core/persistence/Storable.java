package de.unima.core.persistence;

import org.apache.jena.ontology.OntModel;

public interface Storable {
	
	public boolean load();
	public boolean store();
	public String getID();
	public OntModel getData();
	public void setStore(Store s);
}

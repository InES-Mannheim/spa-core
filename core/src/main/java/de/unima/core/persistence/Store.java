package de.unima.core.persistence;

import org.apache.jena.ontology.OntModel;

public interface Store {
	
	public boolean commit(String id, OntModel data);
	public OntModel update(String id);

}

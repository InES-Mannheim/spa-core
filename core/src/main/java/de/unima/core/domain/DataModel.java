package de.unima.core.domain;

import org.apache.jena.ontology.OntModel;

import de.unima.core.persistence.Storable;

public interface DataModel extends Storable {
	
	@Override
	public OntModel getData();

}

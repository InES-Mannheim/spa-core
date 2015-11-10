package de.unima.core.domain;

import org.apache.jena.ontology.OntModel;

import de.unima.core.persistence.Entity;

public interface DataModel extends Entity<String> {
	public OntModel getData();
}

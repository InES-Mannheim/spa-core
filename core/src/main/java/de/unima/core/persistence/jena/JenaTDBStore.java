package de.unima.core.persistence.jena;

import java.util.Optional;

import org.apache.jena.ontology.OntModel;

import de.unima.core.persistence.Entity;
import de.unima.core.persistence.Store;

public class JenaTDBStore implements Store<String> {

	@Override
	public boolean save(Entity<String> entity) {
		return false;
	}

	@Override
	public Optional<OntModel> load(Entity<String> entity) {
		return Optional.empty();
	}

}

package de.unima.core.persistence.impl;

import org.apache.jena.ontology.OntModel;

import de.unima.core.persistence.Store;

public class TDBStoreConnectorImpl implements Store {

	@Override
	public boolean commit(String id, OntModel data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OntModel update(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}

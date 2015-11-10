package de.unima.core.domain.impl;

import org.apache.jena.ontology.OntModel;

import de.unima.core.domain.DataModel;
import de.unima.core.persistence.AbstractEntity;
import de.unima.core.persistence.Store;

public class DataModelImpl extends AbstractEntity<String> implements DataModel {
	
	private String id;
	private OntModel data;

	public DataModelImpl(String id, OntModel data, Store<String> store) {
		super(store);
		this.id = id;
		this.data = data;
	}

	@Override
	public OntModel getData() {
		return this.data;
	}

	@Override
	protected boolean setData(OntModel d) {
		this.data = d;
		return true;
	}

	@Override
	public String getId() {
		return this.id;
	}

}

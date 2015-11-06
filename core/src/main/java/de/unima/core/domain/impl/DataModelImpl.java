package de.unima.core.domain.impl;

import org.apache.jena.ontology.OntModel;

import de.unima.core.domain.DataModel;

public class DataModelImpl implements DataModel {
	
	private String id;
	private OntModel data;
	

	public DataModelImpl(String i, OntModel d) {
		
		this.id = i;
		this.data = d;
	}


	@Override
	public OntModel getData() {
	
		return this.data;
	}

}

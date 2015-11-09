package de.unima.core.domain.impl;

import org.apache.jena.ontology.OntModel;

import de.unima.core.domain.DataModel;
import de.unima.core.persistence.impl.AbstractStorable;

public class DataModelImpl extends AbstractStorable implements DataModel {
	
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


	@Override
	protected void setData(OntModel d) {
		
		this.data = d;
	}


	@Override
	public String getID() {
		
		return this.id;
	}

}

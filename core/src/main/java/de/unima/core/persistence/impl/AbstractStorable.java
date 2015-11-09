package de.unima.core.persistence.impl;

import org.apache.jena.ontology.OntModel;

import de.unima.core.persistence.Storable;
import de.unima.core.persistence.Store;

public abstract class AbstractStorable implements Storable {
	
	private Store store;

	@Override
	public boolean store() {
		
		if (this.store==null) {
			
			return true;
			
		} else {
			
			return this.store.commit(this.getID(), this.getData());
		}
	}
	
	@Override
	public boolean load() {
		
		if (this.store==null) {
			
			return true;
		}
		
		OntModel d = this.store.update(this.getID());
	
		if (d==null) {
			
			return false;
			
		} else {
			
			this.setData(d);
			return true;
		}
	}
	
	@Override
	public void setStore(Store s) {
		
		this.store = s;
	}
	
	abstract public OntModel getData();
	
	abstract protected void setData(OntModel d);
	
	abstract public String getID();

}

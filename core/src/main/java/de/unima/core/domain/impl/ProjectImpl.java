package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.unima.core.domain.Project;
import de.unima.core.domain.DataPool;


public class ProjectImpl implements Project {
	
	private Set<String> schemeIDs;
	private Map<String, DataPool> dataPools;
	
	public ProjectImpl(Set<String> schemeIDs) {
		
		this.schemeIDs = schemeIDs;	
		this.dataPools = new HashMap<String, DataPool>();
	}

	public Set<String> getSchemeIDs() {

		return this.schemeIDs;
	}

	public Set<String> getDataPoolIDs() {
		
		return this.dataPools.keySet();
	}


	public DataPool getDataPool(String id) {
		
		if (!this.dataPools.containsKey(id)) {
	
			return null;
		
		} else {
		
		  DataPool ds = this.dataPools.get(id);
		  ds.loadDataPool();
		  return ds;
		}
	}


	public void createDataPool(String id) {
		
		DataPool ds = new DataPoolImpl(id, this);
		this.dataPools.put(id, ds);
	} 

	
    public boolean isValid(DataPool dataPool) {
	  // TODO Auto-generated method stub
	  // Implement a consistency check of the data store via reasoner
	  return true;
	}


}

package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.DataPool;


public class ProjectImpl implements Project {
	
	private Set<String> schemeIDs;
	private Map<String, DataPool> dataPools;
	private Repository repository;
	
	public ProjectImpl(String sID, Repository r) {
		
		this.schemeIDs = new HashSet<String>();
		this.schemeIDs.add(sID);
		this.dataPools = new HashMap<String, DataPool>();
		this.repository = r;
	}

	@Override
	public Set<String> getSchemeIDs() {

		return this.schemeIDs;
	}

	@Override
	public Set<String> getDataPoolIDs() {
		
		return this.dataPools.keySet();
	}

	@Override
	public DataPool getDataPool(String id) {
		
		if (!this.dataPools.containsKey(id)) {
	
			return null;
		
		} else {
		
		  DataPool ds = this.dataPools.get(id);
		  ds.updateDataPool();
		  return ds;
		}
	}

	@Override
	public void createDataPool(String id) {
		
		DataPool ds = new DataPoolImpl(id, this);
		this.dataPools.put(id, ds);
	}
	
	@Override
	public void linkScheme(String sID) {
		
		this.schemeIDs.add(sID);
	}
	
	@Override
	public void unlinkScheme(String sID) {
		
		this.schemeIDs.remove(sID);
	}

	@Override
	public Repository getRepository() {
		
		return this.repository;
	}

	
}

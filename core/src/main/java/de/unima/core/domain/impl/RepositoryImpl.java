package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;
import de.unima.core.io.Importer;
import de.unima.core.io.RDFFile;
import de.unima.core.persistence.Store;

public class RepositoryImpl implements Repository {

	private Map<String, Project> projects;
	private Map<String, DataScheme> dataschemes;
	
	public RepositoryImpl() {
		
		this.dataschemes = new HashMap<String, DataScheme>();
		this.projects = new HashMap<String, Project>();
	}

	@Override
	public boolean createProject(String id, String schemeID) {
		
		if (this.projects.containsKey(id) & !this.dataschemes.keySet().contains(schemeID)) {
			return false;
		} else {
			this.projects.put(id, new ProjectImpl(schemeID, this));
			return true;
		}
	}

	@Override
	public Project getProject(String id) {

		return this.projects.get(id);
	}

	@Override
	public Set<String> getProjectIDs() {
		
		return this.projects.keySet();
	}

	@Override
	public boolean registerDataScheme(String i, IOObject<RDFFile> ioo, Importer<? extends DataSource> imp) {

		if (this.dataschemes.containsKey(i)) {

			return false;
			
		} else {
			
			DataScheme newDataScheme = new DataSchemeImpl(i, ioo.getData(), this, imp, Store.fake());
	
			if (!newDataScheme.save()) {
				
				System.err.println("Unable to store scheme " + i + " while registration.");
				return false;
				
			} else {
				
				this.dataschemes.put(i, newDataScheme);
				return true;
			}
		}
		
	}


	@Override
	public DataScheme getDataScheme(String id) {

		return this.dataschemes.get(id);
	}

}

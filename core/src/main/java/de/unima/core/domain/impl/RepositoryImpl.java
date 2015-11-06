package de.unima.core.domain.impl;

import java.util.Map;
import java.util.Set;

import de.unima.core.domain.impl.DataSchemeImpl;
import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;
import de.unima.core.io.Importer;
import de.unima.core.io.RDFFile;

public class RepositoryImpl implements Repository {

	private Map<String, Project> projects;
	private Map<String, DataScheme> schemes;


	public boolean createProject(String id, Set<String> schemeIDs) {
		
		if (this.projects.containsKey(id) & !this.schemes.keySet().containsAll(schemeIDs)) {
			return false;
		} else {
			this.projects.put(id, new ProjectImpl(schemeIDs));
			return true;
		}
	}


	public Project getProject(String id) {

		return this.projects.get(id);
	}


	public Set<String> getProjectIDs() {
		
		return this.projects.keySet();
	}


	public boolean registerDataScheme(String i, IOObject<RDFFile> ioo, Importer<DataSource> imp) {

		if (this.schemes.containsKey(i)) {

			return false;
			
		} else {
			
			DataScheme newDataScheme = new DataSchemeImpl(i, ioo.getData(), this, imp);
	
			if (!newDataScheme.store()) {
				
				System.err.println("Storing of scheme " + i + " fails while registration.");
				return false;
				
			} else {
				
				this.schemes.put(i, newDataScheme);
				return true;
			}
		}
		
	}

}

package de.unima.core.domain;

import java.util.Set;

import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;
import de.unima.core.io.Importer;
import de.unima.core.io.RDFFile;

public interface Repository {
	
	public boolean createProject(String id, String schemeID);
	public Project getProject(String id);
	public DataScheme getDataScheme(String id);
	public Set<String> getProjectIDs();
	public boolean registerDataScheme(String id, IOObject<RDFFile> ioo, Importer<? extends DataSource> i);
}

package de.unima.core.domain;

import java.util.Set;

public interface Project {

	public Set<String> getSchemeIDs();
	public Set<String> getDataPoolIDs();
	public Repository getRepository();
	public DataPool getDataPool(String id);
	public void createDataPool(String id);
	void linkScheme(String sID);
	void unlinkScheme(String sID);
}

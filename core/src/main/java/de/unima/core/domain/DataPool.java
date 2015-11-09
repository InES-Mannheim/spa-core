package de.unima.core.domain;

import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;

public interface DataPool {
	
	public boolean addDataModel(String id, IOObject<? extends DataSource> ioo);
	public boolean isValid();
	public boolean updateDataPool();
}

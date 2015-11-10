package de.unima.core.domain;

import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;

public interface DataPool {
	
	boolean addDataModel(String id, IOObject<? extends DataSource> ioo);
	boolean isValid();
	boolean updateDataPool();
}

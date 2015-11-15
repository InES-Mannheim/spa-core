package de.unima.core.domain.impl;

import de.unima.core.domain.DataModel;
import de.unima.core.persistence.AbstractEntity;

public class DataModelImpl extends AbstractEntity<String> implements DataModel {

	public DataModelImpl(String id) {
		super(id);
	}

}

package de.unima.core.domain.impl;

import java.util.Map;

import org.apache.jena.ext.com.google.common.collect.Maps;

import de.unima.core.domain.DataModel;
import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.persistence.AbstractEntity;

public class DataPoolImpl extends AbstractEntity<String> implements DataPool {

	private final Project project;
	private final Map<String, DataModel> datamodels;

	public DataPoolImpl(String id, Project projectOfDataPool) {
		super(id);
		this.datamodels = Maps.newHashMap();
		this.project = projectOfDataPool;
	}

	@Override
	public boolean addDataModel(DataModel model) {
		datamodels.put(model.getId(), model);
		return true;
	}

	@Override
	public boolean removeDataModel(DataModel model) {
		datamodels.remove(model.getId());
		return true;
	}
	
	@Override
	public Project getProject() {
		return project;
	}
}

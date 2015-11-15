package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;

import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.persistence.AbstractEntity;


public class ProjectImpl extends AbstractEntity<String> implements Project {
	
	private Map<String, Schema> schemas;
	private Map<String, DataPool> dataPools;
	private Repository repository;
	
	public ProjectImpl(String id, Repository repository) {
		super(id);
		this.schemas = new HashMap<>();
		this.dataPools = new HashMap<>();
		this.repository = repository;
	}

	@Override
	public Set<Schema> getSchemas() {
		return Sets.newHashSet(schemas.values());
	}

	@Override
	public Set<DataPool> getDataPools() {
		return Sets.newHashSet(dataPools.values());
	}

	@Override
	public Optional<DataPool> findDataPoolById(String id) {
		return Optional.of(dataPools.get(id));
	}

	@Override
	public void createDataPool(String id) {
		dataPools.put(id, new DataPoolImpl(id, this));
	}
	
	@Override
	public Repository getRepository() {
		return repository;
	}

	@Override
	public Optional<Schema> findSchemaById(String id) {
		return Optional.of(schemas.get(id));
	}

	@Override
	public boolean addSchema(Schema schema) {
		schemas.put(schema.getId(), schema);
		return true;
	}

	@Override
	public boolean removeSchema(Schema schema) {
		return schemas.remove(schema.getId()) != null;
	}

	
}

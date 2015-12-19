package de.unima.core.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.Maps;

import com.google.common.collect.ImmutableList;

/**
 * A project combines {@link Schema}s and {@link DataPool}s.
 */
public class Project extends AbstractEntity<String>{
	
	private final Repository repository;
	private final Map<String, DataPool> datapools;
	private final Map<String, Schema> schemas;
	
	public Project(String id){
		this(id, null, null);
	}
	
	public Project(String id, String label, Repository repository) {
		this(id, label, repository, Collections.emptyList(), Collections.emptyList());
	}
	
	public Project(String id, String label, Repository repository, List<DataPool> dataPools, List<Schema> schemas){
		super(id, label);
		this.repository = repository;
		this.datapools = Maps.newHashMap();
		for(DataPool pool:dataPools){
			this.datapools.put(pool.getId(), pool);
		}
		this.schemas = Maps.newHashMap();
		for(Schema schema:schemas){
			this.schemas.put(schema.getId(), schema);
		}
	}

	/**
	 * Returns the repository this project belongs to.
	 * 
	 * @return {@link Repository} of this project
	 */
	public Repository getRepository(){
		return repository;
	}
	
	/**
	 * Adds a new {@link DataPool}.
	 * 
	 * @param dataPool which should be added
	 * @return true if successful; false otherwise
	 */
	public boolean addDataPool(DataPool dataPool){
		datapools.put(dataPool.getId(), dataPool);
		return true;
	}
	
	/**
	 * Replaces current data pools with given data pools.
	 * 
	 * @param datapools which should replace current data pools
	 * @return data pools which have been removed from the project
	 */
	public List<DataPool> replaceDataPools(List<DataPool> datapools){
		final List<DataPool> removedPools = this.datapools.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
		this.datapools.clear();
		datapools.forEach(datapool -> this.datapools.put(datapool.getId(), datapool));
		removedPools.removeAll(datapools);
		return removedPools;
	}
	
	/**
	 * Returns {@code DataPool}s which belong to this project.
	 * 
	 * @return All {@code DataPool}s
	 */
	public List<DataPool> getDataPools(){
		return ImmutableList.<DataPool>builder().addAll(datapools.values()).build();
	}
	
	/**
	 * Finds the {@code DataPool} with given id.
	 * 
	 * @param id of the pool
	 * @return the pool if found; empty otherwise
	 */
	public Optional<DataPool> findDataPoolById(String id){
		return Optional.ofNullable(datapools.get(id));
	}
	
	/**
	 * Removes and returns the {@code DataPool} with given id.
	 * 
	 * @param id of the pool
	 * @return the pool if found; empty otherwise
	 */
	public Optional<DataPool> removeDataPoolById(String id){
		return Optional.ofNullable(datapools.remove(id));
	}
	
	/**
	 * Removes all {@code DataPool}s from this project.
	 * 
	 * @return removed {@code DataPool}s
	 */
	public List<DataPool> removeAllDataPools(){
		return datapools.keySet().stream().map(datapools::remove).collect(Collectors.toList());
	}
	
	/**
	 * Returns all schemas which are linked to 
	 * 
	 * @return all linked schemas
	 */
	public List<Schema> getLinkedSchemas(){
		return ImmutableList.<Schema>builder().addAll(schemas.values()).build();
	}
	
	/**
	 * Is given schema with given id linked?
	 * 
	 * @param id of the schema
	 * @return true if schema is linked, false otherwise 
	 */
	public boolean isSchemaLinked(String id){
		return findLinkedSchemaById(id).isPresent();
	}
	
	/**
	 * Finds the schema with given id.
	 * 
	 * @param id of the schema
	 * @return the schema; empty otherwise
	 */
	public Optional<Schema> findLinkedSchemaById(String id){
		return Optional.ofNullable(schemas.get(id));
	}
	
	/**
	 * Adds given schema to this project.
	 * 
	 * @param schema the new schema
	 * @return true if successful; false otherwise 
	 */
	public boolean linkSchema(Schema schema){
		schemas.put(schema.getId(), schema);
		return true;
	}
	
	/**
	 * Unlinks given schema from this project.
	 * 
	 * @param id of the schema which should be unlinked
	 * @return unlinked Schema if successful; empty otherwise
	 */
	public Optional<Schema> unlinkSchema(String id){
		return Optional.ofNullable(schemas.remove(id));
	}
	
	/**
	 * Unlinks all schemas.
	 * 
	 * @return all previously linked schemas
	 */
	public List<Schema> unlinkAllSchemas(){
		final List<Schema> ret = schemas.entrySet().stream().map(Entry::getValue).collect(Collectors.toList());
		schemas.clear();
		return ret;
	}

	/**
	 * Replaces current linked schemas with given schemas.
	 * 
	 * @param schemas which should replace current schemas
	 * @return schemas which have been unlinked from the project
	 */
	public List<Schema> replaceLinkedSchemas(List<Schema> schemas) {
		final List<Schema> unlinkedSchemas = this.schemas.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
		this.schemas.clear();
		schemas.forEach(schema -> this.schemas.put(schema.getId(), schema));
		unlinkedSchemas.removeAll(schemas);
		return unlinkedSchemas;
	}
}

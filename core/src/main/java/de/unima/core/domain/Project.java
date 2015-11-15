package de.unima.core.domain;

import java.util.Optional;
import java.util.Set;

import de.unima.core.persistence.Entity;

/**
 * A project combines {@link Schema}s and {@link DataPool}s. 
 */
public interface Project extends Entity<String>{
	
	/**
	 * Returns the repository this project belongs to.
	 * 
	 * @return {@link Repository} of this project
	 */
	Repository getRepository();
	
	/**
	 * Creates a new {@link DataPool}.
	 * 
	 * @param id of the new pool
	 */
	void createDataPool(String id);
	
	/**
	 * Returns {@code DataPool}s which belong to this project.
	 * 
	 * @return data pools
	 */
	Set<DataPool> getDataPools();
	
	/**
	 * Finds the {@code DataPool} with given id.
	 * @param id of the pool
	 * @return the pool if found; empty otherwise
	 */
	Optional<DataPool> findDataPoolById(String id);
	
	/**
	 * Returns all schemas which are linked to 
	 * @return
	 */
	Set<Schema> getSchemas();
	
	/**
	 * Finds the {@code Schema} with given id.
	 * 
	 * @param id of the schema
	 * @return the schema; empty otherwise
	 */
	Optional<Schema> findSchemaById(String id);
	
	/**
	 * Adds given schema to this project.
	 * 
	 * @param schema the new schema
	 * @return true if successful; false otherwise 
	 */
	boolean addSchema(Schema schema);
	
	/**
	 * Removes given schema from this project.
	 * 
	 * @param schema which should be removed
	 * @return true if successful; false otherwise
	 */
	boolean removeSchema(Schema schema);
}

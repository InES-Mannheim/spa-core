package de.unima.core.domain;

import de.unima.core.persistence.Entity;

/**
 * Graph for multiple {@link DataModel}s.
 */
public interface DataPool extends Entity<String>{
	
	/**
	 * Adds given {@link DataModel} to this pool.
	 * 
	 * @param model which should be added
	 * @return true if successful; false otherwise
	 */
	boolean addDataModel(DataModel model);
	
	/**
	 * Removes given {@link DataModel} from this pool.
	 * 
	 * @param model
	 * @return true if successful; false otherwise
	 */
	boolean removeDataModel(DataModel model);
	
	/**
	 * Returns the project this pool belongs to.
	 * @return project of this pool
	 */
	Project getProject();
}
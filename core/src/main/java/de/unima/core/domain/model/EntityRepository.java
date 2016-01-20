/*******************************************************************************
 *    Copyright 2016 University of Mannheim
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.unima.core.domain.model;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Entity}s. 
 *
 * @param <T> type with lower type bound of {@code Entity}
 * @param <R> type of the Entity Id
 */
public interface EntityRepository<T extends Entity<R>, R> {

	/**
	 * Saves all entities.
	 * 
	 * @param entities which should be persisted
	 * @return list with id of each saved entity
	 */
	public List<R> saveAll(List<T> entities);
	
	/**
	 * Saves entity.
	 * 
	 * @param entity
	 *            which should be persisted
	 * @return id of the entity if successful; false otherwise
	 */
	public Optional<R> save(T entity);

	/**
	 * Finds all entities.
	 * 
	 * @return list of entities
	 */
	public List<T> findAll();

	/**
	 * Finds entity with given id.
	 * 
	 * @param id
	 *            of the entity
	 * @return the entity; empty otherwise
	 */
	public Optional<T> findById(R id);

	/**
	 * Deletes all entities.
	 * 
	 * @param entities which data should be deleted
	 * 
	 * @return number of deleted statements; empty if not found
	 */
	public long deleteAll(List<T> entities);

	/**
	 * Deletes entity.
	 * 
	 * @param entity
	 *            which should be deleted
	 * @return number of deleted statements; empty if not found
	 */
	public long delete(T entity);
}

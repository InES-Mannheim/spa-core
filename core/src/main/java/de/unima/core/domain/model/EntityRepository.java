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

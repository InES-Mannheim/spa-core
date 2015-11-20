package de.unima.core.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Repository for a number of {@link Entity}s. 
 *
 * @param <T> type with lower type bound of {@code Entity}
 * @param <R> type of the Entity Id
 */
public interface Repository<T extends Entity<R>, R> {

	/**
	 * Saves entity.
	 * 
	 * If given entity is already known to the system only changes will be
	 * persisted.
	 * 
	 * @param entity
	 *            should should be persisted
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
	 * @return number of deleted statements; empty if not found
	 */
	public Optional<Integer> deleteAll();

	/**
	 * Deletes entity identified by given id.
	 * 
	 * @param id
	 *            of the entity
	 * @return number of deleted statements; empty if not found
	 */
	public Optional<Integer> deleteById(R id);
}

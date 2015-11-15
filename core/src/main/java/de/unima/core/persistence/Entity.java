package de.unima.core.persistence;

/**
 * Entity (cf. Domain Driven Design) is a thing with an ID.
 */
public interface Entity<T> {
	
	/**
	 * The id of this entity.
	 * 
	 * @return id of the entity
	 */
	public T getId();
}

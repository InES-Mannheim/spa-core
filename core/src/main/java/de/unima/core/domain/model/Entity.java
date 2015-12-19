package de.unima.core.domain.model;

/**
 * Entity (cf. Domain Driven Design) is a thing with an ID.
 * 
 * @param <T> id type 
 */
public interface Entity<T> {
	
	/**
	 * The id of this entity.
	 * 
	 * @return id of the entity
	 */
	public T getId();
	
	
	/**
	 * Sets the label of this entity.
	 * 
	 * @param label of this entity
	 */
	public void setLabel(String label);
	
	/**
	 * Gets the label of this entity.
	 * 
	 * @return label of this entity.
	 */
	public String getLabel();
}

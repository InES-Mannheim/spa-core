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
	
	
	/**
	 * Sets the label of this entity.
	 * 
	 * @return label of this entity
	 */
	public void setLabel(String label);
	
	/**
	 * The label of this entity.
	 * 
	 * @return label of this entity.
	 */
	public String getLabel();
}

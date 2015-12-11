package de.unima.core.domain.model;

import org.apache.jena.ext.com.google.common.base.Objects;

public abstract class AbstractEntity<T> implements Entity<T> {
	
	protected T id;
	private String label;
	
	public AbstractEntity(T id) {
		this(id, null);
	}
	
	public AbstractEntity(T id, String label) {
		this.id = id;
		this.setLabel(label);
	}
	
	@Override
	public T getId() {
		return id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Entity)) return false;
		return Objects.equal(this.getId(), ((Entity<?>) obj).getId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getId());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" ["+id+"]";
	}
	
}

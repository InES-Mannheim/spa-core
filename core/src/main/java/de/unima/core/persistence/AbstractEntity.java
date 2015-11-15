package de.unima.core.persistence;

import org.apache.jena.ext.com.google.common.base.Objects;

public abstract class AbstractEntity<T> implements Entity<T> {
	
	protected T id;
	
	public AbstractEntity(T id) {
		this.id = id;
	}
	
	@Override
	public T getId() {
		return id;
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
	
}

package de.unima.core.persistence;

import org.apache.jena.ext.com.google.common.base.Objects;
import org.apache.jena.ontology.OntModel;

public abstract class AbstractEntity<T> implements Entity<T> {

	private final Store<T> store;
	
	public AbstractEntity(Store<T> store){
		this.store = store;
	}

	@Override
	public boolean save() {
		return store.save(this);
	}
	
	@Override
	public boolean load() {
		return store.load(this).map(this::setData).orElse(false);
	}
	
	protected abstract boolean setData(OntModel data);
	
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

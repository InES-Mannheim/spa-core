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

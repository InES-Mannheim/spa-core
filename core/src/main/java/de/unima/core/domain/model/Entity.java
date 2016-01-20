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

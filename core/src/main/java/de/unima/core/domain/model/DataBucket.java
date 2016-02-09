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
 * A data bucket contains the actual data.
 * 
 * All {@code DataBucket}s make up a complete {@link DataPool}.
 * Likewise, a {@code DataBucket} must adhere to the project {@link Schema}s.
 */
public class DataBucket extends AbstractEntity<String> {
	
	public DataBucket(String id) {
		super(id);
	}
	
	public DataBucket(String id, String label) {
		super(id, label);
	}

}
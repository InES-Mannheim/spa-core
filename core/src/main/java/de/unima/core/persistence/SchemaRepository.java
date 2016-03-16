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
package de.unima.core.persistence;

import de.unima.core.domain.model.Schema;
import de.unima.core.storage.Store;

public class SchemaRepository extends PartialDataStore<Schema, String> {

	public SchemaRepository(Store store) {
		super(store);
	}

	@Override
	protected Class<Schema> getEntityType() {
		return Schema.class;
	}

	@Override
	protected String getRdfClass() {
		return Vocabulary.Schema;
	}
}
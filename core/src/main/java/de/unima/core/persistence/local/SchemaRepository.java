package de.unima.core.persistence.local;

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

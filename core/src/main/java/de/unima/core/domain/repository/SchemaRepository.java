package de.unima.core.domain.repository;

import de.unima.core.domain.Schema;
import de.unima.core.domain.Vocabulary;
import de.unima.core.persistence.PartialDataStore;
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

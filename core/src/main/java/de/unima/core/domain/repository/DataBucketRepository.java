package de.unima.core.domain.repository;

import de.unima.core.domain.DataBucket;
import de.unima.core.domain.Vocabulary;
import de.unima.core.persistence.PartialDataStore;
import de.unima.core.storage.Store;

public class DataBucketRepository extends PartialDataStore<DataBucket, String> {

	public DataBucketRepository(Store store) {
		super(store);
	}

	@Override
	protected Class<DataBucket> getEntityType() {
		return DataBucket.class;
	}

	@Override
	protected String getRdfClass() {
		return Vocabulary.DataBucket;
	}

}

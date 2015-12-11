package de.unima.core.persistence.local;

import de.unima.core.domain.model.DataBucket;
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

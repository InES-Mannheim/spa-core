package de.unima.core.domain.repository;

import de.unima.core.domain.DataBucket;
import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.domain.Vocabulary;
import de.unima.core.persistence.AbstractRepository;
import de.unima.core.storage.Store;

public class DataPoolRepository extends AbstractRepository<DataPool, String>{

	public DataPoolRepository(Store store) {
		super(store);
	}
	
	@Override
	protected void adaptTransformation() {
		transformation.with("dataBuckets", DataBucket.class)
			.asResources(Vocabulary.containsDataBucket, DataBucket::getId)
			.with("project", Project.class)
			.asResource(Vocabulary.belongsToProject, Project::getId);
	}

	@Override
	protected Class<DataPool> getEntityType() {
		return DataPool.class;
	}

	@Override
	protected String getRdfClass() {
		return Vocabulary.DataPool;
	}

}

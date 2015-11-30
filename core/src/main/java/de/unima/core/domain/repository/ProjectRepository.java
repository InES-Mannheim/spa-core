package de.unima.core.domain.repository;

import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.domain.Vocabulary;
import de.unima.core.persistence.AbstractRepository;
import de.unima.core.storage.Store;

public class ProjectRepository extends AbstractRepository<Project, String>{

	public ProjectRepository(Store store) {
		super(store);
	}
	
	@Override
	protected void adaptTransformation() {
		transformation.with("dataPools", DataPool.class)
			.asResources(Vocabulary.containsDataPool, dataPool -> dataPool.getId())
			.with("linkedSchemas", Schema.class)
			.asResources(Vocabulary.linksSchema, schema -> schema.getId())
			.with("repository", Repository.class)
			.asResource(Vocabulary.belongsToRepository, repository -> repository.getId());
	}

	@Override
	protected Class<Project> getEntityType() {
		return Project.class;
	}

	@Override
	protected String getRdfClass() {
		return Vocabulary.Project;
	}

}

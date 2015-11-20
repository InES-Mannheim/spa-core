package de.unima.core.domain.repository;

import java.nio.file.Path;
import java.util.Optional;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.Schema;
import de.unima.core.domain.Vocabulary;
import de.unima.core.persistence.AbstractRepository;

public class RepositoryRepository extends AbstractRepository<Repository, String> {

	public RepositoryRepository(Optional<Path> pathToRepository) {
		super(pathToRepository);
		addProjectsAndSchemasToRdfTransformation();
	}

	private void addProjectsAndSchemasToRdfTransformation() {
		transformation.with("projects", Project.class).asResources(Vocabulary.containsProject, Project::getId)
					  .with("schemas", Schema.class).asResources(Vocabulary.containsSchema, Schema::getId);
	}

	@Override
	protected Class<Repository> getEntityType() {
		return Repository.class;
	}

	@Override
	protected String getRdfClass() {
		return Vocabulary.Repository;
	}

}

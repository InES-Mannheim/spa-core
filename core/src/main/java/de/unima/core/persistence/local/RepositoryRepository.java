package de.unima.core.persistence.local;

import java.util.List;
import java.util.function.Function;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResourceFactory;

import com.google.common.collect.Lists;

import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Repository;
import de.unima.core.domain.model.Schema;
import de.unima.core.storage.Store;

public class RepositoryRepository extends AbstractEntityRepository<Repository, String> {

	public RepositoryRepository(Store store) {
		super(store);
		addProjectsAndSchemasToRdfTransformation();
	}

	private void addProjectsAndSchemasToRdfTransformation() {
		transformation.with("projects", Project.class).asResources(Vocabulary.containsProject, Project::getId)
					  .with("schemas", Schema.class).asResources(Vocabulary.containsSchema, Schema::getId);
		
	}
	@Override
	protected Function<Model, List<?>> additionalConstructorArguments() {
		return model -> {
			final List<Object> constructorArguments = Lists.newArrayList();
			constructorArguments.add(extractProjects(model));
			constructorArguments.add(extractSchemas(model));
			return constructorArguments;
		};
	}
	
	private static List<Project> extractProjects(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.containsProject));
		final List<Project> projects = Lists.newArrayList();
		while(objects.hasNext()){
			projects.add(new Project(objects.next().asResource().toString()));
		}
		return projects;
	}
	
	private static List<Schema> extractSchemas(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.containsSchema));
		final List<Schema> schemas = Lists.newArrayList();
		while(objects.hasNext()){
			schemas.add(new Schema(objects.next().asResource().toString()));
		}
		return schemas;
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

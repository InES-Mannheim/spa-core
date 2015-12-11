package de.unima.core.persistence.local;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResourceFactory;

import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Repository;
import de.unima.core.domain.model.Schema;
import de.unima.core.storage.Store;

public class ProjectRepository extends AbstractEntityRepository<Project, String>{

	public ProjectRepository(Store store) {
		super(store);
	}
	
	@Override
	protected void adaptTransformationToRdf() {
		transformation.with("dataPools", DataPool.class)
			.asResources(Vocabulary.containsDataPool, dataPool -> dataPool.getId())
			.with("linkedSchemas", Schema.class)
			.asResources(Vocabulary.linksSchema, schema -> schema.getId())
			.with("repository", Repository.class)
			.asResource(Vocabulary.belongsToRepository, repository -> repository.getId());
	}
	
	@Override
	protected Function<Model, List<?>> additionalConstructorArguments() {
		return model -> {
			final List<Object> constructorArguments = Lists.newArrayList();
			constructorArguments.addAll(extractRepository(model));
			constructorArguments.add(extractDataPools(model));
			constructorArguments.add(extractSchemas(model));
			return constructorArguments;
		};
	}

	private static List<Repository> extractRepository(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.belongsToRepository));
		final Optional<String> repoId = objects.hasNext()?Optional.ofNullable(objects.next().asResource().toString()):Optional.empty();
		return repoId.map(Repository::new).map(Lists::newArrayList).orElseGet(Lists::newArrayList);
	}
	
	private static List<DataPool> extractDataPools(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.containsDataPool));
		final List<DataPool> datapools = Lists.newArrayList();
		while(objects.hasNext()){
			datapools.add(new DataPool(objects.next().asResource().toString()));
		}
		return datapools;
	}
	
	private static List<Schema> extractSchemas(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.linksSchema));
		final List<Schema> schemas = Lists.newArrayList();
		while(objects.hasNext()){
			schemas.add(new Schema(objects.next().asResource().toString()));
		}
		return schemas;
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

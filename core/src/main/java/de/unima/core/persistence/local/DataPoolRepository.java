package de.unima.core.persistence.local;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResourceFactory;

import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.storage.Store;

public class DataPoolRepository extends AbstractEntityRepository<DataPool, String>{

	public DataPoolRepository(Store store) {
		super(store);
	}
	
	@Override
	protected void adaptTransformationToRdf() {
		transformation.with("dataBuckets", DataBucket.class)
			.asResources(Vocabulary.containsDataBucket, DataBucket::getId)
			.with("project", Project.class)
			.asResource(Vocabulary.belongsToProject, Project::getId);
	}
	
	@Override
	protected Function<Model, List<?>> additionalConstructorArguments() {
		return model -> {
			final List<Object> constructorArguments = Lists.newArrayList();
			constructorArguments.addAll(extractProject(model));
			constructorArguments.add(extractDataBuckets(model));
			return constructorArguments;
		};
	}

	private static List<Project> extractProject(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.belongsToProject));
		final Optional<String> projectId = objects.hasNext()?Optional.ofNullable(objects.next().asResource().toString()):Optional.empty();
		return projectId.map(Project::new).map(Lists::newArrayList).orElseGet(Lists::newArrayList);
	}
	
	private static List<DataBucket> extractDataBuckets(Model model) {
		final NodeIterator objects = model.listObjectsOfProperty(ResourceFactory.createProperty(Vocabulary.containsDataBucket));
		final List<DataBucket> databuckets = Lists.newArrayList();
		while(objects.hasNext()){
			databuckets.add(new DataBucket(objects.next().asResource().toString()));
		}
		return databuckets;
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

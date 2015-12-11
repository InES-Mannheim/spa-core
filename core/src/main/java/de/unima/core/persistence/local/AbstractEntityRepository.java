package de.unima.core.persistence.local;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

import com.google.common.collect.Lists;

import de.unima.core.domain.model.Entity;
import de.unima.core.domain.model.EntityRepository;
import de.unima.core.storage.Store;

/**
 * Abstract implementation of {@link EntityRepository} providing common {@code CRUD} functionality. 
 *
 * @param <T> type of the {@link Entity}
 * @param <R> type of the {@code id}
 */
public abstract class AbstractEntityRepository<T extends Entity<R>, R> implements EntityRepository<T, R> {
	
	protected final Store store;
	
	protected final Transformation<T>.SubjectMapping transformation;
	
	protected final Function<Model, Optional<Resource>> extractId = model -> {
		final ResIterator subjects = model.listSubjects();
		return subjects.hasNext() ? Optional.ofNullable(subjects.next()) : Optional.empty(); 
	};
	
	protected final Function<Model, Optional<String>> extractLabel = model -> {
		final NodeIterator objects = model.listObjectsOfProperty(RDFS.label);
		return objects.hasNext() ? Optional.ofNullable(objects.next().asLiteral().getString()) : Optional.empty();
	};
	
	public AbstractEntityRepository(Store store) {
		this.store = store;
		this.transformation = Transformation.map(getEntityType())
				.to(getRdfClass())
				.withId("id")
				.withString("label")
				.asLiteral(RDFS.label.toString());
		adaptTransformationToRdf();
		transformation.createEntityWith(createInstanceConstructor().apply(getEntityType()));
	}
	
	protected abstract Class<T> getEntityType();
	
	protected abstract String getRdfClass();
	
	protected void adaptTransformationToRdf(){}
	
	private final Function<Class<T>, Function<Model, T>> createInstanceConstructor(){
		return type -> model -> {
			final ArrayList<Object> constructorArguments = extractId.apply(model)
					.map(id -> extractLabel.apply(model)
						.map(label -> Lists.<Object>newArrayList(id.toString(), label))
						.orElseGet(() -> Lists.<Object>newArrayList(id.toString())))
					.orElseGet(() -> new ArrayList<>());
			constructorArguments.addAll(additionalConstructorArguments().apply(model));
			return instantiateWithArguments(type, constructorArguments.toArray()); 
		};
	}
	
	protected Function<Model, List<? extends Object>> additionalConstructorArguments(){
		return model -> Collections.emptyList();
	}
	
	private static <R> R instantiateWithArguments(Class<R> type, Object... arguments){
		try {
			return ConstructorUtils.invokeConstructor(type, arguments);
		} catch (Exception e){
				throw new IllegalStateException(String.format("Could not instaniate type '%s' with arguments %s.", 
						type.getName(), 
						Arrays.toString(arguments)), e);
		}
	}
	
	@Override
	public List<R> saveAll(List<T> entities) {
		entities.forEach(this::checkEntityToBeNotNullAndHasIdSet);
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return entities.stream().map(entity -> saveEntityInDataSetAsNamedModel(entity, dataset)).collect(Collectors.toList());
		})).get().get();
	}

	@Override
	public Optional<R> save(T entity) {
		checkEntityToBeNotNullAndHasIdSet(entity);
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return saveEntityInDataSetAsNamedModel(entity, dataset);
		})).get();
	}
	
	private R saveEntityInDataSetAsNamedModel(T entity, Dataset dataset){
		final String graphId = generateGraphId(entity);
		dataset.addNamedModel(graphId, transformation.get().apply(entity));
		return entity.getId();
	}

	@Override
	public List<T> findAll() {
		return store.readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return Lists.newArrayList(dataset.listNames())
					.stream()
					.filter(this::isNamedGraphOfThisEntityType)
					.map(name -> findByIdWithoutTransaction(name, dataset))
					.collect(Collectors.toList());
		})).get().get();
	}
	
	private boolean isNamedGraphOfThisEntityType(String graphId){
		return graphId.startsWith(getRdfClass()); 
	}

	@Override
	public Optional<T> findById(R id) {
		checkNotNull(id, "Could not find entity as id is null.");
		return store.readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return findByIdWithoutTransaction(generateGraphId(id), dataset);
		})).get();
	}

	private T findByIdWithoutTransaction(String graphId, Dataset dataset) {
		final Model model = dataset.getNamedModel(graphId);
		if(model.isEmpty()) return null;
		return transformation.inverse().get().apply(model);
	}

	@Override
	public long deleteAll(List<T> entities) {
		entities.forEach(this::checkEntityToBeNotNullAndHasIdSet);
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> 
			entities.stream().mapToLong(entity -> deleteGraphsOfEntity(entity, dataset)).sum()
		)).get().get();
	}

	@Override
	public long delete(T entity) {
		checkEntityToBeNotNullAndHasIdSet(entity);
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			return deleteGraphsOfEntity(entity, dataset);
		})).get().get();
	}

	protected final void checkEntityToBeNotNullAndHasIdSet(T entity) {
		checkNotNull(entity, "Entity must not be null.");
		checkArgument(entity.getId() != null,
				String.format("Id of %s is null. Please set the Id as Uri.", entity.getClass().getSimpleName()));
	}
	
	private Long deleteGraphsOfEntity(T entity, Dataset dataset) {
		return deleteGeneratedGraph(dataset, entity) + deleteGraphNamedLikeEntity(dataset, entity);
	}
	
	private Long deleteGeneratedGraph(Dataset dataset, T entity) {
		final String graphUri = generateGraphId(entity);
		return deleteNamedGraph(dataset, graphUri);
	}
	
	private String generateGraphId(T entity){
		return generateGraphId(entity.getId());
	}
	
	private String generateGraphId(R id) {
		final String stringId = id.toString();
		return stringId.endsWith("/") ? stringId + "graph" : stringId + "/graph";
	}

	private Long deleteGraphNamedLikeEntity(Dataset dataset, T entity) {
		return deleteNamedGraph(dataset, entity.getId().toString());
	}

	private Long deleteNamedGraph(Dataset dataset, final String graphUri) {
		return Optional.ofNullable(dataset.getNamedModel(graphUri)).map(model -> {
			final long size = model.size();
			dataset.removeNamedModel(graphUri);
			return size;
		}).orElse(0l);
	}
	
	public final Store getStore(){
		return store;
	}

}

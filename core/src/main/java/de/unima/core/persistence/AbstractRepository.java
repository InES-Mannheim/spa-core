package de.unima.core.persistence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.query.Dataset;
import org.apache.jena.vocabulary.RDFS;

import de.unima.core.persistence.transformation.Transformation;
import de.unima.core.storage.Store;

public abstract class AbstractRepository<T extends Entity<R>, R> implements Repository<T, R> {
	
	protected final Store store;
	protected final Transformation<T>.SubjectMapping transformation;
	
	public AbstractRepository(Store store) {
		this.store = store;
		this.transformation = Transformation.map(getEntityType())
				.to(getRdfClass())
				.withId("id")
				.withString("label")
				.asLiteral(RDFS.label.toString());
		adaptTransformation();
	}
	
	protected abstract Class<T> getEntityType();
	
	protected abstract String getRdfClass();
	
	protected void adaptTransformation(){}
	
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> findById(R id) {
		throw new UnsupportedOperationException();
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

	private Long deleteGraphsOfEntity(T entity, Dataset dataset) {
		return deleteGeneratedGraph(dataset, entity) + deleteGraphNamedLikeEntity(dataset, entity);
	}
	
	private Long deleteGeneratedGraph(Dataset dataset, T entity) {
		final String graphUri = generateGraphId(entity);
		return deleteNamedGraph(dataset, graphUri);
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
	
	protected void checkEntityToBeNotNullAndHasIdSet(T entity) {
		checkNotNull(entity, "Entity must not be null.");
		checkArgument(entity.getId() != null,
				String.format("Id of %s is null. Please set the Id as Uri.", entity.getClass().getSimpleName()));
	}
	
	protected String generateGraphId(T entity){
		return generateGraphId(entity.getId());
	}
	
	protected String generateGraphId(R id) {
		final String stringId = id.toString();
		return stringId.endsWith("/") ? stringId + "graph" : stringId + "/graph";
	}
	
	public final Store getStore(){
		return store;
	}

}

package de.unima.core.persistence;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.jena.query.Dataset;
import org.apache.jena.vocabulary.RDFS;

import de.unima.core.persistence.transformation.Transformation;
import de.unima.core.storage.Store;
import de.unima.core.storage.jena.JenaTDBStore;

public abstract class AbstractRepository<T extends Entity<R>, R> implements Repository<T, R> {
	
	private final Store store;
	protected final Transformation<T>.SubjectMapping transformation;
	
	public AbstractRepository(Optional<Path> pathToRepository) {
		this.store = pathToRepository.map(path -> new JenaTDBStore(path)).orElseGet(() -> new JenaTDBStore());
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
	public Optional<R> save(T entity) {
		final String graphId = generateGraphId(entity);
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			dataset.addNamedModel(graphId, transformation.get().apply(entity));
			return entity.getId();
		})).get();
	}
	
	protected String generateGraphId(T entity){
		final String id = entity.getId().toString();
		return id.endsWith("/") ? id + "graph" : id + "/graph";
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
	public Optional<Integer> deleteAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Integer> deleteById(R id) {
		throw new UnsupportedOperationException();
	}
	
	Store getStore(){
		return store;
	}

}

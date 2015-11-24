package de.unima.core.persistence;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import de.unima.core.storage.Store;

public abstract class PartialDataStore<T extends Entity<R>, R> extends AbstractRepository<T, R> {

	public PartialDataStore(Store store) {
		super(store);
	}

	public Optional<R> addDataToEntity(T entity, Model data){
		checkEntityToBeNotNullAndHasIdSet(entity);
		checkNotNull(data, "Data must not be null");
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			dataset.addNamedModel(entity.getId().toString(), data);
			return entity.getId();
		})).get();
	}

	public Optional<Model> findDataOfEntity(T entity) {
		checkEntityToBeNotNullAndHasIdSet(entity);
		return store.readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> dataset.getNamedModel(entity.getId().toString()))).get();
	}

}

package de.unima.core.domain.repository;

import static com.google.common.base.Preconditions.*;

import java.util.Optional;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import de.unima.core.domain.Schema;
import de.unima.core.domain.Vocabulary;
import de.unima.core.persistence.AbstractRepository;
import de.unima.core.storage.Store;

public class SchemaRepository extends AbstractRepository<Schema, String> {

	public SchemaRepository(Store store) {
		super(store);
	}

	@Override
	protected Class<Schema> getEntityType() {
		return Schema.class;
	}

	@Override
	protected String getRdfClass() {
		return Vocabulary.Schema;
	}
	
	public Optional<String> addDataToSchema(Schema schema, Model data){
		checkEntityToBeNotNullAndHasIdSet(schema);
		checkNotNull(data, "Data must not be null");
		return store.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> {
			dataset.addNamedModel(schema.getId(), data);
			return schema.getId();
		})).get();
	}

	public Optional<Model> getDataForSchema(Schema schema) {
		checkEntityToBeNotNullAndHasIdSet(schema);
		return store.readWithConnection(connection -> connection.as(Dataset.class).map(dataset -> dataset.getNamedModel(schema.getId()))).get();
	}

}

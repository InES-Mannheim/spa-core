package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntModel;

import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Repository;
import de.unima.core.io.DataSource;
import de.unima.core.io.Importer;
import de.unima.core.persistence.AbstractEntity;
import de.unima.core.persistence.Store;

public class DataSchemeImpl extends AbstractEntity<String> implements DataScheme {
	
	private String id;
	private OntModel data;
	private Repository repository;
	private Map<String, Importer<? extends DataSource>> instanceImporters;

	
	public DataSchemeImpl(String i, OntModel d, Repository r, Importer<? extends DataSource> imp, Store<String> store) {
		super(store);
		this.id = i;
		this.data = d;
		this.repository = r;
		this.instanceImporters = new HashMap<String, Importer<? extends DataSource>>();
		this.instanceImporters.put(imp.getID(), imp);
	}

	@Override
	final public OntModel getData() {

		return this.data;
	}

	@Override
	final public String getId() {

		return this.id;
	}

	@Override
	final protected boolean setData(OntModel d) {
		this.data = d;
		return true;
	}

	@Override
	public void setImporter(Importer<? extends DataSource> imp) {
		instanceImporters.put(imp.getID(), imp);
	}

	@Override
	public Importer<? extends DataSource> getImporter(String impID) {

		return this.instanceImporters.get(impID);
	}
	
	
	
}

package de.unima.core.domain.impl;

import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Repository;
import de.unima.core.io.DataSource;
import de.unima.core.io.Importer;
import de.unima.core.persistence.Store;
import de.unima.core.persistence.impl.AbstractStorable;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntModel;

public class DataSchemeImpl extends AbstractStorable implements DataScheme {
	
	private String id;
	private OntModel data;
	private Repository repository;
	private Map<String, Importer<? extends DataSource>> instanceImporters;

	
	public DataSchemeImpl(String i, OntModel d, Repository r, Importer<? extends DataSource> imp) {

		this.id = i;
		this.data = d;
		this.repository = r;
		this.instanceImporters = new HashMap<String, Importer<? extends DataSource>>();
		this.instanceImporters.put(imp.getID(), imp);
		this.setStore(null);
	}
	
	public DataSchemeImpl(String i, OntModel d, Repository r, Importer<? extends DataSource> imp, Class<Store> s) {

		this(i, d, r, imp);

		try {

			this.setStore(s.newInstance());

		} catch (InstantiationException e) {

			this.setStore(null);
			e.printStackTrace();

		} catch (IllegalAccessException e) {

			this.setStore(null);
			e.printStackTrace();
		}
	}

	@Override
	final public OntModel getData() {

		return this.data;
	}

	@Override
	final public String getID() {

		return this.id;
	}

	@Override
	final protected void setData(OntModel d) {
		
		this.data = d;
	}

	@Override
	public void setImporter(Importer<? extends DataSource> imp) {
		
		this.instanceImporters.put(imp.getID(), imp);
	}

	@Override
	public Importer<? extends DataSource> getImporter(String impID) {

		return this.instanceImporters.get(impID);
	}
	
	
	
}

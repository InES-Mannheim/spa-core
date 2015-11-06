package de.unima.core.domain.impl;

import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Repository;
import de.unima.core.io.DataSource;
import de.unima.core.io.Importer;
import de.unima.core.persistence.Store;
import de.unima.core.persistence.impl.AbstractStorable;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.OntModel;

public class DataSchemeImpl extends AbstractStorable implements DataScheme {
	
	private String id;
	private OntModel data;
	private Repository repository;
	private Set<Importer<DataSource>> instanceImporters;

	
	public DataSchemeImpl(String i, OntModel d, Repository r, Importer<DataSource> imp) {

		this.id = i;
		this.data = d;
		this.repository = r;
		this.instanceImporters = new HashSet<Importer<DataSource>>();
		this.instanceImporters.add(imp);
		this.setStore(null);
	}
	
	public DataSchemeImpl(String i, OntModel d, Repository r, Importer<DataSource> imp, Class<Store> s) {

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
	final protected OntModel getData() {

		return this.data;
	}

	@Override
	final protected String getID() {

		return this.id;
	}

	@Override
	final protected void setData(OntModel d) {
		
		this.data = d;
	}
	
	
	
}

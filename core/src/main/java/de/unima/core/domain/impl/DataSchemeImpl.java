package de.unima.core.domain.impl;

import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Repository;
import de.unima.core.io.DataSource;
import de.unima.core.io.Importer;

import java.util.Set;

import org.apache.jena.ontology.OntModel;

public class DataSchemeImpl implements DataScheme {
	
	private OntModel data;
	private Repository repository;
	private Set<Importer<DataSource>> instanceImporters;

	
	public DataSchemeImpl(OntModel d, Repository r, Importer<DataSource> i) {
		
		this.data = d;
		this.repository = r;
		this.instanceImporters.add(i);
	}
	
}

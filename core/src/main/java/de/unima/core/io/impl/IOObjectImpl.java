package de.unima.core.io.impl;

import org.apache.jena.ontology.OntModel;

import de.unima.core.io.DataSource;
import de.unima.core.io.Exporter;
import de.unima.core.io.IOObject;
import de.unima.core.io.Importer;

public class IOObjectImpl<S extends DataSource> implements IOObject<S> {
	
	private Importer<S> importer;
	private Exporter<S> exporter;
	private OntModel data;
	private S source;

	
	public IOObjectImpl(S s, Importer<S> i) {
		
		this.source = s;
		this.importer = i;
	}
	
	@Override
	public OntModel getData() {
		
		this.importData();
		return this.data;
	}
	

	@Override
	public void setExporter(Exporter e) {
		
		this.exporter = e;
	}

	private void importData() {

		this.data = this.importer.importData(this.source);
	}
	
}

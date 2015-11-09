package de.unima.core.io.impl;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.io.BPMN20File;
import de.unima.core.io.Importer;

public class BPMN20ImporterImpl implements Importer<BPMN20File> {
	
	private String id;

	@Override
	public OntModel importData(BPMN20File bpmn_source) {
		
		OntModel m = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
		// TODO Implement the import from BPMN-Data into OntModel
		return m;
	}

	@Override
	public String getID() {

		return this.id;
	}
}

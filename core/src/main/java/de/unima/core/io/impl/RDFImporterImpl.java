package de.unima.core.io.impl;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.io.RDFFile;
import de.unima.core.io.RDFImporter;

public class RDFImporterImpl implements RDFImporter {

	@Override
	public OntModel importData(RDFFile ds) {
	
		// TODO Auto-generated method stub
		return ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

}

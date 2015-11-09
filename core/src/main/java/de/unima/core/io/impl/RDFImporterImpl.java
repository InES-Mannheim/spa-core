package de.unima.core.io.impl;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.io.RDFFile;
import de.unima.core.io.RDFImporter;

public class RDFImporterImpl implements RDFImporter {

	@Override
	public OntModel importData(RDFFile rdf_source) {
	
		OntModel m = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	    m.read(rdf_source.getPath());
	    return m;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

}

package de.unima.core.io.impl;

import java.io.File;

import org.apache.jena.ontology.OntModel;

import de.unima.core.io.Importer;
import de.unima.core.io.XMLFile;
import de.unima.ontmalizer.XSD2OWLMapper;

public class XSDImporterImpl implements Importer<XMLFile> {
	
	private String id;

	@Override
	public OntModel importData(XMLFile xmlSource) {
		XSD2OWLMapper mapping = new XSD2OWLMapper(new File(xmlSource.getPath()));
	    mapping.setObjectPropPrefix("");
	    mapping.setDataTypePropPrefix("");
	    mapping.convertXSD2OWL();
		return mapping.getOntology();
	}

	@Override
	public String getID() {

		return this.id;
	}

}

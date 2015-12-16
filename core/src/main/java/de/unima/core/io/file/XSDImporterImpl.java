package de.unima.core.io.file;

import java.io.File;

import org.apache.jena.ontology.OntModel;

import de.unima.ontmalizer.XSD2OWLMapper;

public class XSDImporterImpl implements FileBasedImporter<OntModel> {
	
	@Override
	public OntModel importData(File xmlSource) {
		XSD2OWLMapper mapping = new XSD2OWLMapper(xmlSource);
	    mapping.setObjectPropPrefix("");
	    mapping.setDataTypePropPrefix("");
	    mapping.convertXSD2OWL();
		return mapping.getOntology();
	}

}

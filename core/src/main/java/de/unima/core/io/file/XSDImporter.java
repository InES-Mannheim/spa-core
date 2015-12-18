package de.unima.core.io.file;

import java.io.File;
import java.io.InputStream;

import org.apache.jena.ontology.OntModel;

import de.unima.ontmalizer.XSD2OWLMapper;

public class XSDImporter implements FileBasedImporter<OntModel> {
	
	@Override
	public OntModel importData(File xmlSource) {
		XSD2OWLMapper mapping = new XSD2OWLMapper(xmlSource);
	    return convertXsdToOntology(mapping);
	}
	
	public OntModel importData(InputStream xmlSource){
		final XSD2OWLMapper mapping = new XSD2OWLMapper(xmlSource);
		return convertXsdToOntology(mapping);
	}

	private OntModel convertXsdToOntology(XSD2OWLMapper mapping) {
		mapping.setObjectPropPrefix("");
	    mapping.setDataTypePropPrefix("");
	    mapping.convertXSD2OWL();
		return mapping.getOntology();
	}
	

}

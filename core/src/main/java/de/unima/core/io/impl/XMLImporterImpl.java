package de.unima.core.io.impl;

import java.io.File;

import org.apache.jena.ontology.OntModel;

import de.unima.core.io.Importer;
import de.unima.core.io.XMLFile;
import de.unima.ontmalizer.XML2OWLMapper;

public class XMLImporterImpl implements Importer<XMLFile> {
	
	private String id;
	private OntModel schemeOntologyModel;
	
	public XMLImporterImpl(OntModel schemeOntologyModel) {
		this.schemeOntologyModel = schemeOntologyModel;
	}

	@Override
	public OntModel importData(XMLFile xmlSource) {
		XML2OWLMapper mapping = new XML2OWLMapper(new File(xmlSource.getPath()), schemeOntologyModel);
	    mapping.convertXML2OWL();
	    schemeOntologyModel.add(mapping.getModel());
		return schemeOntologyModel;
	}

	@Override
	public String getID() {

		return this.id;
	}

}

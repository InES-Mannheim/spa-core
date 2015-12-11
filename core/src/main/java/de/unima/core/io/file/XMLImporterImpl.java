package de.unima.core.io.file;

import java.io.File;

import org.apache.jena.ontology.OntModel;

import de.unima.ontmalizer.XML2OWLMapper;

public class XMLImporterImpl implements FileBasedImporter {
	
	private String id;
	private OntModel schemeOntologyModel;
	
	public XMLImporterImpl(OntModel schemeOntologyModel) {
		this.schemeOntologyModel = schemeOntologyModel;
	}

	@Override
	public OntModel importData(File xmlSource) {
		XML2OWLMapper mapping = new XML2OWLMapper(xmlSource, schemeOntologyModel);
	    mapping.convertXML2OWL();
	    schemeOntologyModel.add(mapping.getModel());
		return schemeOntologyModel;
	}

	@Override
	public String getID() {

		return this.id;
	}

}

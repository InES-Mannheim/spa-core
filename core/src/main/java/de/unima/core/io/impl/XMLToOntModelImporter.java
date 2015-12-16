package de.unima.core.io.impl;

import java.io.File;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.io.Importer;
import de.unima.core.io.XMLFile;
import de.unima.ontmalizer.XML2OWLMapper;

public class XMLToOntModelImporter implements Importer<XMLFile> {
	
	private String id;
	private OntModel schemeOntologyModel;
	
	public XMLToOntModelImporter(OntModel schemeOntologyModel) {
		this.schemeOntologyModel = schemeOntologyModel;
	}

	@Override
	public OntModel importData(XMLFile xmlSource) {
		XML2OWLMapper mapping = new XML2OWLMapper(new File(xmlSource.getPath()), schemeOntologyModel);
	    mapping.convertXML2OWL();
	    OntModel ontModelInstance = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	    ontModelInstance.add(mapping.getModel());
		return ontModelInstance;
	}

	@Override
	public String getID() {

		return this.id;
	}

}

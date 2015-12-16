package de.unima.core.io.file;

import java.io.File;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.ontmalizer.XML2OWLMapper;

public class XMLImporter implements FileBasedImporter<Model> {
	
	private OntModel ontology;
	
	public XMLImporter(OntModel ontology) {
		this.ontology = ontology;
	}

	@Override
	public Model importData(File xml) {
		XML2OWLMapper mapper = new XML2OWLMapper(xml, ontology);
	    mapper.convertXML2OWL();
	    return ModelFactory.createDefaultModel().add(mapper.getModel());
	}

}

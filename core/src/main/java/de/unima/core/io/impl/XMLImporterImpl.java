package de.unima.core.io.impl;

import java.io.File;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.io.Importer;
import tr.com.srdc.ontmalizer.XSD2OWLMapper;

public class XMLImporterImpl implements Importer<XSD2001FileImpl> {
	
	private String id;
	private OntModel typeModel;
	
	public XMLImporterImpl(XSD2001FileImpl xsdSource) {
		XSD2OWLMapper mapping = new XSD2OWLMapper(new File("xes.xsd"));
	    mapping.setObjectPropPrefix("");
	    mapping.setDataTypePropPrefix("");
	    mapping.convertXSD2OWL();
	    (OntModel)mapping.getOntology();
	}

	@Override
	public OntModel importData(XSD2001FileImpl xmlSource) {
		
		OntModel m = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
		// TODO Implement the import from BPMN-Data into OntModel
		return m;
	}

	@Override
	public String getID() {

		return this.id;
	}

}

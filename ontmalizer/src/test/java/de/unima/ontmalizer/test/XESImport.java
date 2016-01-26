package de.unima.ontmalizer.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.junit.Test;

import tr.com.srdc.ontmalizer.XML2OWLMapper;
import tr.com.srdc.ontmalizer.XSD2OWLMapper;


public class XESImport {

	// Save converted models to files
	public static void main(String[] args) {
		XSD2OWLMapper mapping = new XSD2OWLMapper(new File("src/test/resources/xes.xsd"));
		mapping.setObjectPropPrefix("");
		mapping.setDataTypePropPrefix("");
		mapping.convertXSD2OWL();
		
		FileOutputStream ont;
		try {
		    File f = new File("src/test/resources/xes.owl");
		    ont = new FileOutputStream(f);
		    mapping.writeOntology(ont, "RDF/XML");
		    ont.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		XML2OWLMapper instanceMapper = new XML2OWLMapper(new File("src/test/resources/running-example.xes"), new File("src/test/resources/xes.owl"));
		instanceMapper.convertXML2OWL();
		instanceMapper.setBaseURI("http://unima.de/running-examplex#");

		try {
		    File f = new File("src/test/resources/runnig-example.owl");
			ont = new FileOutputStream(f);
			instanceMapper.writeModel(ont, "RDF/XML");
		    ont.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	@Test
	public void convertXESToOWL() {
		XSD2OWLMapper schemeMapper = new XSD2OWLMapper(new File("src/test/resources/xes.xsd"));
		schemeMapper.setObjectPropPrefix("");
		schemeMapper.setDataTypePropPrefix("");
		schemeMapper.convertXSD2OWL();
		
		OntModel schemeOntology = schemeMapper.getOntology();
		
		XML2OWLMapper instanceMapper = new XML2OWLMapper(new File("src/test/resources/running-example.xes"), schemeMapper);
		instanceMapper.convertXML2OWL();
		instanceMapper.setBaseURI("http://unima.de/running-examplex#");

		Model instanceOntology = instanceMapper.getModel();
		schemeOntology.add(instanceOntology);
		NodeIterator it = schemeOntology.listObjects();
		HashSet<String> values = new HashSet<>();
		while(it.hasNext()) {
			RDFNode node = it.next();
			if(node.isLiteral()) {
				values.add(node.asLiteral().getString());
			}
		}
		
		assertTrue(values.contains("check ticket"));
		assertTrue(values.contains("reinitiate request"));
		assertTrue(values.contains("examine thoroughly"));
		assertTrue(values.contains("register request"));
		assertTrue(values.contains("examine casually"));
	}
	
	@Test
	public void convertXESToOWLUsingOntModel() throws IOException {
		XSD2OWLMapper schemeMapper = new XSD2OWLMapper(new File("src/test/resources/xes.xsd"));
		schemeMapper.setObjectPropPrefix("");
		schemeMapper.setDataTypePropPrefix("");
		schemeMapper.convertXSD2OWL();
		
		OntModel schemeOntology = schemeMapper.getOntology();
		
		XML2OWLMapper instanceMapper = new XML2OWLMapper(new File("src/test/resources/running-example.xes"), schemeOntology);
		instanceMapper.convertXML2OWL();
		instanceMapper.setBaseURI("http://unima.de/running-examplex#");

		Model instanceOntology = instanceMapper.getModel();
		schemeOntology.add(instanceOntology);
		NodeIterator it = schemeOntology.listObjects();
		HashSet<String> values = new HashSet<>();
		while(it.hasNext()) {
			RDFNode node = it.next();
			if(node.isLiteral()) {
				values.add(node.asLiteral().getString());
			}
		}
		
		assertTrue(values.contains("check ticket"));
		assertTrue(values.contains("reinitiate request"));
		assertTrue(values.contains("examine thoroughly"));
		assertTrue(values.contains("register request"));
		assertTrue(values.contains("examine casually"));
	}

}

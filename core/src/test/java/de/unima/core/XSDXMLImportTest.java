package de.unima.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.ontology.OntModel;
import org.junit.Test;

import de.unima.core.domain.DataPool;
import de.unima.core.domain.DataScheme;
import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.domain.impl.RepositoryImpl;
import de.unima.core.io.BPMN20File;
import de.unima.core.io.IOObject;
import de.unima.core.io.RDFFile;
import de.unima.core.io.XMLFile;
import de.unima.core.io.impl.BPMN20FileImpl;
import de.unima.core.io.impl.BPMN20ImporterImpl;
import de.unima.core.io.impl.IOObjectImpl;
import de.unima.core.io.impl.RDFFileImpl;
import de.unima.core.io.impl.RDFImporterImpl;
import de.unima.core.io.impl.XMLFileImpl;
import de.unima.core.io.impl.XMLImporterImpl;
import de.unima.core.io.impl.XSDImporterImpl;

public class XSDXMLImportTest {

	@Test
	public void test() throws IOException {
		Repository r = new RepositoryImpl();
		
		IOObject<XMLFile> xsdScheme = new IOObjectImpl<XMLFile>(new XMLFileImpl("src/test/resources/xes.xsd"), new XSDImporterImpl());
		
		FileWriter writer = new FileWriter(new File("src/test/resources/xes.owl"));
		xsdScheme.getData().write(writer);
		
		IOObject<RDFFile> rdfScheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("src/test/resources/xes.owl"), new RDFImporterImpl());
	
		r.registerDataScheme("XES2.0", rdfScheme, new XMLImporterImpl(rdfScheme.getData()));
		
		r.createProject("TestProject", "XES2.0");
		
		Project p = r.getProject("TestProject");
		
		p.createDataPool("SampleDataPool");
		
		DataPool dp = p.getDataPool("SampleDataPool");
		
		IOObject<XMLFile> xesModel = new IOObjectImpl<XMLFile>(new XMLFileImpl("src/test/resources/running-example.xes"), new XMLImporterImpl(rdfScheme.getData()));
		
		dp.addDataModel("SampleXES2.0Model", xesModel);
				
		writer = new FileWriter(new File("src/test/resources/running-example.owl"));
		xesModel.getData().write(writer);
	}
	
	@Test
	public void test2() throws IOException {
		Repository r = new RepositoryImpl();
		
		IOObject<XMLFile> xsdScheme = new IOObjectImpl<XMLFile>(new XMLFileImpl("src/test/resources/BPMN20.xsd"), new XSDImporterImpl());
		
		FileWriter writer = new FileWriter(new File("src/test/resources/BPMN20.owl"));
		xsdScheme.getData().write(writer);
		
		IOObject<RDFFile> rdfScheme = new IOObjectImpl<RDFFile>(new RDFFileImpl("src/test/resources/BPMN20.owl"), new RDFImporterImpl());
	
		r.registerDataScheme("BPMN2.0", rdfScheme, new XMLImporterImpl(rdfScheme.getData()));
		
		r.createProject("TestProject2", "BPMN2.0");
		
		Project p = r.getProject("TestProject2");
		
		p.createDataPool("SampleDataPool2");
		
		DataPool dp = p.getDataPool("SampleDataPool2");
		
		IOObject<XMLFile> xesModel = new IOObjectImpl<XMLFile>(new XMLFileImpl("src/test/resources/Mail Process.bpmn"), new XMLImporterImpl(rdfScheme.getData()));
		
		dp.addDataModel("SampleBPMN2.0Model", xesModel);
				
		writer = new FileWriter(new File("src/test/resources/Mail Process.owl"));
		xesModel.getData().write(writer);
	}

}

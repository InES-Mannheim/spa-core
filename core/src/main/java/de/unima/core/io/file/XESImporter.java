package de.unima.core.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class XESImporter implements FileBasedImporter<Model> {

	private final OntModel xesOntology;
	private final XMLImporter dataImporter;
    
	public XESImporter() {
		try(InputStream stream = openStream("xml/xes.xsd")){
			xesOntology = new XSDImporter().importData(stream);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		dataImporter = new XMLImporter(xesOntology);
	}
	
	private static InputStream openStream(final String resourceName) {
		final URL fileUrl = Resources.getResource(resourceName);
		try {
			return Resources.asByteSource(fileUrl).openStream();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Model importData(File dataSource) {
		if(isEmpty(dataSource)){
			return ModelFactory.createDefaultModel();
		}
		return dataImporter.importData(dataSource);
	}

	private boolean isEmpty(File dataSource) {
		try {
			return Files.size(dataSource.toPath()) == 0l;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}

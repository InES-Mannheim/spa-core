package de.unima.core.io.file;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class XESImporter implements FileBasedImporter<Model> {

	private final OntModel xesOntology;
	private final XMLImporterImpl dataImporter;
    
	public XESImporter() {
		xesOntology = new XSDImporterImpl().importData(getFilePath("xml/xes.xsd").toFile());
		dataImporter = new XMLImporterImpl(xesOntology);
	}
	
	private static Path getFilePath(final String fileName) {
		try {
			return Paths.get(Resources.getResource(fileName).toURI());
		} catch (URISyntaxException e) {
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

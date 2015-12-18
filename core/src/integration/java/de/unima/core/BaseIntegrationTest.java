package de.unima.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public abstract class BaseIntegrationTest {
	
	protected Model loadFileAsModel(final String fileName) throws IOException {
		final Path path = getFilePath(fileName);
		final InputStream inputStreamForFile = Files.newInputStream(path, StandardOpenOption.READ);
		return ModelFactory.createDefaultModel().read(inputStreamForFile, null);
	}

	protected static Path getFilePath(final String fileName) {
		try {
			return Paths.get(Resources.getResource(fileName).toURI());
		} catch (URISyntaxException e) {
			throw Throwables.propagate(e);
		}
	}
}

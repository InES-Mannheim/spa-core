package de.unima.core.io.file;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class BPMN20ExporterTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private BPMN20Exporter exporter;
	private String namespace = "http://test.de/ps#";

	@Before
	public void setUp(){
		this.exporter = new BPMN20Exporter(namespace);
	}
	
	@Test
	public void whenModelWasExportedToBpmnCorrespondingFileMustBeBpmn() throws IOException{
		final Model model = loadFileAsModel("scanMailProcessModel.owl");
		final File target = folder.newFile();
		
		exporter.exportToFile(model, target);
		
		final BpmnModelInstance expectedInstance = Bpmn.readModelFromFile(target);
		
		assertThat(expectedInstance, is(notNullValue()));
	}
	
	@Test
	public void whenSimpleModelWasExportedToBpmnCorrespondingFileMustBeBpmn() throws IOException{
		final Model importedBpmn = new BPMN20Importer(namespace).importData(getFilePath("example-spa.bpmn").toFile());
		final File target = folder.newFile();
		
		exporter.exportToFile(importedBpmn, target);
		
		final BpmnModelInstance expectedInstance = Bpmn.readModelFromFile(target);
		
		assertThat(expectedInstance, is(notNullValue()));
	}
	
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

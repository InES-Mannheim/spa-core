package de.unima.core.io.file;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class XESImporterTest {

	@Rule
	public TemporaryFolder folders = new TemporaryFolder();
	
	private XESImporter xesImporter;

	@Before
	public void setup(){
		xesImporter = new XESImporter();
	}
	
	@Test
	public void whenEmptyFileIsImportedThenTheModelShouldBeEmpty() throws IOException{
		final Model imported = xesImporter.importData(folders.newFile());
		assertThat(imported.isEmpty(), is(true));
	}
	
	@Test
	public void whenXesWithOneEventEntryIsImportedThenTheModelShouldContainCorrespondingStatements() throws IOException{
		final File oneEventLog = folders.newFile("oneLog.xes");
		appendOneEvent(oneEventLog);
		final Model importedLog = xesImporter.importData(oneEventLog);
		assertThat(importedLog.isEmpty(), is(false));
		final List<RDFNode> importedObjects = importedLog.listObjects().toList();
		assertThat(importedObjects, hasItem(ResourceFactory.createTypedLiteral("Costs", XSDDatatype.XSDName)));
		assertThat(importedObjects, hasItem(ResourceFactory.createPlainLiteral("50")));
	}

	private void appendOneEvent(File oneLogEntry) throws IOException {
		final String logEntry = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> "
				+ " <log xes.version=\"1.0\" xmlns=\"http://code.deckfour.org/xes\"> "
				+ " <trace>"
				+ " <event>"
				+ " <string key=\"Costs\" value=\"50\"/>"
				+ " </event>"
				+ " </trace>"
				+ " </log>";
		Files.write(oneLogEntry.toPath(), logEntry.getBytes(), StandardOpenOption.APPEND);
	}
}

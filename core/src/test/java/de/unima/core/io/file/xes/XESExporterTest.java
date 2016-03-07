package de.unima.core.io.file.xes;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class XESExporterTest {
	
	private static OntModelToXLogExporter exporter = new OntModelToXLogExporter();
	
	@Test
	public void whenOneEmptyLogIsAvailbleThenOneLogIsExported() {
		Set<XLog> logs = exportOntModelFromFileToXESLogs("empty.owl");
		assertThat(logs.size(), is(1));
	}
	
	@Test
	public void whenExtensionsAreAvailableThenExtensionsAreExported() {
		Set<XLog> logs = exportOntModelFromFileToXESLogs("extensions.owl");
		Set<String> extensionNames = logs.stream().findFirst().get().getExtensions()
				.stream().map(extension -> extension.getName()).collect(Collectors.toSet());	
		assertThat(extensionNames, hasItems("Concept", "Time", "Organizational"));
	}
	
	@Test
	public void whenGlobalsAreAvailableThenGlobalsAreExported() {
		Set<XLog> logs = exportOntModelFromFileToXESLogs("globals.owl");
		List<XAttribute> globalsKeys2 = logs.stream().findFirst().get().getGlobalEventAttributes();
		Set<String> globalsKeys = logs.stream().findFirst().get().getGlobalEventAttributes()
				.stream().map(global -> global.getKey()).collect(Collectors.toSet());
		assertThat(globalsKeys, hasItems("Costs", "concept:name", "org:resource"));
	}
	
	@Test
	public void whenClassifiersAreAvailableThenClassifiersAreExported() {
		Set<XLog> logs = exportOntModelFromFileToXESLogs("classifiers.owl");
		Set<String> classifierNames = logs.stream().findFirst().get().getClassifiers()
				.stream().map(classifier -> classifier.toString()).collect(Collectors.toSet());
		assertThat(classifierNames, hasItems("activity classifier", "Activity"));
	}
	
	@Test
	public void whenATraceIsAvailableThenTraceIsExported() {
		Set<XLog> logs = exportOntModelFromFileToXESLogs("log-with-trace.owl");
		int numberOfTraces = logs.stream().findFirst().get().size();
		assertThat(numberOfTraces, is(1));
	}
	
	@Test
	public void whenAnEventWithAttributesIsAvailableThenAllDetailsAreExported() {
		Set<XLog> logs = exportOntModelFromFileToXESLogs("log-with-trace-with-event.owl");
		XEvent event = logs.stream().findFirst().get().get(0).get(0);
		XAttributeMap attribues = event.getAttributes();
		assertThat(attribues.get("Costs").toString(), equalTo("50"));
		assertThat(attribues.get("Resource").toString(), equalTo("Pete"));
	}
	
	private Set<XLog> exportOntModelFromFileToXESLogs(String ontModelFileName) {
		Model model = loadModelFromFile(ontModelFileName);
		return exporter.export(model);
	}
	
	private Model loadModelFromFile(String fileName) {
		Model model = ModelFactory.createDefaultModel();
		model.read("xes-exporter/" + fileName, "RDF/XML");
		return model;
	}
}

package de.unima.core.io.impl.XESExporter;

import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.deckfour.xes.model.XLog;

public class OntModelToXESExporter {

	public Set<XLog> export(Model model) {
		LogsRetriever retriever = new LogsRetriever(model);
		return retriever.retrieve();
	}
}

package de.unima.core.io.file.xes;

import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.deckfour.xes.model.XLog;

public class OntModelToXLogExporter {

	public Set<XLog> export(Model model) {
		LogsRetriever retriever = new LogsRetriever(model);
		return retriever.retrieve();
	}
}

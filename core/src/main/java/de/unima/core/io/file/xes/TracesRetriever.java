package de.unima.core.io.file.xes;

import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

class TracesRetriever extends SetRetriever<XTrace> {
	
	private final RDFNode logNode;
	
	public TracesRetriever(RDFNode logNode, Model model) {
		super(model);
		this.logNode = logNode;
	}
	
	@Override
	protected XTrace createElement(QuerySolution querySolution) {
		final RDFNode traceNode = querySolution.get("?trace");
		final XTrace trace = factory.createTrace();
		final XAttributeMap attributes = getAttributes(traceNode);
		trace.getAttributes().putAll(attributes);
		final Set<XEvent> eventsForTrace = getEvents(traceNode);
		trace.addAll(eventsForTrace);
		return trace;
	}

	@Override
	protected void setQueryParameters(SelectBuilder queryBuilder) {
		queryBuilder.setVar("?log", logNode);
	}
	
	private Set<XEvent> getEvents(RDFNode traceNode) {
		EventsRetriever retriever = new EventsRetriever(traceNode, model);
		return retriever.retrieve();
	}

	@Override
	protected SelectBuilder createAndConfigureQueryBuilder() {
		final SelectBuilder queryBuilder;
		queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addPrefix("rdf:", NS_RDF);
		queryBuilder.addVar("?trace");
		queryBuilder.addWhere("?trace", "rdf:type", "xes:TraceType");
		return queryBuilder;
	}

}

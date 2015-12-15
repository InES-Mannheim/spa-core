package de.unima.core.io.impl.XESExporter;

import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

public class TracesRetriever extends SetRetriever<XTrace> {

	private static final SelectBuilder queryBuilder;
	/**
	 * Initialize query builder for retrieving all traces of a specific log
	 */
	static {
		queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addPrefix("rdf:", NS_RDF);
		queryBuilder.addVar("?trace");
		queryBuilder.addWhere("?trace", "rdf:type", "xes:TraceType");
		queryBuilder.addWhere("?log", "xes:trace", "?trace");
	}
	
	private final RDFNode logNode;
	
	public TracesRetriever(RDFNode logNode, Model model) {
		super(model);
		this.logNode = logNode;
	}
	
	@Override
	protected SelectBuilder getQueryBuilder() {
		return queryBuilder;
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
	protected void setQueryParameters() {
		queryBuilder.setVar("?log", logNode);
	}
	
	private Set<XEvent> getEvents(RDFNode traceNode) {
		EventsRetriever retriever = new EventsRetriever(traceNode, model);
		return retriever.retrieve();
	}

}

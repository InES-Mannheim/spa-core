package de.unima.core.io.impl.XESExporter;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;

public class EventsRetriever extends SetRetriever<XEvent> {

	private static final SelectBuilder queryBuilder;
	/**
	 * Initialize query builder for retrieving all events of a specific trace
	 */
	static {
		queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addPrefix("rdf:", NS_RDF);
		queryBuilder.addVar("?event");
		queryBuilder.addWhere("?event", "rdf:type", "xes:EventType");
		queryBuilder.addWhere("?trace", "xes:event", "?event");
	}
	
	private final RDFNode traceNode;
	
	public EventsRetriever(RDFNode traceNode, Model model) {
		super(model);
		this.traceNode = traceNode;
	}
	
	@Override
	protected SelectBuilder getQueryBuilder() {
		return queryBuilder;
	}

	@Override
	protected XEvent createElement(QuerySolution querySolution) {
		final RDFNode eventNode = querySolution.get("?event");
		final XEvent event = factory.createEvent();
		final XAttributeMap attributes = getAttributes(eventNode);
		event.getAttributes().putAll(attributes);
		return event;
	}

	@Override
	protected void setQueryParameters() {
		queryBuilder.setVar("?trace", traceNode);
	}

}

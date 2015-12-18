package de.unima.core.io.file.xes;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;

class EventsRetriever extends SetRetriever<XEvent> {

	
	private final RDFNode traceNode;
	
	public EventsRetriever(RDFNode traceNode, Model model) {
		super(model);
		this.traceNode = traceNode;
	}
	
	@Override
	protected SelectBuilder createAndConfigureQueryBuilder() {
		final SelectBuilder queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addPrefix("rdf:", NS_RDF);
		queryBuilder.addVar("?event");
		queryBuilder.addWhere("?event", "rdf:type", "xes:EventType");
		return queryBuilder;
	}
	
	@Override
	protected void setQueryParameters(SelectBuilder queryBuilder) {
		queryBuilder.setVar("?trace", traceNode);
	}

	@Override
	protected XEvent createElement(QuerySolution querySolution) {
		final RDFNode eventNode = querySolution.get("?event");
		final XEvent event = factory.createEvent();
		final XAttributeMap attributes = getAttributes(eventNode);
		event.getAttributes().putAll(attributes);
		return event;
	}
}

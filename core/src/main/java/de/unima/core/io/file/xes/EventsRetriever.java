/*******************************************************************************
 *    Copyright 2016 University of Mannheim
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.unima.core.io.file.xes;

import org.apache.jena.query.ParameterizedSparqlString;
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
	protected ParameterizedSparqlString createAndConfigureQueryBuilder() {
		final ParameterizedSparqlString queryBuilder = new ParameterizedSparqlString();
		queryBuilder.setNsPrefix("xes", NS_XES);
		queryBuilder.setNsPrefix("rdf", NS_RDF);
		queryBuilder.append("SELECT DISTINCT ?event\n");
		queryBuilder.append("WHERE {\n");
		queryBuilder.append("	?event rdf:type xes:EventType .\n");
		queryBuilder.append("}\n");
		return queryBuilder;
	}
	
	@Override
	protected void setQueryParameters(ParameterizedSparqlString queryBuilder) {
		queryBuilder.setParam("?trace", traceNode);
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

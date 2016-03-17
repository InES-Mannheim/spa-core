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

import java.util.Set;

import org.apache.jena.query.ParameterizedSparqlString;
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
	protected void setQueryParameters(ParameterizedSparqlString queryBuilder) {
		queryBuilder.setParam("?log", logNode);
	}
	
	private Set<XEvent> getEvents(RDFNode traceNode) {
		EventsRetriever retriever = new EventsRetriever(traceNode, model);
		return retriever.retrieve();
	}

	@Override
	protected ParameterizedSparqlString createAndConfigureQueryBuilder() {
		final ParameterizedSparqlString queryBuilder = new ParameterizedSparqlString();
		queryBuilder.setNsPrefix("xes", NS_XES);
		queryBuilder.setNsPrefix("rdf", NS_RDF);
		queryBuilder.append("SELECT DISTINCT ?trace\n");
		queryBuilder.append("WHERE {\n");
		queryBuilder.append("	?trace rdf:type xes:TraceType .\n");
		queryBuilder.append("}\n");
		return queryBuilder;
	}

}

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

import java.util.Collection;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.model.XAttribute;

class ScopedLogGlobalsRetriever extends SetRetriever<Collection<XAttribute>> {

	private final RDFNode logNode;
	private final String scope;
	
	public ScopedLogGlobalsRetriever(RDFNode logNode, String scope, Model model) {
		super(model);
		this.logNode = logNode;
		this.scope = scope;
	}

	@Override
	protected Collection<XAttribute> createElement(QuerySolution querySolution) {
		final RDFNode globalNode = querySolution.get("?global");
		return getAttributes(globalNode).values();
	}

	@Override
	protected void setQueryParameters(ParameterizedSparqlString queryBuilder) {
		queryBuilder.setLiteral("?scope", scope);
		queryBuilder.setParam("?log", logNode);
	}

	@Override
	protected ParameterizedSparqlString createAndConfigureQueryBuilder() {
		final ParameterizedSparqlString queryBuilder = new ParameterizedSparqlString();
		queryBuilder.setNsPrefix("xsd", NS_XSD);
		queryBuilder.setNsPrefix("xes", NS_XES);
		queryBuilder.append("SELECT DISTINCT ?global\n");
		queryBuilder.append("WHERE {\n");
		queryBuilder.append("	?log xes:global ?global .\n");
		queryBuilder.append("	?global xes:scope ?scope^^xsd:NCName .\n");
		queryBuilder.append("}\n");
		return queryBuilder;
	}

}

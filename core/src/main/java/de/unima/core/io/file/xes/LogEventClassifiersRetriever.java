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
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;

class LogEventClassifiersRetriever extends SetRetriever<XEventClassifier> {

	private final RDFNode logNode;
	
	public LogEventClassifiersRetriever(RDFNode logNode, Model model) {
		super(model);
		this.logNode = logNode;
	}

	@Override
	protected XEventClassifier createElement(QuerySolution querySolution) {
		final String classifierName = querySolution.get("?name").asLiteral().getString();				
		final String classifierKeys= querySolution.get("?keys").asLiteral().getString();
		return new XEventAttributeClassifier(classifierName, classifierKeys);
	}

	@Override
	protected void setQueryParameters(ParameterizedSparqlString queryBuilder) {
		queryBuilder.setParam("?log", logNode);
	}

	@Override
	protected ParameterizedSparqlString createAndConfigureQueryBuilder() {
		final ParameterizedSparqlString queryBuilder = new ParameterizedSparqlString();
		queryBuilder.setNsPrefix("xes", NS_XES);
		queryBuilder.append("SELECT DISTINCT ?name ?keys\n");
		queryBuilder.append("WHERE {\n");
		queryBuilder.append("	?log xes:classifier ?classifier .\n");
		queryBuilder.append("	?classifier\n");
		queryBuilder.append("		xes:name ?name ;\n");
		queryBuilder.append("		xes:keys ?keys .\n");
		queryBuilder.append("}\n");
		return queryBuilder;
	}

}

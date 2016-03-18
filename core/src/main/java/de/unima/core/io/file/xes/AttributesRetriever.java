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

import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;

import com.google.common.base.Throwables;

class AttributesRetriever extends Retriever<XAttributeMap> {
	
	private final RDFNode node;
	
	public AttributesRetriever(RDFNode node, Model model) {
		super(model);
		this.node = node;
	}
	
	@Override
	protected ParameterizedSparqlString createAndConfigureQueryBuilder() {
		ParameterizedSparqlString queryBuilder = new ParameterizedSparqlString();
		queryBuilder.setNsPrefix("xes", NS_XES);
		queryBuilder.setNsPrefix("rdfs", NS_RDFS);
		queryBuilder.setNsPrefix("owl", NS_OWL);
		
		queryBuilder.append("SELECT DISTINCT ?key ?value ?attribute\n");
		queryBuilder.append("WHERE {\n");
		queryBuilder.append("	?eventAttr\n");
		queryBuilder.append("		xes:key     ?key ;\n");
		queryBuilder.append("		xes:value   ?value .\n");
		queryBuilder.append("	?node ?attribute ?eventAttr .\n");
		queryBuilder.append("	?attributeType rdfs:subClassOf xes:AttributeType .\n");
		queryBuilder.append("	?attributeTypeAnon\n");
		queryBuilder.append("		owl:allValuesFrom   ?attributeType ;\n");
		queryBuilder.append("		owl:onProperty   ?attribute .\n");
		queryBuilder.append("}\n");
		
		return queryBuilder;
	}
	
	@Override
	public XAttributeMap retrieve() {
		final ParameterizedSparqlString builder = createAndConfigureQueryBuilder();
		setQueryParameters(builder);
		return executeQuery(builder.asQuery());
	}
	
	@Override
	protected void setQueryParameters(ParameterizedSparqlString queryBuilder) {
		queryBuilder.setParam("?node", node);
	}
	
	@Override
	protected XAttributeMap executeQuery(Query query) {
		XAttributeMap attributes = factory.createAttributeMap();
		try(QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				XAttribute attribute = createElement(solution);
				attributes.put(attribute.getKey(), attribute);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}
		return attributes;
	}

	protected XAttribute createElement(QuerySolution querySolution) {
		final String attributeType = querySolution.get("?attribute").asNode().getLocalName();
		final String key = querySolution.get("?key").asLiteral().getString();
		final String value = querySolution.get("?value").asLiteral().getString();
		final XAttribute attribute = createAttribute(attributeType, key, value);
		return attribute;
	}

	
	private XAttribute createAttribute(String attributeType, String key, String value) {
		switch(attributeType) {
			case "date": return factory.createAttributeTimestamp(key, parseDate(value), null);
			case "int": return factory.createAttributeDiscrete(key, Integer.parseInt(value), null);
			case "float": return factory.createAttributeContinuous(key, Double.parseDouble(value), null);
			case "boolean": return factory.createAttributeBoolean(key, Boolean.parseBoolean(value), null);
			case "id": return factory.createAttributeID(key, parseId(value), null);
			case "list": return factory.createAttributeList(key, null);
			case "container": return factory.createAttributeContainer(key, null);
			case "string":
			default: return factory.createAttributeLiteral(key, value, null);
		}
	}
	
	private static Date parseDate(String dateString) {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
		return Date.from(zonedDateTime.toInstant());
	}
	
	private static XID parseId(String idString) {
		final XID id = XID.parse(idString);
		return id;
	}

}

package de.unima.core.io.file.xes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.arq.querybuilder.SelectBuilder;
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

	private final static SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	private final RDFNode node;
	
	public AttributesRetriever(RDFNode node, Model model) {
		super(model);
		this.node = node;
	}
	
	@Override
	protected SelectBuilder createAndConfigureQueryBuilder() {
		final SelectBuilder queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addPrefix("rdfs:", NS_RDFS);
		queryBuilder.addPrefix("owl:", NS_OWL);
		queryBuilder.addVar("?key");
		queryBuilder.addVar("?value");
		queryBuilder.addVar("?attribute");
		queryBuilder.addWhere("?eventAttr", "xes:key", "?key");
		queryBuilder.addWhere("?eventAttr", "xes:value", "?value");
		queryBuilder.addWhere("?node", "?attribute", "?eventAttr");
		queryBuilder.addWhere("?attributeType", "rdfs:subClassOf", "xes:AttributeType");
		queryBuilder.addWhere("?attributeTypeAnon", "owl:allValuesFrom", "?attributeType");
		queryBuilder.addWhere("?attributeTypeAnon", "owl:onProperty", "?attribute");
		return queryBuilder;
	}
	
	@Override
	public XAttributeMap retrieve() {
		final SelectBuilder builder = createAndConfigureQueryBuilder();
		setQueryParameters(builder);
		return executeQuery(builder.build());
	}
	
	@Override
	protected void setQueryParameters(SelectBuilder queryBuilder) {
		queryBuilder.setVar("?node", node);
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
		try {
			return dateParser.parse(dateString);
		} catch (ParseException e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static XID parseId(String idString) {
		final XID id = XID.parse(idString);
		return id;
	}

}

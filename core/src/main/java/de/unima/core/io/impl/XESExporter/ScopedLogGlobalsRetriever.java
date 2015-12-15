package de.unima.core.io.impl.XESExporter;

import java.util.Collection;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.model.XAttribute;

public class ScopedLogGlobalsRetriever extends SetRetriever<Collection<XAttribute>> {

	private static final SelectBuilder queryBuilder;
	/**
	 * Initialize query builder for retrieving all scoped global variables of a specific log
	 */
	static {
		queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xsd:", NS_XSD);
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addVar("?global");
		queryBuilder.addWhere("?log", "xes:global", "?global");
		queryBuilder.addWhere("?global", "xes:scope", "?scope");
	}
	
	private final RDFNode logNode;
	private final String scope;
	
	public ScopedLogGlobalsRetriever(RDFNode logNode, String scope, Model model) {
		super(model);
		this.logNode = logNode;
		this.scope = scope;
	}
	
	@Override
	protected SelectBuilder getQueryBuilder() {
		return queryBuilder;
	}

	@Override
	protected Collection<XAttribute> createElement(QuerySolution querySolution) {
		final RDFNode globalNode = querySolution.get("?global");
		return getAttributes(globalNode).values();
	}

	@Override
	protected void setQueryParameters() {
		queryBuilder.setVar("?scope", "\""+scope+"\"^^xsd:NCName");
		queryBuilder.setVar("?log", logNode);
	}

}

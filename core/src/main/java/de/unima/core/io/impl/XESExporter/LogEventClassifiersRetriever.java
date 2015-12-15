package de.unima.core.io.impl.XESExporter;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;

public class LogEventClassifiersRetriever extends SetRetriever<XEventClassifier> {

	private static final SelectBuilder queryBuilder;
	/**
	 * Initialize query builder for retrieving all extensions of a specific log
	 */
	static {
		queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addVar("?name");
		queryBuilder.addVar("?keys");
		queryBuilder.addWhere("?log", "xes:classifier", "?classifier");
		queryBuilder.addWhere("?classifier", "xes:name", "?name");
		queryBuilder.addWhere("?classifier", "xes:keys", "?keys");
	}
	
	private final RDFNode logNode;
	
	public LogEventClassifiersRetriever(RDFNode logNode, Model model) {
		super(model);
		this.logNode = logNode;
	}
	
	@Override
	protected SelectBuilder getQueryBuilder() {
		return queryBuilder;
	}

	@Override
	protected XEventClassifier createElement(QuerySolution querySolution) {
		final String classifierName = querySolution.get("?name").asLiteral().getString();				
		final String classifierKeys= querySolution.get("?keys").asLiteral().getString();
		return new XEventAttributeClassifier(classifierName, classifierKeys);
	}

	@Override
	protected void setQueryParameters() {
		queryBuilder.setVar("?log", logNode);
	}

}

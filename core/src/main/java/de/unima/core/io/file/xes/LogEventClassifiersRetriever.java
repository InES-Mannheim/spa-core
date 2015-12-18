package de.unima.core.io.file.xes;

import org.apache.jena.arq.querybuilder.SelectBuilder;
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
	protected void setQueryParameters(SelectBuilder queryBuilder) {
		queryBuilder.setVar("?log", logNode);
	}

	@Override
	protected SelectBuilder createAndConfigureQueryBuilder() {
		final SelectBuilder queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addVar("?name");
		queryBuilder.addVar("?keys");
		queryBuilder.addWhere("?log", "xes:classifier", "?classifier");
		queryBuilder.addWhere("?classifier", "xes:name", "?name");
		queryBuilder.addWhere("?classifier", "xes:keys", "?keys");
		return queryBuilder;
	}

}

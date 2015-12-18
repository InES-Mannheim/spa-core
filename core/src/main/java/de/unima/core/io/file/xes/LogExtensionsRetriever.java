package de.unima.core.io.file.xes;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;

import com.google.common.base.Throwables;

class LogExtensionsRetriever extends SetRetriever<XExtension> {

	private final RDFNode logNode;
	
	public LogExtensionsRetriever(RDFNode logNode, Model model) {
		super(model);
		this.logNode = logNode;
	}

	@Override
	protected XExtension createElement(QuerySolution querySolution) {
		final String uri = querySolution.get("?uri").asLiteral().getString();
		try {
			return XExtensionManager.instance().getByUri(new URI(uri));
		} catch (URISyntaxException e) {
			Throwables.propagate(e);
		}
		return null;
	}

	@Override
	protected void setQueryParameters(SelectBuilder queryBuilder) {
		queryBuilder.setVar("?log", logNode);
	}

	@Override
	protected SelectBuilder createAndConfigureQueryBuilder() {
		final SelectBuilder queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addVar("?uri");
		queryBuilder.addWhere("?log", "xes:extension", "?extension");
		queryBuilder.addWhere("?extension", "xes:uri", "?uri");
		return queryBuilder;
	}

}

package de.unima.core.io.impl.XESExporter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class LogsRetriever extends SetRetriever<XLog> {

	private static final SelectBuilder queryBuilder;
	/**
	 * Initialize query builder for retrieving all logs
	 */
	static {
		queryBuilder = new SelectBuilder();
		queryBuilder.addPrefix("xes:", NS_XES);
		queryBuilder.addPrefix("rdf:", NS_RDF);
		queryBuilder.addVar("?log");
		queryBuilder.addVar("?attributeKey");
		queryBuilder.addVar("?attributeValue");
		queryBuilder.addWhere("?log", "rdf:type", "xes:log");
	}
	
	public LogsRetriever(Model model) {
		super(model);
	}
	
	@Override
	protected SelectBuilder getQueryBuilder() {
		return queryBuilder;
	}

	@Override
	protected XLog createElement(QuerySolution querySolution) {
		final RDFNode logNode = querySolution.get("?log");
		final XLog log = factory.createLog();
		final XAttributeMap attributes = getAttributes(logNode);
		log.getAttributes().putAll(attributes);
		final Set<XTrace> tracesForLog = getTraces(logNode);
		log.addAll(tracesForLog);
		final Set<XExtension> extensions = getLogExtensions(logNode);
		log.getExtensions().addAll(extensions);
		final Collection<XAttribute> traceScopeGlobalAttributes = getScopedLogGlobals(logNode, "trace");
		log.getGlobalTraceAttributes().addAll(traceScopeGlobalAttributes);
		final Collection<XAttribute> eventScopeGlobalAttributes = getScopedLogGlobals(logNode, "event");
		log.getGlobalEventAttributes().addAll(eventScopeGlobalAttributes);
		final Set<XEventClassifier> classifiers = getLogEventClassifiers(logNode);
		log.getClassifiers().addAll(classifiers);
		return log;
	}
	
	@Override
	protected void setQueryParameters() {		
	}
	
	private Set<XTrace> getTraces(RDFNode logNode) {
		TracesRetriever retriever = new TracesRetriever(logNode, model);
		return retriever.retrieve();
	}
	
	private Set<XExtension> getLogExtensions(RDFNode logNode) {
		LogExtensionsRetriever retriever = new LogExtensionsRetriever(logNode, model);
		return retriever.retrieve();
	}
	
	private Collection<XAttribute> getScopedLogGlobals(RDFNode logNode, String scope) {
		ScopedLogGlobalsRetriever retriever = new ScopedLogGlobalsRetriever(logNode, scope, model);
		Set<Collection<XAttribute>> attributes = retriever.retrieve();
		if(attributes.isEmpty()) {
			return new HashSet<>();
		}
		return attributes.iterator().next();
	}

	private Set<XEventClassifier> getLogEventClassifiers(RDFNode logNode) {
		LogEventClassifiersRetriever retriever = new LogEventClassifiersRetriever(logNode, model);
		return retriever.retrieve();
	}
}

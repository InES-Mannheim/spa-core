package de.unima.core.io.impl;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventAttributeClassifier;

import com.google.common.base.Throwables;

/**
 * This exporter converts an ontology model based on XES format to an object model in the XES API format
 */
public class OntModelToXESExporter {
	
	private final static String NS_XES = "http://code.deckfour.org/xes#";
	private final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final static String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private final static String NS_OWL = "http://www.w3.org/2002/07/owl#";
	private final static SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	private final static XFactory factory = new XFactoryNaiveImpl();
	
	/**
	 * Exports an ontology model to a set of logs
	 * @param model an ontology model of one or multiple XES log instance(s)
	 * @return a set of logs in XES API format
	 */
	public Set<XLog> exportModel(Model model) {			
		final Set<XLog> logs = retrieveLogs(model);		
		return logs;
	}
	
	/**
	 * Extracts and returns a set of logs
	 * @param model	an ontology model of one or multiple XES log instance(s)
	 * @return a set of logs in XES API format
	 */
	private Set<XLog> retrieveLogs(Model model) {
		final Set<XLog> logs = new HashSet<XLog>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addPrefix("rdf:", NS_RDF);
		selectBuilder.addVar("?log");
		selectBuilder.addVar("?attributeKey");
		selectBuilder.addVar("?attributeValue");
		selectBuilder.addWhere("?log", "rdf:type", "xes:log");
		final Query logQuery = selectBuilder.build();

		try(QueryExecution queryExecution = QueryExecutionFactory.create(logQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final RDFNode logNode = solution.get("?log");
				final XLog log = factory.createLog();
				final XAttributeMap attributes = retrieveAttributes(model, logNode);
				log.getAttributes().putAll(attributes);
				final Set<XTrace> tracesForLog = retrieveTraces(model, logNode);
				log.addAll(tracesForLog);
				final Set<XExtension> extensions = retrieveLogExtensions(model, logNode);
				log.getExtensions().addAll(extensions);
				final Set<XAttribute> traceScopeGlobalAttributes = retrieveScopedLogGlobals(model, logNode, "trace");
				log.getGlobalTraceAttributes().addAll(traceScopeGlobalAttributes);
				final Set<XAttribute> eventScopeGlobalAttributes = retrieveScopedLogGlobals(model, logNode, "event");
				log.getGlobalEventAttributes().addAll(eventScopeGlobalAttributes);
				final Set<XEventClassifier> classifiers = retrieveLogEventClassifiers(model, logNode);
				log.getClassifiers().addAll(classifiers);
				logs.add(log);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}
		
		return logs;
	}
	
	/**
	 * Extracts and returns a set of traces for a specific log instance
	 * @param model	an ontology model of one or multiple XES log instance(s)
	 * @param logNode a concrete log instance for which all traces should be retrieved
	 * @return a set of traces in XES API format for a specific log instance
	 */
	private Set<XTrace> retrieveTraces(Model model, RDFNode logNode) {
		final Set<XTrace> traces = new HashSet<XTrace>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addPrefix("rdf:", NS_RDF);
		selectBuilder.addVar("?trace");
		selectBuilder.addWhere("?trace", "rdf:type", "xes:TraceType");
		selectBuilder.addWhere(wrapURI(logNode.asNode().getURI()), "xes:trace", "?trace");
		final Query traceQuery = selectBuilder.build();

		try(QueryExecution queryExecution = QueryExecutionFactory.create(traceQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final RDFNode traceNode = solution.get("?trace");
				final XTrace trace = factory.createTrace();
				final XAttributeMap attributes = retrieveAttributes(model, traceNode);
				trace.getAttributes().putAll(attributes);
				final Set<XEvent> eventsForTrace = retrieveEvents(model, traceNode);
				trace.addAll(eventsForTrace);
				traces.add(trace);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}
		
		return traces;
	}
	
	/**
	 * Extracts and returns a set of events for a specific trace instance
	 * @param model	an ontology model of one or multiple XES log instance(s)
	 * @param traceNode a concrete trace instance for which all events should be retrieved
	 * @return a set of events in XES API format for a specific trace instance
	 */
	private Set<XEvent> retrieveEvents(Model model, RDFNode traceNode) {
		final Set<XEvent> events = new HashSet<XEvent>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addPrefix("rdf:", NS_RDF);
		selectBuilder.addVar("?event");
		selectBuilder.addWhere("?event", "rdf:type", "xes:EventType");
		selectBuilder.addWhere(wrapURI(traceNode.asNode().getURI()), "xes:event", "?event");
		final Query eventQuery = selectBuilder.build();

		try(QueryExecution queryExecution = QueryExecutionFactory.create(eventQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final RDFNode eventNode = solution.get("?event");
				final XEvent event = factory.createEvent();
				final XAttributeMap attributes = retrieveAttributes(model, eventNode);
				event.getAttributes().putAll(attributes);
				events.add(event);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}
		
		return events;
	}
	
	/**
	 * Extracts and returns a set of attributes for a specific element instance
	 * @param model	an ontology model of one or multiple XES log instance(s)
	 * @param traceNode a concrete element instance for which all attributes should be retrieved
	 * @return a set of attributes in XES API format for a specific element instance
	 */
	private XAttributeMap retrieveAttributes(Model model, RDFNode node) {
		final XAttributeMap attributes = factory.createAttributeMap();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addPrefix("rdfs:", NS_RDFS);
		selectBuilder.addPrefix("owl:", NS_OWL);
		selectBuilder.addVar("?key");
		selectBuilder.addVar("?value");
		selectBuilder.addVar("?attribute");
		selectBuilder.addWhere("?eventAttr", "xes:key", "?key");
		selectBuilder.addWhere("?eventAttr", "xes:value", "?value");
		selectBuilder.addWhere(wrapURI(node.asNode().getURI()), "?attribute", "?eventAttr");
		selectBuilder.addWhere("?attributeType", "rdfs:subClassOf", "xes:AttributeType");
		selectBuilder.addWhere("?attributeTypeAnon", "owl:allValuesFrom", "?attributeType");
		selectBuilder.addWhere("?attributeTypeAnon", "owl:onProperty", "?attribute");
		final Query attributeQuery = selectBuilder.build();
		
		try(QueryExecution queryExecution = QueryExecutionFactory.create(attributeQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final String attributeType = solution.get("?attribute").asNode().getLocalName();
				final String key = solution.get("?key").asLiteral().getString();
				final String value = solution.get("?value").asLiteral().getString();
				final XAttribute attribute = createAttribute(attributeType, key, value);
				attributes.put(key, attribute);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}

		return attributes;
	}
	
	private Set<XExtension> retrieveLogExtensions(Model model, RDFNode logNode) {
		final Set<XExtension> extensions = new HashSet<XExtension>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addVar("?uri");
		selectBuilder.addWhere(wrapURI(logNode.asNode().getURI()), "xes:extension", "?extension");
		selectBuilder.addWhere("?extension", "xes:uri", "?uri");
		final Query logExtensionQuery = selectBuilder.build();
		
		try(QueryExecution queryExecution = QueryExecutionFactory.create(logExtensionQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final String uri = solution.get("?uri").asLiteral().getString();
				XExtension extension = XExtensionManager.instance().getByUri(new URI(uri));
				extensions.add(extension);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}

		return extensions;
	}
	
	private Set<XAttribute> retrieveScopedLogGlobals(Model model, RDFNode logNode, String scope) {
		final Set<XAttribute> scopedGlobalAttributes = new HashSet<XAttribute>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addVar("?global");
		selectBuilder.addWhere(wrapURI(logNode.asNode().getURI()), "xes:global", "?global");
		selectBuilder.addWhere("?global", "xes:scope", "\""+scope+"\""+"^^<http://www.w3.org/2001/XMLSchema#NCName>");
		final Query logExtensionQuery = selectBuilder.build();
		
		try(QueryExecution queryExecution = QueryExecutionFactory.create(logExtensionQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final RDFNode globalNode = solution.get("?global");				

				final XAttributeMap attributeMap = retrieveAttributes(model, globalNode);
				scopedGlobalAttributes.addAll(attributeMap.values());
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}

		return scopedGlobalAttributes;
	}
	
	private Set<XEventClassifier> retrieveLogEventClassifiers(Model model, RDFNode logNode) {
		final Set<XEventClassifier> eventClassifiers = new HashSet<XEventClassifier>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addVar("?name");
		selectBuilder.addVar("?keys");
		selectBuilder.addWhere(wrapURI(logNode.asNode().getURI()), "xes:classifier", "?classifier");
		selectBuilder.addWhere("?classifier", "xes:name", "?name");
		selectBuilder.addWhere("?classifier", "xes:keys", "?keys");
		final Query logExtensionQuery = selectBuilder.build();
		
		try(QueryExecution queryExecution = QueryExecutionFactory.create(logExtensionQuery, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final String classifierName = solution.get("?name").asLiteral().getString();				
				final String classifierKeys= solution.get("?keys").asLiteral().getString();
				
				XEventClassifier eventClassifier = new XEventAttributeClassifier(classifierName, classifierKeys);
				eventClassifiers.add(eventClassifier);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}

		return eventClassifiers;
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
	
	private static String wrapURI(String uid) {
		return "<" + uid + ">";
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

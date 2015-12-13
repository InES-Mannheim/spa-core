package de.unima.core.io.impl;

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

public class OntModelToXESExporter {
	
	private final static String NS_XES = "http://code.deckfour.org/xes#";
	private final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final XFactory factory = new XFactoryNaiveImpl();
	
	public Set<XLog> exportModel(Model model) {			
		final Set<XLog> logs = retrieveLogs(model);		
		return logs;
	}
	
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

		try(QueryExecution qexec = QueryExecutionFactory.create(logQuery, model)) {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final RDFNode logNode = solution.get("?log");
				final XLog log = factory.createLog();
				final XAttributeMap attributes = retrieveAttributes(model, logNode);
				log.getAttributes().putAll(attributes);
				final Set<XTrace> tracesForLog = retrieveTraces(model, logNode);
				log.addAll(tracesForLog);
				logs.add(log);
			}
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return logs;
	}
	
	private Set<XTrace> retrieveTraces(Model model, RDFNode logNode) {
		final Set<XTrace> traces = new HashSet<XTrace>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addPrefix("rdf:", NS_RDF);
		selectBuilder.addVar("?trace");
		selectBuilder.addWhere("?trace", "rdf:type", "xes:TraceType");
		selectBuilder.addWhere("<"+logNode.asNode().getURI()+">", "xes:trace", "?trace");
		final Query traceQuery = selectBuilder.build();

		try(QueryExecution qexec = QueryExecutionFactory.create(traceQuery, model)) {
			ResultSet results = qexec.execSelect();
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
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return traces;
	}
	
	private Set<XEvent> retrieveEvents(Model model, RDFNode traceNode) {
		final Set<XEvent> events = new HashSet<XEvent>();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addPrefix("rdf:", NS_RDF);
		selectBuilder.addVar("?event");
		selectBuilder.addWhere("?event", "rdf:type", "xes:EventType");
		selectBuilder.addWhere("<"+traceNode.asNode().getURI()+">", "xes:event", "?event");
		final Query eventQuery = selectBuilder.build();

		try(QueryExecution qexec = QueryExecutionFactory.create(eventQuery, model)) {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final RDFNode eventNode = solution.get("?event");
				final XEvent event = factory.createEvent();
				final XAttributeMap attributes = retrieveAttributes(model, eventNode);
				event.getAttributes().putAll(attributes);
				events.add(event);
			}
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return events;
	}
	
	private XAttributeMap retrieveAttributes(Model model, RDFNode node) {
		final XAttributeMap attributes = factory.createAttributeMap();
		final SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.addPrefix("xes:", NS_XES);
		selectBuilder.addVar("?key");
		selectBuilder.addVar("?value");
		selectBuilder.addWhere("?eventAttr", "xes:key", "?key");
		selectBuilder.addWhere("?eventAttr", "xes:value", "?value");
		selectBuilder.addWhere("<"+node.asNode().getURI()+">", "xes:string", "?eventAttr");
		final Query attributeQuery = selectBuilder.build();
		
		try(QueryExecution qexec = QueryExecutionFactory.create(attributeQuery, model)) {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				final String key = solution.get("?key").asLiteral().getString();
				final String value = solution.get("?value").asLiteral().getString();
				final XAttribute attribute = factory.createAttributeLiteral(key, value, null);
				attributes.put(key, attribute);
			}
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}

		return attributes;
	}

}

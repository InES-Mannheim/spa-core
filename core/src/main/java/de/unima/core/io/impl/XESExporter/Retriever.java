package de.unima.core.io.impl.XESExporter;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;

public abstract class Retriever<T> {
	
	protected final static String NS_XES = "http://code.deckfour.org/xes#";
	protected final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	protected final static String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	protected final static String NS_OWL = "http://www.w3.org/2002/07/owl#";
	protected final static String NS_XSD = "http://www.w3.org/2001/XMLSchema#";
	protected final static XFactory factory = new XFactoryNaiveImpl();
	
	protected final Model model;
	
	protected abstract SelectBuilder getQueryBuilder();
	protected abstract void setQueryParameters();
	public abstract T retrieve();
	protected abstract T executeQuery(Query query);
	
	public Retriever(Model model) {
		this.model = model;
	}
	
}

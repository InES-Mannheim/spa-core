package de.unima.core.io.impl.XESExporter;

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
import org.deckfour.xes.model.XAttributeMap;

import com.google.common.base.Throwables;

public abstract class SetRetriever<T> extends Retriever<Set<T>> {
	
	protected abstract T createElement(QuerySolution querySolution);
	
	public SetRetriever(Model model) {
		super(model);
	}
	
	public Set<T> retrieve() {
		final SelectBuilder queryBuilder = getQueryBuilder();
		final Query query;
		synchronized(SetRetriever.class){
			setQueryParameters();
			query = queryBuilder.build();	
		}
		return executeQuery(query);
	}
	
	protected Set<T> executeQuery(Query query) {
		Set<T> elements = new HashSet<>();
		try(QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
			ResultSet results = queryExecution.execSelect();
			while(results.hasNext()) {
				final QuerySolution solution = results.nextSolution();
				T element = createElement(solution);
				elements.add(element);
			}
		} catch(Exception e) {
			throw Throwables.propagate(e);
		}
		return elements;
	}
	
	protected XAttributeMap getAttributes(RDFNode node) {
		AttributesRetriever retriever = new AttributesRetriever(node, model);
		return retriever.retrieve();
	}
}

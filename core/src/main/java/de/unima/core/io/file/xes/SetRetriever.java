package de.unima.core.io.file.xes;

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

abstract class SetRetriever<T> extends Retriever<Set<T>> {
	
	public SetRetriever(Model model) {
		super(model);
	}
	
	protected abstract T createElement(QuerySolution querySolution);
	
	public Set<T> retrieve() {
		final SelectBuilder queryBuilder = createAndConfigureQueryBuilder(); 
		setQueryParameters(queryBuilder);
		return executeQuery(queryBuilder.build());
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

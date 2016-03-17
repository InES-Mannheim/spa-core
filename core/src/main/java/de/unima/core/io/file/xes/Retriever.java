/*******************************************************************************
 *    Copyright 2016 University of Mannheim
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.unima.core.io.file.xes;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;

abstract class Retriever<T> {
	
	protected final static String NS_XES = "http://www.xes-standard.org/#";
	protected final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	protected final static String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	protected final static String NS_OWL = "http://www.w3.org/2002/07/owl#";
	protected final static String NS_XSD = "http://www.w3.org/2001/XMLSchema#";
	protected final static XFactory factory = new XFactoryNaiveImpl();
	
	
	protected final Model model;
	
	public Retriever(Model model) {
		this.model = model;
	}
	
	protected abstract ParameterizedSparqlString createAndConfigureQueryBuilder();
	
	protected void setQueryParameters(ParameterizedSparqlString queryBuilder){}
	
	public abstract T retrieve();
	
	protected abstract T executeQuery(Query query);
	
}

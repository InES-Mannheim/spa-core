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

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.deckfour.xes.model.XLog;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class OntModelToXLogExporter {

	private static final String SCHEMAPATH = "ontologies/xes.owl";
	private static final OntModel SCHEMA = loadSchemaFromFile();
	
	private static OntModel loadSchemaFromFile() {
		OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
		try (InputStream schemaInputStream = Resources.asByteSource(Resources.getResource(SCHEMAPATH)).openBufferedStream()){
			schemaModel.read(schemaInputStream, null);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	    return schemaModel;
	}
	
	public Set<XLog> export(Model dataModel) {
		Model unifiedModel = ModelFactory.createUnion(SCHEMA, dataModel);
		LogsRetriever retriever = new LogsRetriever(unifiedModel);
		return retriever.retrieve();
	}

}

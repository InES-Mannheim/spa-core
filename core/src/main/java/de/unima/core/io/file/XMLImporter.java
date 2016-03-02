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
package de.unima.core.io.file;

import java.io.File;
import java.io.InputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import tr.com.srdc.ontmalizer.XML2OWLMapper;
import tr.com.srdc.ontmalizer.XSD2OWLMapper;

public class XMLImporter implements FileBasedImporter<Model> {
	
	private OntModel ontology;
	
	public XMLImporter(OntModel ontology) {
		this.ontology = ontology;
	}

	@Override
	public Model importData(File xml) {
		XML2OWLMapper mapper = new XML2OWLMapper(xml, ontology);
	    mapper.convertXML2OWL();
	    return ModelFactory.createDefaultModel().add(mapper.getModel());
	}

	public Model importData(InputStream is) {
		XML2OWLMapper mapper = new XML2OWLMapper(is, new XSD2OWLMapper(ontology));
		mapper.convertXML2OWL();
		return ModelFactory.createDefaultModel().add(mapper.getModel());
	}

}

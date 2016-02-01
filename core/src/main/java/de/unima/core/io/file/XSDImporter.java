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

import tr.com.srdc.ontmalizer.XSD2OWLMapper;

public class XSDImporter implements FileBasedImporter<OntModel> {
	
	@Override
	public OntModel importData(File xmlSource) {
		XSD2OWLMapper mapping = new XSD2OWLMapper(xmlSource);
	    return convertXsdToOntology(mapping);
	}
	
	public OntModel importData(InputStream xmlSource){
		final XSD2OWLMapper mapping = new XSD2OWLMapper(xmlSource);
		return convertXsdToOntology(mapping);
	}

	private OntModel convertXsdToOntology(XSD2OWLMapper mapping) {
		mapping.setObjectPropPrefix("");
	    mapping.setDataTypePropPrefix("");
	    mapping.convertXSD2OWL();
		return mapping.getOntology();
	}
	

}

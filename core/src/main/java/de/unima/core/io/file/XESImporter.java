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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class XESImporter implements FileBasedImporter<Model> {

	private final OntModel xesOntology;
	private final XMLImporter dataImporter;
    
	public XESImporter() {
		try(InputStream stream = openStream("xml/xes.xsd")){
			xesOntology = new XSDImporter().importData(stream);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		dataImporter = new XMLImporter(xesOntology);
	}
	
	private static InputStream openStream(final String resourceName) {
		final URL fileUrl = Resources.getResource(resourceName);
		try {
			return Resources.asByteSource(fileUrl).openStream();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Model importData(File dataSource) {
		if(isEmpty(dataSource)){
			return ModelFactory.createDefaultModel();
		}
		return dataImporter.importData(dataSource);
	}

	private boolean isEmpty(File dataSource) {
		try {
			return Files.size(dataSource.toPath()) == 0l;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}

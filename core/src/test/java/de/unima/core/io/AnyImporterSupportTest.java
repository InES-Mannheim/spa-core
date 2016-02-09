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
package de.unima.core.io;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

public class AnyImporterSupportTest {

	private AnyImporterSupport importerSupport;

	@Before
	public void setUp(){
		this.importerSupport = new AnyImporterSupport();
	}
	
	@Test
	public void whenImporterIsAddedAKeyShouldBeReturned(){
		final Key key = importerSupport.addImporter(createSimpleFileBasedImporter(), "BPMN2");
		assertThat(key, is(equalTo(new Key("BPMN2"))));
	}
	
	@Test
	public void whenImporterIsNotPresentAndIsRemovedThenItShouldReturnEmpty(){
		assertThat(importerSupport.removeImporter(new Key("bla")).isPresent(), is(false));
	}
	
	@Test
	public void whenImporterIsPresentAndIsRemovedThenItShouldBeReturned(){
		importerSupport.addImporter(createSimpleFileBasedImporter(), "BPMN2");
		assertThat(importerSupport.removeImporter(new Key("BPMN2")).isPresent(), is(true));
	}
	
	@Test
	public void whenImporterIsNotPresentAndShouldBeFoundThenReturnEmpty(){
		assertThat(importerSupport.findImporterByKey(new Key("BPMN2")).isPresent(), is(false));
	}
	
	@Test
	public void whenImporterIsPresentAndIsFoundThenItShouldBeReturned(){
		importerSupport.addImporter(createSimpleFileBasedImporter(), "BPMN2");
		assertThat(importerSupport.findImporterByKey(new Key("BPMN2")).isPresent(), is(true));
	}
	
	private Importer<File, Model> createSimpleFileBasedImporter(){
		return new Importer<File, Model>() {
			@Override
			public Model importData(File dataSource) {
				return ModelFactory.createOntologyModel();
			}
		};
	}
}

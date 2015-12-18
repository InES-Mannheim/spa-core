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

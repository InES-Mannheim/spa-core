package de.unima.core.io.formats;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.ModelParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

public class Bpmn2Test {

	@Rule
	public ExpectedException excepted = ExpectedException.none();
	
	final Bpmn2 bpmn2 = new Bpmn2("http://test.de/ind/");
	
	@Test
	public void instanceShouldNotBeNull(){
		final Bpmn2 bpmn2 = new Bpmn2("http://test.de/ind/");
		assertThat(bpmn2, is(notNullValue()));
	}
	
	@Test
	public void deserializeShouldNotBeNull(){
		assertThat(bpmn2.deserialize(), is(notNullValue()));
	}
	
	@Test
	public void shouldDeserializeAsNotNull(){
		final BpmnModelInstance bpmn = bpmn2.deserialize()
				.apply(new ByteArrayInputStream(simpleXmlProcess().getBytes()));
		assertThat(bpmn, is(notNullValue()));
	}
	
	@Test
	public void shouldDesirializeToOneProcess(){
		final BpmnModelInstance bpmn = bpmn2.deserialize()
				.apply(new ByteArrayInputStream(simpleXmlProcess().getBytes()));
		assertThat(bpmn.getModelElementsByType(Process.class), hasSize(1));
	}
	
	@Test
	public void shouldFailWhenDataIsNotAProcess(){
		excepted.expect(ModelParseException.class);
		excepted.expectCause(instanceOf(SAXException.class));
		bpmn2.deserialize().apply(new ByteArrayInputStream("aasd".getBytes()));
	}
	
	@Test
	public void shouldFailWhenDataIsNull(){
		excepted.expect(IllegalArgumentException.class);
		bpmn2.deserialize().apply(null);
	}
	
	private String simpleXmlProcess(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\" id=\"_ZoYc8GFjEeWzdo9Q5Ppa4w\" exporter=\"camunda modeler\" exporterVersion=\"2.7.0\" targetNamespace=\"http://activiti.org/bpmn\">\n" + 
				"  <bpmn2:process id=\"Process_1\" isExecutable=\"false\">\n" + 
				"    <bpmn2:startEvent id=\"StartEvent_1\"/>\n" + 
				"  </bpmn2:process>\n" + 
				"</bpmn2:definitions>";
	}
	
	@Test
	public void readShouldNotBeNull(){
		assertThat(bpmn2.read(), is(notNullValue()));
	}
	
	@Test
	public void readShouldReturnNotNullModel(){
		assertThat(bpmn2.read().apply(Bpmn.createEmptyModel()), is(notNullValue()));
	}
	
	@Test
	public void readShouldFailWhenBpmnModelIsNull(){
		excepted.expect(NullPointerException.class);
		assertThat(bpmn2.read().apply(null), is(notNullValue()));
	}
}

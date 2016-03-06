package de.unima.core.io.formats;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class Bpmn2Test {

	@Test
	public void instanceShouldNotBeNull(){
		final Bpmn2 bpmn2 = new Bpmn2();
		assertThat(bpmn2, is(notNullValue()));
	}
}

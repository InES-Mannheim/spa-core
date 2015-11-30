package de.unima.core.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProjectTest {

	@Test
	public void whenAllSchemasAreUnlinkedThenThereShouldBeNoLinkedSchemas(){
		final Project project = new Project("http://test.de/Project/1", new Repository("http://www.test.de/Repository/1"), "Test");
		final Schema linkedSchema = new Schema("http://test.de/Schema/1");
		project.linkSchema(linkedSchema);
		assertThat(project.isSchemaLinked(linkedSchema.getId()), is(true));
		project.unlinkAllSchemas();
		assertThat(project.isSchemaLinked(linkedSchema.getId()), is(not(true)));
	}
}

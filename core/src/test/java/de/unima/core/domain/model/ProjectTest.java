package de.unima.core.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Repository;
import de.unima.core.domain.model.Schema;

public class ProjectTest {

	@Test
	public void whenAllSchemasAreUnlinkedThenThereShouldBeNoLinkedSchemas(){
		final Project project = new Project("http://test.de/Project/1", "Test", new Repository("http://www.test.de/Repository/1"));
		final Schema linkedSchema = new Schema("http://test.de/Schema/1");
		project.linkSchema(linkedSchema);
		assertThat(project.isSchemaLinked(linkedSchema.getId()), is(true));
		project.unlinkAllSchemas();
		assertThat(project.isSchemaLinked(linkedSchema.getId()), is(not(true)));
	}
}

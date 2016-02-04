package de.unima.core.domain.model;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

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
	
	@Test
	public void allDataBucketShouldBeRemovableFromAProject(){
		final Project project = new Project("http://www.test.de");
		final DataPool dataPool = new DataPool("test", project);
		project.addDataPool(dataPool);
		final List<DataPool> pools = project.removeAllDataPools();
		assertThat(pools.get(0), is(dataPool));
		assertThat(project.getDataPools(),is(empty()));
	}
}

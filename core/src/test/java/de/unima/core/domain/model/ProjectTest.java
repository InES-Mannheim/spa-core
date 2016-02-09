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
package de.unima.core.domain.model;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

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

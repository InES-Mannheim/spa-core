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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AbstractEntityTest {
	
	@Test
	public void enititiesWithSameIdsShouldBeEqual(){
		final Entity<String> first = new StringEntity("test");
		final Entity<String> second = new StringEntity("test");
		assertThat(first, is(equalTo(second)));
	}
	
	@Test
	public void enititiesWithDifferentIdsShouldBeNonEqual(){
		final Entity<String> first = new StringEntity("test2");
		final Entity<String> second = new StringEntity("test");
		assertThat(first, is(not(equalTo(second))));
	}
	
	private class StringEntity extends AbstractEntity<String>{
		public StringEntity(String id) {
			super(id);
		}
	}
}

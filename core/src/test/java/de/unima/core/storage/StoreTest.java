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
package de.unima.core.storage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

public class StoreTest  {

	@Test
	public void noOpShouldBeCreated() {
		final Store store = Store.noOp();
		assertThat(store, is(notNullValue()));
	}
	
	@Test
	public void noOpShouldReturnEmpty() {
		final Store store = Store.noOp();
		final Optional<String> result = store.runWithConnection(connection -> "test");
		assertThat(result.isPresent(), is(false));
	}
}

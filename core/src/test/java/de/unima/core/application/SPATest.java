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
package de.unima.core.application;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class SPATest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private SPA spa;

	@Before
	public void setUp(){
		this.spa = SPABuilder.local().sharedMemory().build();
	}
	
	@Test
	public void whenFileFormatIsNotSupportedThenAnIllegalArgumentExceptionShouldBeThrown(){
		expected.expect(IllegalArgumentException.class);
		spa.importSchema(new File(""), "Nope", "na");
	}
}

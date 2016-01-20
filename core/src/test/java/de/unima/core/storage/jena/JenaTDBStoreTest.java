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
package de.unima.core.storage.jena;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.VCARD;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.unima.core.storage.StoreConnection;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore.JenaTdbStoreConnection;

public class JenaTDBStoreTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private JenaTDBStore store;

	@Before
	public void setUp() throws IOException {
		this.store = JenaTDBStore.withFolder(folder.newFolder().toPath());
	}

	@Test
	public void storeOperationMustBeRunWithAStoreConnection() {
		final Optional<StoreConnection> result = store.runWithConnection(connection -> connection);
		assertThat(result.isPresent(), is(true));
	}

	@Test
	public void storeConnectionMustBeAJenaTdbStoreConnection() {
		final Optional<StoreConnection> result = store.runWithConnection(connection -> connection);
		assertThat(result.get(), instanceOf(JenaTdbStoreConnection.class));
	}

	@Test
	public void storeConnectionMustBeCastableToDataSet() {
		final Optional<StoreConnection> result = store.runWithConnection(connection -> connection);
		assertThat(result.get().as(Dataset.class).isPresent(), is(true));
	}
	
	@Test
	public void storeConnectionShouldNotBeCastableToString() {
		final Optional<StoreConnection> result = store.runWithConnection(connection -> connection);
		assertThat(result.get().as(String.class).isPresent(), is(false));
	}

	@Test
	public void transactionSupportShouldBeEnabled() {
		assertThat(store.getConnection().areTransactionsSupported(), is(true));
	}
	
	@Test
	public void twoMemoryStoresShouldUseTheSameMemoryLocation(){
		final JenaTDBStore store1 = JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation);
		final JenaTDBStore store2 = JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation);
		
		store1.writeWithConnection(connection -> connection.as(Dataset.class).map(dataset -> 
			dataset.getDefaultModel().createResource("http://www.test.de/res1").addProperty(VCARD.FN, "test")
		));
		
		final List<Statement> statements = store2.readWithConnection(connection -> connection.as(Dataset.class)
				.map(dataset -> dataset.getDefaultModel().listStatements().toList())
				.orElse(Collections.<Statement>emptyList()))
				.get();
		assertThat(statements.size(), is(1));
	}

	@Test
	public void dataAddedToTheConnectionShouldBeCommitedIntoTheStore() {
		store.writeWithConnection(connection -> connection.as(Dataset.class)
				.map(dataset -> dataset.getDefaultModel()
						.createResource("http://test.de/subject")
						.addProperty(VCARD.FN, "test")));
		
		final List<Statement> statements = store.readWithConnection(connection -> connection.as(Dataset.class)
				.map(dataset -> dataset.getDefaultModel().listStatements().toList())
				.orElse(Collections.<Statement>emptyList()))
				.get();
		assertThat(statements.size(), is(1));
	}
}

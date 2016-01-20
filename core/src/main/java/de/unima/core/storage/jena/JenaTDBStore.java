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

import java.nio.file.Path;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;

import de.unima.core.storage.Lock;
import de.unima.core.storage.Store;
import de.unima.core.storage.StoreConnection;

public class JenaTDBStore implements Store {

	private final Dataset dataset;

	private JenaTDBStore(Location location){
		this.dataset = TDBFactory.createDataset(location);
	}
	
	public static JenaTDBStore withCommonMemoryLocation(String locationId){
		return new JenaTDBStore(Location.mem(locationId));
	}
	
	public static JenaTDBStore withUniqueMemoryLocation(){
		return new JenaTDBStore(Location.mem());
	}
	
	public static JenaTDBStore withFolder(Path pathToFolder){
		return new JenaTDBStore(Location.create(pathToFolder.toString()));
	}
	
	@Override
	public StoreConnection getConnection() {
		return new JenaTdbStoreConnection();
	}
	
	public final class JenaTdbStoreConnection implements StoreConnection {
		
		@Override
		public boolean areTransactionsSupported() {
			return true;
		}
	 	
		@Override
		public long beginTransaction(Lock lock) {
			final ReadWrite readWrite = (lock == Lock.READ ? ReadWrite.READ: ReadWrite.WRITE);
			JenaTDBStore.this.dataset.begin(readWrite);
			return 0;
		}
		
		@Override
		public void commitTransaction(long transactionToken) {
			JenaTDBStore.this.dataset.commit();
		}
		
		@Override
		public void rollbackTransaction(long transactionToken) {
			JenaTDBStore.this.dataset.abort();
		}
		
		@Override
		public Object getWrappedStoreConnection() {
			return JenaTDBStore.this.dataset;
		}
	}

}

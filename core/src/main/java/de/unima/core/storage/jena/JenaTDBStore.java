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

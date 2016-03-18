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

import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Throwables;

/**
 * Common abstraction over several triple stores.
 */
public interface Store {

	/**
	 * Locks {@link StoreConnection} in read mode.
	 * 
	 * <p> A {@code Read} lock indicates that the thread just performs read
	 * operations, i.e. no data is altered. Thus, multiple threads holding a
	 * {@code Read} lock may access the database at the same time. However, the
	 * locking semantics is dependent on the actual implementation.
	 * 
	 * @param operation performing a read operation 
	 * @param <T> result type of the read operation 
	 * @return operation result
	 */
	default <T> Optional<T> readWithConnection(Function<? super StoreConnection, T> operation){
		return runWithConnection(operation, Lock.READ);
	}
	
	/**
	 * Locks {@link StoreConnection} in write mode.
	 * 
	 * <p> In order to prevent race conditions, a {@code Write} lock is much
	 * more greedy than a {@code Read} lock. Depending on the underlying
	 * implementation this may result into a lock of the complete database.
	 * 
	 * @param operation performing a write operation 
	 * @param <T> result type of the write operation 
	 * @return operation result
	 */
	default <T> Optional<T> writeWithConnection(Function<? super StoreConnection, T> operation){
		return runWithConnection(operation, Lock.WRITE);
	}
	
	/**
	 * Runs given function against a new {@link StoreConnection}.
	 * 
	 * If transactions are supported, each operation is run in a transaction.
	 * 
	 * @param operation which should be executed
	 * @param lock data access mode; defaults to {@link Lock#WRITE}
	 * @param <T> result type of the function
	 * @return function result
	 * @throws IllegalStateException if any error occurs
	 */
	default <T> Optional<T> runWithConnection(Function<? super StoreConnection, T> operation, Lock... lock){
		final StoreConnection connection = getConnection();
		return Optional.ofNullable(TransactionSupport.tryTransactional(connection, operation, lock).apply(connection));
	}
	
	/**
	 * Creates a new {@code StoreConnection}.
	 * 
	 * @return store connection
	 */
	StoreConnection getConnection();

	/**
	 * Returns store which does nothing.
	 * 
	 * @return store which always returns an empty result
	 */
	public static Store noOp() {
		return new Store() {
			@Override
			public StoreConnection getConnection() {
				return new StoreConnection.Identity();
			}
			
			@Override
			public <T> Optional<T> runWithConnection(Function<? super StoreConnection, T> operation, Lock... lock) {
				return Optional.empty();
			}
		};
	}
	
	final class TransactionSupport {
		private static <T> Function<? super StoreConnection, T> tryTransactional(StoreConnection connection, Function<? super StoreConnection, T> operation, Lock[] locks){
			final Lock lock = Optional.ofNullable(locks).filter(ls -> ls.length > 0).map(ls -> ls[0]).orElse(Lock.WRITE);
			if(connection.areTransactionsSupported()){
				return conn -> {
					final long token = conn.beginTransaction(lock);
					try{
						final T result = operation.apply(conn);
						conn.commitTransaction(token);
						return result;
					} catch (Exception e){
						conn.rollbackTransaction(token);
						throw Throwables.propagate(e);
					}
				};
			}
			return operation;
		}
	}
}

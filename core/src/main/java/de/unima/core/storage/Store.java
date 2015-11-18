package de.unima.core.storage;

import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Throwables;

/**
 * Common abstraction over several triple stores.
 * 
 * @param <T> id type
 */
public interface Store {

	/**
	 * Locks {@link StoreConnection} in read mode.
	 * 
	 * @param operation performing a read operation
	 * @return operation result
	 */
	default <T> Optional<T> readWithConnection(Function<? super StoreConnection, T> operation){
		return runWithConnection(operation, Lock.READ);
	}
	
	/**
	 * Locks {@link StoreConnection} in write mode.
	 * 
	 * @param operation performing a read operation
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

package de.unima.core.io;

/**
 * Provides means to export data.
 * 
 * @param <T> format of the data.
 */
public interface Exporter<T> {
	public T export();
}

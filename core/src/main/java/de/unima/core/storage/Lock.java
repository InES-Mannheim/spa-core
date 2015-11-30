package de.unima.core.storage;

/**
 * Indicates type of data access to prevent race conditions.
 * 
 * Some stores optimize transactions for read and write access. For example,
 * Jena TDB implements write-ahead-locking.
 */
public enum Lock {
	READ, WRITE
}
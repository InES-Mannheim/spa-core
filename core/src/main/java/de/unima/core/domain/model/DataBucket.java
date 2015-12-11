package de.unima.core.domain.model;

/**
 * A data bucket contains the actual data.
 * 
 * All {@code DataBucket}s make up a complete {@link DataPool}.
 * Likewise, a {@code DataBucket} must adhere to the project {@link Schema}s.
 */
public class DataBucket extends AbstractEntity<String> {
	
	public DataBucket(String id) {
		super(id);
	}
	
	public DataBucket(String id, String label) {
		super(id, label);
	}

}
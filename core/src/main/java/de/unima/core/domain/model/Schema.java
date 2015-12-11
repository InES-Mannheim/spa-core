package de.unima.core.domain.model;

/**
 * A schema describes the structure of the data.
 */
public class Schema extends AbstractEntity<String> {

	public Schema(String id) {
		super(id);
	}
	
	public Schema(String id, String label) {
		super(id, label);
	}
}

package de.unima.core.domain.impl;

import de.unima.core.domain.Project;
import de.unima.core.domain.Schema;
import de.unima.core.persistence.AbstractEntity;

public class SchemaImpl extends AbstractEntity<String> implements Schema {
	
	public SchemaImpl(String id, Project project) {
		super(id);
	}

}

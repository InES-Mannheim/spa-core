package de.unima.core.domain;

import de.unima.core.io.DataSource;
import de.unima.core.io.Importer;
import de.unima.core.persistence.Entity;

public interface DataScheme extends Entity<String> {
	
	public void setImporter(Importer<? extends DataSource> imp);
	public Importer<? extends DataSource> getImporter(String imp);

}

package de.unima.core.domain;

import de.unima.core.io.DataSource;
import de.unima.core.io.Importer;
import de.unima.core.persistence.Storable;

public interface DataScheme extends Storable {
	
	public void setImporter(Importer<? extends DataSource> imp);
	public Importer<? extends DataSource> getImporter(String imp);

}

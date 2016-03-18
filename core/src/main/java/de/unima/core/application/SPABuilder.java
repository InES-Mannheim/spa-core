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
package de.unima.core.application;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import com.google.common.base.Preconditions;

import de.unima.core.io.AnyImporterSupport;
import de.unima.core.io.ImporterSupport;
import de.unima.core.io.file.BPMN20Exporter;
import de.unima.core.io.file.BPMN20Importer;
import de.unima.core.io.file.FileBasedExporterSupport;
import de.unima.core.io.file.RDFExporter;
import de.unima.core.io.file.RDFImporter;
import de.unima.core.io.file.XESExporter;
import de.unima.core.io.file.XESImporter;
import de.unima.core.io.file.XSDImporter;
import de.unima.core.persistence.PersistenceService;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

/**
 * The SPABuilder is used to create a new instance of SPA.
 * The builder provides a set of basic configurations but
 * can be easily extended with custom implementations e.g.
 * for support of another JenaTDBStore.
 */
public class SPABuilder {
 	
	/**
	 * Provide access to all configurations that
	 * use local memory or disks.
	 * @return An instance of a local builder
	 */
	public static LocalBuilder local() {
		return new SPABuilder().new LocalBuilder();
	}
	
	/**
	 * Provide access to all configurations that
	 * use remote connections to get and save triples.
	 * @return An instance of a remote builder
	 */
	public static RemoteBuilder remote(){
        return new SPABuilder().new RemoteBuilder();
    }
	
	public class LocalBuilder {
		
	    public UniqueMemoryBuilder uniqueMemory() {
	    	return new UniqueMemoryBuilder();
	    }
	    
	    public SharedMemoryBuilder sharedMemory() {
	    	return new SharedMemoryBuilder();
	    }
	    
	    public FolderBuilder folder(String pathToFolder) {
	    	return new FolderBuilder(pathToFolder);
	    }
	    
	    /**
	     * The UniqueMemoryBuilder is used to configure 
	     * a SPA instance that uses a unique location in
	     * memory in order to temporarily save triples.
	     */
	    public class UniqueMemoryBuilder extends Builder {
	    	
	    	protected void validateConfigurationParameters() {
	    	}
	    	
	    	protected PersistenceService getPersistenceService() {
	    		return new PersistenceService(JenaTDBStore.withUniqueMemoryLocation());
	    	}
	    	
	    }
	    
	    /**
	     * The SharedMemoryBuilder is used to configure 
	     * a SPA instance that uses a shared location in
	     * memory in order to temporarily save triples.
	     */
	    public class SharedMemoryBuilder extends Builder {

	    	protected void validateConfigurationParameters() {
	    	}
	    	
	    	protected PersistenceService getPersistenceService() {
	    		return new PersistenceService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
	    	}
	    	
	    }
	    
	    /**
	     * The FolderBuilder is used to configure 
	     * a SPA instance that uses the a specific folder
	     * in the files system in order to persist triples.
	     */
	    public class FolderBuilder extends Builder {
	    	
	    	private String pathToFolder;
	    	
	    	public FolderBuilder(String pathToFolder) {
	    		this.pathToFolder = pathToFolder;
	    	}
	    	
	    	protected void validateConfigurationParameters() {
	    		Preconditions.checkNotNull(pathToFolder);
	    	}
	    	
	    	protected PersistenceService getPersistenceService() {
	    		return new PersistenceService(JenaTDBStore.withFolder(Paths.get(pathToFolder)));
	    	}
	    	
	    }
	    
	}
	
	public class RemoteBuilder {
	}
 
	public abstract class Builder {
		
		private static final String DEFAULT_NAMESPACE = "http://www.uni-mannheim/spa/local/bpmn/";
		private String namespace = DEFAULT_NAMESPACE;
		
		/**
		 * Set the namespace for importers/exporters with dynamic namespace support.
		 * @param customNamespace The namespace which should be used
		 * @param <T> type of the concrete builder
		 * @return A instance of the extending class in order to chain further methods
		 */
		@SuppressWarnings("unchecked")
		public <T extends Builder> T namespace(String customNamespace) {
            this.namespace = customNamespace;
            return (T)this;
        }
		
		/**
		 * Creates the SPA instance based on the set configuration
		 * parameters.
		 * @throws IllegalArgumentException if any configuration parameter is not valid
		 * @throws NullPointerException if a mandatory configuration parameter is not set
		 * @return A SPA instance based on the configurations
		 */
		public SPA build() throws IllegalArgumentException {
			validateNamespace();
			validateConfigurationParameters();
			return createSpa(getPersistenceService());
	    }
		
		protected abstract void validateConfigurationParameters();
		
		protected abstract PersistenceService getPersistenceService();
		
		String getNamespace() {
			return namespace;
		}
		
		private void validateNamespace() throws IllegalArgumentException {
			try {
				new URL(namespace);
	    	} catch(MalformedURLException e) {
	    		throw new IllegalArgumentException("Namespace is not valid.");
	    	}
    	}
		
	    private SPA createSpa(final PersistenceService persistenceService) {
			final ImporterSupport importers = createDefaultImporters();
			final FileBasedExporterSupport exporters = createDefaultExporters();
			return new SPA(persistenceService, importers, exporters);
		}
		
	    private ImporterSupport createDefaultImporters(){
			final ImporterSupport importerSupport = new AnyImporterSupport();
			importerSupport.addImporter(new BPMN20Importer(namespace), "BPMN2");
			importerSupport.addImporter(new XSDImporter(), "XSD");
			importerSupport.addImporter(new XESImporter(), "XES");
			importerSupport.addImporter(new RDFImporter(), "RDF");
			return importerSupport;
		}
		
	    private FileBasedExporterSupport createDefaultExporters(){
			final FileBasedExporterSupport exporters = new FileBasedExporterSupport();
			exporters.addExporter(new BPMN20Exporter(namespace), "BPMN2");
			exporters.addExporter(new RDFExporter(), "RDF");
			exporters.addExporter(new XESExporter(), "XES");
			return exporters;
		}
	}
	
}
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
	public LocalBuilder local() {
		return this.new LocalBuilder();
	}
	
	/**
	 * Provide access to all configurations that
	 * use remote connections to get and save triples.
	 * @return An instance of a remote builder
	 */
	public RemoteBuilder remote(){
        return this.new RemoteBuilder();
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
	     * a SPA instance that uses Random Access Memory
	     * to save triples, thus it is non persistent.
	     */
	    public class UniqueMemoryBuilder extends Builder {
	    	
	    	protected void validateConfigurationParameters() {
	    	}
	    	
	    	protected PersistenceService getPersistenceService() {
	    		return new PersistenceService(JenaTDBStore.withUniqueMemoryLocation());
	    	}
	    	
	    }
	    
	    /**
	     * @TODO Provide description.
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
	     * a SPA instance that uses the local file
	     * system in order to persist triples.
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
 
	abstract class Builder {
		
		private static final String LOCAL_INDIVIDUAL_NAMESPACE = "http://www.uni-mannheim/spa/local/bpmn/";
		
		/**
		 * Create the SPA instance based on the set configuration
		 * parameters. If any configuration is not valid an
		 * IllegalArgumentException is thrown.
		 * In case of a remote connection also an IOException can occur
		 * if the connection could not be set up.
		 * @return
		 */
		public SPA build() {
			validateConfigurationParameters();
			return createSpa(getPersistenceService());
	    }
		
		protected abstract void validateConfigurationParameters();
		
		protected abstract PersistenceService getPersistenceService();
		
	    private SPA createSpa(final PersistenceService persistenceService) {
			final ImporterSupport importers = createDefaultImporters();
			final FileBasedExporterSupport exporters = createDefaultExporters();
			return new SPA(persistenceService, importers, exporters);
		}
		
	    private ImporterSupport createDefaultImporters(){
			final ImporterSupport importerSupport = new AnyImporterSupport();
			importerSupport.addImporter(new BPMN20Importer(LOCAL_INDIVIDUAL_NAMESPACE), "BPMN2");
			importerSupport.addImporter(new XSDImporter(), "XSD");
			importerSupport.addImporter(new XESImporter(), "XES");
			importerSupport.addImporter(new RDFImporter(), "RDF");
			return importerSupport;
		}
		
	    private FileBasedExporterSupport createDefaultExporters(){
			final FileBasedExporterSupport exporters = new FileBasedExporterSupport();
			exporters.addExporter(new BPMN20Exporter(LOCAL_INDIVIDUAL_NAMESPACE), "BPMN2");
			exporters.addExporter(new RDFExporter(), "RDF");
			exporters.addExporter(new XESExporter(), "XES");
			return exporters;
		}
	}
	
}
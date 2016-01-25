package de.unima.core.application.local;

import java.nio.file.Paths;

import org.apache.commons.lang3.NotImplementedException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

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
import de.unima.core.persistence.local.LocalPersistenceService;
import de.unima.core.persistence.local.LocalPersistenceServiceTest;
import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

public class SPABuilder {
 
	public LocalBuilder local() {
		return this.new LocalBuilder();
	}
	
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
	    
	    class UniqueMemoryBuilder extends Builder {
	    	
	    	protected void validateConfigurationParameters() {
	    	}
	    	
	    	protected LocalPersistenceService getPersistenceService() {
	    		return new LocalPersistenceService(JenaTDBStore.withUniqueMemoryLocation());
	    	}
	    	
	    }
	    
	    class SharedMemoryBuilder extends Builder {

	    	protected void validateConfigurationParameters() {
	    	}
	    	
	    	protected LocalPersistenceService getPersistenceService() {
	    		return new LocalPersistenceService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
	    	}
	    	
	    }
	    
	    class FolderBuilder extends Builder {
	    	
	    	private String pathToFolder;
	    	
	    	public FolderBuilder(String pathToFolder) {
	    		this.pathToFolder = pathToFolder;
	    	}
	    	
	    	protected void validateConfigurationParameters() {
	    		Preconditions.checkNotNull(pathToFolder);
	    	}
	    	
	    	protected LocalPersistenceService getPersistenceService() {
	    		return new LocalPersistenceService(JenaTDBStore.withFolder(Paths.get(pathToFolder)));
	    	}
	    	
	    }
	}
	
	class RemoteBuilder {
	    public VirtuosoBuilder virtuoso(){
	    	return new VirtuosoBuilder();
	    }

	    class VirtuosoBuilder extends Builder {
	    	
	    	private String url;
	    	private String user;
	        private String password;
	        
	        
	        protected void validateConfigurationParameters() {
	    		Preconditions.checkNotNull(url);
	    		Preconditions.checkArgument(!Strings.isNullOrEmpty(user), "User is not set or empty.");
	    		Preconditions.checkNotNull(password);
	    	}
	    	
	    	protected LocalPersistenceService getPersistenceService() {
	    		throw new NotImplementedException("Virtuoso Builder is not supported yet.");
	    	}
	        
	        public VirtuosoBuilder url(String url){
	            this.url = url;
	            return this;
	        }
	        
	        public VirtuosoBuilder user(String user){
	            this.user = user;
	            return this;
	        }

	        public VirtuosoBuilder password(String pasword){
	            this.password = pasword;
	            return this;
	        }      
	    
	    }
	}
 
	abstract class Builder {
		
		private static final String LOCAL_INDIVIDUAL_NAMESPACE = "http://www.uni-mannheim/spa/local/bpmn/";
		
		public LocalSPA build() {
			validateConfigurationParameters();
			return createSpa(getPersistenceService());
	    }
		
		protected abstract void validateConfigurationParameters();
		
		protected abstract LocalPersistenceService getPersistenceService();
		
	    private LocalSPA createSpa(final LocalPersistenceService persistenceService) {
			final ImporterSupport importers = createDefaultImporters();
			final FileBasedExporterSupport exporters = createDefaultExporters();
			return new LocalSPA(persistenceService, importers, exporters);
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



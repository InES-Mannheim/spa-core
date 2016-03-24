package de.unima.core.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

public class PersistenceServiceFactory {
    
    public static PersistenceService withDataInSharedMemory(){
        return new PersistenceService(JenaTDBStore.withCommonMemoryLocation(StoreSupport.commonMemoryLocation));
    }
    
    public static PersistenceService withDataInUniqueMemory(){
        return new PersistenceService(JenaTDBStore.withUniqueMemoryLocation());
    }
    
    public static PersistenceService withDataInFolder(String pathToFolder){
        return withDataInFolder(Paths.get(pathToFolder));
    }
    
    public static PersistenceService withDataInFolder(Path pathToFolder){
        return new PersistenceService(JenaTDBStore.withFolder(pathToFolder));
    }
    
    public static PersistenceService withDataAtVirtuoso(String url, String username, String password) {
    	return new PersistenceService(JenaTDBStore.withVirtuoso(url, username, password));
    }
    
}
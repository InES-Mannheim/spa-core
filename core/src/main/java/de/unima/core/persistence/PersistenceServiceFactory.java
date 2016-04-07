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
package de.unima.core.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.unima.core.storage.StoreSupport;
import de.unima.core.storage.jena.JenaTDBStore;

/**
 * Factory different {@link PersistenceService}.
 *
 * <p> Supports 4 different storage locations:
 * <ul>
 *   <li>Shared Memory: Instances of {@code PersistenceService} share the same memory location.
 *   <li>Unique Memory: Each instance has its own memory location.
 *   <li>Data in folder: Instance stores the data in the given folder.
 *   <li>Virtuoso: Instance stores its data at given Virtuoso server.
 * </ul>
 */
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
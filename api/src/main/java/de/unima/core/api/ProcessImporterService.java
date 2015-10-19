package de.unima.core.api;

import java.util.List;

/**
 * Service for importing BPMN data into the application
 * 
 * @author Christian
 *
 */
public interface ProcessImporterService {
    
    /**
     * Saves process retrieved from Source and returns a generated ProcessId.
     * The ProcessId is a URI. It is unique within the whole application.
     * 
     * @param projectId the project in which context this process should be generated
     * @param source which contains the process in BPMN format
     * @return unique process ID
     */
    String save(String projectId, Source source);
    
    /**
     * Retrieves Process identified by given ProcessId.
     * 
     * @param processId unique id of the process
     * @return Source with Process as BPMN file
     */
    Source getById(String processId);
    
    /**
     * Retrieves all process ids found for the given project.
     * 
     * @param projectId id of the project
     * @return list of process ids
     */
    List<String> getAll(String projectId);
    
    /**
     * Deletes process identified by given Id.
     * 
     * @param id of the Process
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(String id);
    
    /**
     * Updates given process identified by given id with the content contained
     * in the source.
     * 
     * @param id of the process
     * @return true if successful, false otherwise
     */
    boolean updateById(String id, Source source);
    
}

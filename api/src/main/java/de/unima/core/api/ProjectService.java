package de.unima.core.api;

import java.util.List;


/**
 * Service for managing projects
 * 
 * @author Christian
 *
 */
public interface ProjectService {

  /**
   * Creates new project and returns unique id.
   * 
   * @param type of the project which should be created 
   * @return unique project id
   * @see {@link ProjectType}
   */
  String create(ProjectType type);
  
  /**
   * Deletes project identified by given id.
   * 
   * @param processId id of the project
   * @return true if successful, false otherwise
   */
  boolean delete(String processId);
  
  /**
   * Retrieves all project ids known to the application
   * 
   * @return ids of projects
   */
  List<String> listAll();
  
}

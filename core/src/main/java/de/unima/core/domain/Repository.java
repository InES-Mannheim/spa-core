package de.unima.core.domain;

import java.util.Optional;
import java.util.Set;

import de.unima.core.persistence.Entity;

/**
 * A repository contains multiple independent projects.  
 */
public interface Repository extends Entity<String>{
	/**
	 * Creates a new project.
	 * 
	 * @param id of the project
	 * @return true if successful; false otherwise
	 */
	public boolean createProject(String id);
	
	/**
	 * Finds the {@link Project} with given id.
	 * 
	 * @param id of the project
	 * @return the project if found; false otherwise
	 */
	public Optional<Project> findProjectById(String id);
	
	/**
	 * Returns all projects within this repository.
	 * 
	 * @return all projects in this repository
	 */
	public Set<Project> getProjects();
}

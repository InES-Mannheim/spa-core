package de.unima.core.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import de.unima.core.persistence.AbstractEntity;

/**
 * A {@code Repository} is a container for multiple independent {@link Project}s.
 * Further, it contains multiple {@link Schema}s which may be linked to projects.
 */
public class Repository extends AbstractEntity<String> {

	final Map<String, Project> projects;
	final Map<String, Schema> schemas;
	
	public Repository(String id) {
		super(id);
		projects = Maps.newHashMap();
		schemas = Maps.newHashMap();
	}

	/**
	 * Adds a new project.
	 * 
	 * @param project
	 *            which should be added
	 * @return true if successful; false otherwise
	 */
	public boolean addProject(Project project){
		projects.put(project.getId(), project);
		return true;
	}

	/**
	 * Finds the {@link Project} with given id.
	 * 
	 * @param id
	 *            of the project
	 * @return the project if found; false otherwise
	 */
	public Optional<Project> findProjectById(String id){
		return Optional.ofNullable(projects.get(id));
	}

	/**
	 * Returns all projects within this repository.
	 * 
	 * @return all projects in this repository
	 */
	public List<Project> getProjects(){
		return ImmutableList.<Project>builder().addAll(projects.values()).build();
	}

	/**
	 * Removes given project from this repository.
	 * 
	 * @param id of the project
	 * @return the removed project
	 */
	public Optional<Project> removeProject(String id){
		return Optional.ofNullable(projects.remove(id));
	}
	
	/**
	 * Adds a new schema.
	 * 
	 * @param schema
	 *            which should be added
	 * @return true if successful; false otherwise
	 */
	public boolean addSchema(Schema schema){
		schemas.put(schema.getId(), schema);
		return true;
	}

	/**
	 * Finds the {@link Schema} with given id.
	 * 
	 * @param id
	 *            of the schema
	 * @return the schema if found; false otherwise
	 */
	public Optional<Schema> findSchemaById(String id){
		return Optional.ofNullable(schemas.get(id));
	}

	/**
	 * Returns all schemas within this repository.
	 * 
	 * @return all schemas in this repository
	 */
	public List<Schema> getSchemas(){
		return ImmutableList.<Schema>builder().addAll(schemas.values()).build();
	}

	/**
	 * Removes given schema from this repository.
	 * 
	 * The schema will be unlinked from all {@code Project}s. 
	 * 
	 * @param id of the schema 
	 * @return the removed schema
	 */
	public Optional<Schema> removeSchema(String id){
		return Optional.ofNullable(schemas.remove(id));
	}
}

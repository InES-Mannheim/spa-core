package de.unima.core.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * A {@code Repository} is a container for multiple independent {@link Project}s.
 * Further, it contains multiple {@link Schema}s which may be linked to projects.
 */
public class Repository extends AbstractEntity<String> {

	private final Map<String, Project> projects;
	private final Map<String, Schema> schemas;
	
	public Repository(String id) {
		this(id, Collections.emptyList(), Collections.emptyList());
	}
	
	public Repository(String id, List<Project> projects, List<Schema> schemas){
		super(id);
		this.projects = Maps.newHashMap();
		for(Project project:projects){
			this.projects.put(project.getId(), project);
		}
		this.schemas = Maps.newHashMap();
		for(Schema schema:schemas){
			this.schemas.put(schema.getId(), schema);
		}
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

package de.unima.core.application;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import de.unima.core.persistence.local.LocalPeristenceService;

public interface SPA {
	
	/**
	 * Creates a new {@link Project} with generated URI.
	 * 
	 * <p>The project and changes to the repository are persisted.
	 * 
	 * @param label
	 *            of the new project
	 * 
	 * @return new {@code Project} with generated id
	 */
	Project createProject(String label);

	/**
	 * Finds all projects.
	 * 
	 * <p>{@code DataPool}s and linked {@code Schema}s of each project are not
	 * loaded. To fully load a project use
	 * {@link LocalPeristenceService#findProjectById(String)};
	 * 
	 * @return list of persisted projects
	 */
	List<Project> findAllProjects();

	/**
	 * Finds project with given id.
	 * 
	 * <p> All {@code DataPool}s and linked {@code Schema}s of the project are
	 * loaded.
	 * 
	 * @param id
	 *            as URI; for example: http://www.test.com/1
	 * @return project if found; empty otherwise
	 */
	Optional<Project> findProjectById(String id);

	/**
	 * Saves given project.
	 * 
	 * <p> This action also saves all contained {@code DataPool}s.
	 * 
	 * @param project
	 *            which should be saved
	 * @return id of the project
	 * @throws IllegalStateException
	 *             if project could not be saved
	 */
	String saveProject(Project project);

	/**
	 * Deletes given project.
	 * 
	 * <p> All schemas linked to this project are unlinked. Further, all contained
	 * data pools and buckets are removed.
	 * 
	 * @param project
	 *            which should be deleted
	 */
	void deleteProject(Project project);
	

	Schema importSchema(File input, String format, String label);

	/**
	 * Unlinks given schema from all affected projects and deletes the content.
	 * 
	 * <p><b>Note:</b> Affected projects need to be reloaded.
	 * 
	 * @param schema
	 *            which should be removed
	 */
	void deleteSchema(Schema schema);

	/**
	 * Finds all {@code Schema}s.
	 * 
	 * @return list of persisted {@code Schema}s
	 */
	List<Schema> findAllSchemas();

	/**
	 * Finds {@code Schema} by id.
	 * 
	 * The id must be an URI (e.g. http://www.test.com/1)
	 * 
	 * @param id of the Schema as URI
	 * @return found {@code Schema}; empty otherwise
	 */
	Optional<Schema> findSchemaById(String id);

	/**
	 * Exports data stored for given {@code Schema}.
	 * 
	 * @param schema which data should be returned
	 * @return the data if present otherwise empty
	 */
	OutputStream exportSchema(Schema schema, String format);

	/**
	 * Creates a {@link DataPool} with generated Id and adds it to the given
	 * project.
	 * 
	 * <p>
	 * <b>Note:</b> The changes to the project and the new {@code DataPool} are
	 * persisted.
	 * 
	 * @param project
	 *            to add the created pool
	 * @param label
	 *            of the new pool
	 * @return new {@link DataPool}
	 */
	DataPool createDataPool(Project project, String label);

	/**
	 * Saves given {@code DataPool}.
	 * 
	 * @param dataPool
	 *            which should be saved
	 * @return id of the pool
	 */
	String saveDataPool(DataPool dataPool);

	/**
	 * Finds all {@code DataPool}s.
	 * 
	 * <p>
	 * <b>Note:</b> The labels of the contained {@code DataBucket}s are not
	 * loaded.
	 * 
	 * @return list of persistent {@code DataPool}s
	 */
	List<DataPool> findAllDataPools();

	/**
	 * Finds {@code DataPool} by id and all contained data buckets.
	 *
	 * <p>
	 * <b>Note:</b> Each found data pool refers to the project it belongs to.
	 * Thus, {@code DataPool#getProject()} is not null. However, the project is
	 * not fully loaded and should not be saved. To load the project, see
	 * {@link LocalPeristenceService#findProjectById(String)}.
	 * 
	 * @param id
	 *            of the pool
	 * @return found {@code DataPool}; empty otherwise
	 */
	Optional<DataPool> findDataPoolById(String id);

	/**
	 * Deletes given {@code DataPool}. This includes, the deletion of all
	 * contained {@code DataBucket}s.
	 * 
	 * @param dataPool
	 *            which should be deleted
	 */
	void deleteDataPool(DataPool dataPool);

	/**
	 * Imports data as new {@code DataBucket} into given {@code DataPool} and
	 * returns a generated Id.
	 * 
	 * <p>
	 * <b>Note:</b> Changes made to given {@code DataPool} are persisted.
	 * Further, the created {@code DataBucket} is also persisted.
	 * 
	 * @param input
	 *            data
	 * @param format
	 *            of the data
	 * @param label
	 *            of the new {@code DataBucket}
	 * @param dataPool
	 *            of the new {@code DataBucket}
	 * @return created {@code DataBucket}
	 * @throws IllegalStateException
	 *             if the data could not be stored
	 */
	DataBucket importData(File input, String format, String label, DataPool dataPool);

	/**
	 * Removes given {@code DataBucket}.
	 * 
	 * @param dataBucket
	 *            which should be removed
	 */
	void removeDataBucket(DataPool dataPool, DataBucket dataBucket);

	/**
	 * Exports data stored for given {@code DataBucket}.
	 * 
	 * @param bucket
	 *            which data should be returned
	 * @return the data if present; empty otherwise
	 */
	OutputStream exportData(DataBucket bucket);
	
	/**
	 * Lists all supported import formats.
	 *  
	 * @return list of supported import formats
	 */
	List<String> getSupportedImportFormats();
	
}
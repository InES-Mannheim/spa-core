package de.unima.core.application;

import java.io.File;
import java.util.List;
import java.util.Optional;

import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;

public interface SPA {
	
	/**
	 * Creates a new {@link Project} with generated URI.
	 * 
	 * <p>The {@code Project} and changes to the repository are persisted.
	 * 
	 * @param label
	 *            of the new {@code Project}
	 * 
	 * @return new {@code Project} with generated id
	 */
	Project createProject(String label);

	/**
	 * Finds all {@link Project}s.
	 * 
	 * <p>{@link DataPool}s and linked {@link Schema}s of each {@code Project} are not
	 * loaded. To fully load a {@code Project} use
	 * {@link SPA#findProjectById(String)};
	 * 
	 * @return list of persisted {@code Project}s
	 */
	List<Project> findAllProjects();

	/**
	 * Finds {@link Project} with given id.
	 * 
	 * <p> All {@link DataPool}s and linked {@link Schema}s of the {@code Project} are
	 * loaded.
	 * 
	 * @param id
	 *            as URI; for example: http://www.test.com/1
	 * @return {@code Project} if found; empty otherwise
	 */
	Optional<Project> findProjectById(String id);

	/**
	 * Saves given {@link Project}.
	 * 
	 * <p> This action also saves all contained {@code DataPool}s.
	 * 
	 * @param project
	 *            which should be saved
	 * @return id of the {@code Project}
	 * @throws IllegalStateException
	 *             if {@code Project} could not be saved
	 */
	String saveProject(Project project);

	/**
	 * Deletes given {@link Project}.
	 * 
	 * <p> All schemas linked to this {@code Project} are unlinked. Further, all contained
	 * {@link DataPool}s and {@code DataBucket} are removed.
	 * 
	 * @param project
	 *            which should be deleted
	 */
	void deleteProject(Project project);
	
	/**
	 * Imports data as new {@link Schema} and generates an Id.
	 * 
	 * <p>
	 * <b>Note:</b> The created {@code Schema} is persisted.
	 * 
	 * @param input
	 *            schema
	 * @param format
	 *            of the schema
	 * @param label
	 *            of the new {@code Schema}
	 * @return created {@code Schema}
	 * @throws IllegalArgumentException
	 *             if the format is not supported
	 */
	Schema importSchema(File input, String format, String label);

	/**
	 * Unlinks given schema from all affected {@link Project}s and deletes the content.
	 * 
	 * <p><b>Note:</b> Affected {@code Project}s need to be reloaded.
	 * 
	 * @param schema
	 *            which should be removed
	 */
	void deleteSchema(Schema schema);

	/**
	 * Finds all {@link Schema}s.
	 * 
	 * @return list of persisted {@code Schema}s
	 */
	List<Schema> findAllSchemas();

	/**
	 * Finds {@link Schema} by id.
	 * 
	 * <p><b>Note:</b> The id must be an URI (e.g. http://www.test.com/1)
	 * 
	 * @param id of the Schema as URI
	 * @return found {@code Schema}; empty otherwise
	 */
	Optional<Schema> findSchemaById(String id);

	/**
	 * Exports data stored for given {@link Schema}.
	 * 
	 * @param schema
	 *            which data should be returned
	 * @param target
	 *            where to write the result. For some exporters this might also
	 *            be a directory where multiple files are exported to.
	 * @return the data if present otherwise empty
	 */
	File exportSchema(Schema schema, String format, File target);

	/**
	 * Creates a {@link DataPool} with generated Id and adds it to the given
	 * {@link Project}.
	 * 
	 * <p>
	 * <b>Note:</b> The changes to the {@code Project} and the new {@code DataPool} are
	 * persisted.
	 * 
	 * @param project
	 *            to add the created pool
	 * @param label
	 *            of the new pool
	 * @return new {@code DataPool}
	 */
	DataPool createDataPool(Project project, String label);

	/**
	 * Saves given {@link DataPool}.
	 * 
	 * @param dataPool
	 *            which should be saved
	 * @return id of the {@code DataPool}
	 */
	String saveDataPool(DataPool dataPool);

	/**
	 * Finds all {@link DataPool}s.
	 * 
	 * <p>
	 * <b>Note:</b> The labels of the contained {@link DataBucket}s are not
	 * loaded.
	 * 
	 * @return list of persistent {@code DataPool}s
	 */
	List<DataPool> findAllDataPools();

	/**
	 * Finds {@link DataPool} by id and all contained {@link DataBucket}.
	 *
	 * <p>
	 * <b>Note:</b> Each found data pool refers to the {@link Project} it belongs to.
	 * Thus, {@code DataPool#getProject()} is not null. However, the {@code Project} is
	 * not fully loaded and should not be saved. To load the {@code Project}, see
	 * {@link SPA#findProjectById(String)}.
	 * 
	 * @param id
	 *            of the {@code DataPool}
	 * @return found {@code DataPool}; empty otherwise
	 */
	Optional<DataPool> findDataPoolById(String id);

	/**
	 * Deletes given {@link DataPool}. This includes, the deletion of all
	 * contained {@link DataBucket}s.
	 * 
	 * @param dataPool
	 *            which should be deleted
	 */
	void deleteDataPool(DataPool dataPool);

	/**
	 * Imports data as new {@link DataBucket} into given {@link DataPool} and
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
	 * @param format TODO
	 * @param target TODO
	 * @return the data if present; empty otherwise
	 */
	File exportData(DataBucket bucket, String format, File target);
	
	/**
	 * Lists all supported import formats.
	 *  
	 * @return list of supported import formats
	 */
	List<String> getSupportedImportFormats();

	/**
	 * Lists all supported export formats.
	 *  
	 * @return list of supported export formats
	 */
	List<String> getSupportedExportFormats();
	
}
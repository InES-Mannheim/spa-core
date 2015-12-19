package de.unima.core.domain.service;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;

import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;

/**
 * Provides persistence functionality over the domain.
 */
public interface PersistenceService {

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
	Project createPersistentProjectWithGeneratedId(String label);

	/**
	 * Finds all projects.
	 * 
	 * <p>{@code DataPool}s and linked {@code Schema}s of each project are not
	 * loaded. To fully load a project use
	 * {@link PersistenceService#findProjectById(String)};
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
	 * @return number of deleted statements
	 */
	long deleteProject(Project project);

	/**
	 * Adds given schema data as new schema to the given repository and returns
	 * a generated schema Id. 
	 * 
	 * <p> Changes made to the repository and the created schema are persisted.
	 * 
	 * @param label
	 *            of the new schema
	 * @param data
	 *            containing RDF
	 * @return created schema
	 * @throws IllegalStateException
	 *             if schema data could not be stored
	 */
	Schema addDataAsNewSchema(String label, Model data);

	/**
	 * Replaces data of given schema with given data.
	 * 
	 * @param schema
	 *            which data should be replaced
	 * @param data
	 *            containing RDF
	 * @return saved schema
	 * @throws IllegalStateException
	 *             if the schema data could not be stored
	 */
	Schema replaceDataOfSchema(Schema schema, Model data);

	/**
	 * Unlinks given schema from all affected projects and deletes the content.
	 * 
	 * <p><b>Note:</b> Affected projects need to be reloaded.
	 * 
	 * @param schema
	 *            which should be removed
	 * @return number of statements which have been deleted
	 */
	long deleteSchema(Schema schema);

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
	 * Finds data stored for given {@code Schema}.
	 * 
	 * @param schema which data should be returned
	 * @return the data if present; empty otherwise
	 */
	Optional<Model> findDataOfSchema(Schema schema);

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
	DataPool createPeristentDataPoolForProjectWithGeneratedId(Project project, String label);

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
	 * {@link PersistenceService#findProjectById(String)}.
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
	 * Adds given data as new {@code DataBucket} to the given {@code DataPool}
	 * and returns a generated Id.
	 * 
	 * <p>
	 * <b>Note:</b> Changes made to given {@code DataPool} are persisted.
	 * Further, the created {@code DataBucket} is also persisted.
	 * 
	 * @param dataPool
	 *            of the new {@code DataBucket}
	 * 
	 * @param label
	 *            of the new {@code DataBucket}
	 * @param data
	 *            containing RDF
	 * @return created {@code DataBucket}
	 * @throws IllegalStateException
	 *             if the data could not be stored
	 */
	DataBucket addDataAsNewDataBucketToDataPool(DataPool dataPool, String label, Model data);

	/**
	 * Replaces data of given  {@code DataBucket}t with given data.
	 * 
	 * @param bucket
	 *            which data should be replaced
	 * @param data
	 *            containing RDF
	 * @return saved bucket
	 * @throws IllegalStateException
	 *             if the bucket data could not be stored
	 */
	DataBucket replaceDataBucketWithData(DataBucket bucket, Model data);

	/**
	 * Removes given {@link DataBucket}.
	 * 
	 * @param dataPool of the {@code DataBucket} 
	 * 
	 * @param dataBucket
	 *            which should be removed
	 * @return number of statements which have been deleted
	 */
	long removeDataBucketFromDataPool(DataPool dataPool, DataBucket dataBucket);

	/**
	 * Finds data stored for given {@code DataBucket}.
	 * 
	 * @param bucket
	 *            which data should be returned
	 * @return the data if present; empty otherwise
	 */
	Optional<Model> findDataOfDataBucket(DataBucket bucket);

}
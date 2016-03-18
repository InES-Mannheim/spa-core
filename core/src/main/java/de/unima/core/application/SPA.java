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
package de.unima.core.application;

import de.unima.core.domain.model.*;
import de.unima.core.io.Importer;
import de.unima.core.io.ImporterSupport;
import de.unima.core.io.Key;
import de.unima.core.io.file.FileBasedExporterSupport;
import de.unima.core.persistence.PersistenceService;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class SPA {

	private final PersistenceService persistenceService;
	private final ImporterSupport importerSupport;
	private final FileBasedExporterSupport exporterSupport;
	
	public SPA(PersistenceService persistenceService, ImporterSupport importerSupport, FileBasedExporterSupport exporterSupport) {
		this.persistenceService = persistenceService;
		this.importerSupport = importerSupport;
		this.exporterSupport = exporterSupport;
	}

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
	public Project createProject(String label) {
		return persistenceService.createPersistentProjectWithGeneratedId(label);
	}

	/**
	 * Finds all {@link Project}s.
	 * 
	 * <p>{@link DataPool}s and linked {@link Schema}s of each {@code Project} are not
	 * loaded. To fully load a {@code Project} use
	 * {@link SPA#findProjectById(String)};
	 * 
	 * @return list of persisted {@code Project}s
	 */
	public List<Project> findAllProjects() {
		return persistenceService.findAllProjects();
	}

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
	public Optional<Project> findProjectById(String id) {
		return persistenceService.findProjectById(id);
	}

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
	public String saveProject(Project project) {
		return persistenceService.saveProject(project);
	}

	/**
	 * Deletes given {@link Project}.
	 * 
	 * <p> All schemas linked to this {@code Project} are unlinked. Further, all contained
	 * {@link DataPool}s and {@code DataBucket} are removed.
	 * 
	 * @param project
	 *            which should be deleted
	 */
	public void deleteProject(Project project) {
		persistenceService.deleteProject(project);
	}

	/**
	 * Unlinks given schema from all affected {@link Project}s and deletes the content.
	 * 
	 * <p><b>Note:</b> Affected {@code Project}s need to be reloaded.
	 * 
	 * @param schema
	 *            which should be removed
	 */
	public void deleteSchema(Schema schema) {
		persistenceService.deleteSchema(schema);
	}

	/**
	 * Finds all {@link Schema}s.
	 * 
	 * @return list of persisted {@code Schema}s
	 */
	public List<Schema> findAllSchemas() {
		return persistenceService.findAllSchemas();
	}

	/**
	 * Finds {@link Schema} by id.
	 * 
	 * <p><b>Note:</b> The id must be an URI (e.g. http://www.test.com/1)
	 * 
	 * @param id of the Schema as URI
	 * @return found {@code Schema}; empty otherwise
	 */
	public Optional<Schema> findSchemaById(String id) {
		return persistenceService.findSchemaById(id);
	}

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
	public DataPool createDataPool(Project project, String label) {
		return persistenceService.createPeristentDataPoolForProjectWithGeneratedId(project, label);
	}

	/**
	 * Saves given {@link DataPool}.
	 * 
	 * @param dataPool
	 *            which should be saved
	 * @return id of the {@code DataPool}
	 */
	public String saveDataPool(DataPool dataPool) {
		return persistenceService.saveDataPool(dataPool);
	}

	/**
	 * Finds all {@link DataPool}s.
	 * 
	 * <p>
	 * <b>Note:</b> The labels of the contained {@link DataBucket}s are not
	 * loaded.
	 * 
	 * @return list of persistent {@code DataPool}s
	 */
	public List<DataPool> findAllDataPools() {
		return persistenceService.findAllDataPools();
	}

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
	public Optional<DataPool> findDataPoolById(String id) {
		return persistenceService.findDataPoolById(id);
	}

	/**
	 * Deletes given {@link DataPool}. This includes, the deletion of all
	 * contained {@link DataBucket}s.
	 * 
	 * @param dataPool
	 *            which should be deleted
	 */
	public void deleteDataPool(DataPool dataPool) {
		persistenceService.deleteDataPool(dataPool);
	}

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
	public Schema importSchema(File input, String format, String label) {
		return importFile(input, format, data -> persistenceService.addDataAsNewSchema(label, data)); 
	}

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
	public DataBucket importData(File input, String format, String label, DataPool dataPool) {
		return importFile(input, format, data -> persistenceService.addDataAsNewDataBucketToDataPool(dataPool, label, data));
	}
	
	private <T extends Entity<String>, R extends Model> T importFile(File input, String format, Function<R, T> dataToDomainObject) {
		final Optional<Importer<File, R>> importer = importerSupport.findImporterByKey(Key.of(format));
		return importer.map(imp -> imp.importData(input))
				.map(dataToDomainObject)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Format '%s' is not supported. Must be one of %s.", format, importerSupport.listKeysAsString())));
	}

	/**
	 * Removes given {@code DataBucket}.
	 * 
	 * @param dataPool
	 *            where the bucket should be removed from	
	 * @param dataBucket
	 *            which should be removed
	 */
	public void removeDataBucket(DataPool dataPool, DataBucket dataBucket) {
		persistenceService.removeDataBucketFromDataPool(dataPool, dataBucket);
	}
	
	/**
	 * Exports data stored for given {@link Schema}.
	 * 
	 * @param schema
	 *            which data should be returned
	 * @param format
	 *            of the exported file
	 * @param target
	 *            where to write the result. For some exporters this might also
	 *            be a directory where multiple files are exported to.
	 * @return the data if present otherwise empty
	 */
	public File exportSchema(Schema schema, String format, File target) {
		return exportFile(() -> retrieveData(schema), format, target);	
	}

	private Model retrieveData(Schema schema) {
		return persistenceService.findDataOfSchema(schema)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Could not find data for schema '%s'", schema)));
	}

	/**
	 * Exports data stored for given {@code DataBucket}.
	 *
	 * @param bucket which data should be returned
	 * @param format of the file content
	 * @param target where to write the result. For some exporters this might also
	 *               be a directory where multiple files are exported to.
	 * @return the data if present; empty otherwise
	 */
	public File exportData(DataBucket bucket, String format, File target) {
		return exportFile(() -> retrieveData(bucket), format, target);
	}
	
	private Model retrieveData(DataBucket bucket) {
		return persistenceService.findDataOfDataBucket(bucket)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Could not find data for bucket '%s'", bucket)));
	}

	private File exportFile(Supplier<Model> modelSupplier, String format, File target) {
		return exporterSupport.findExporterByKey(Key.of(format))
				.map(exporter -> exporter.exportToFile(modelSupplier.get(), target))
				.orElseThrow(() -> new IllegalArgumentException(String.format("Could not find exporter for format '%s'", format)));
	}

	/**
	 * Lists all supported import formats.
	 *  
	 * @return list of supported import formats
	 */
	public List<String> getSupportedImportFormats() {
		return importerSupport.listKeysAsString();
	}
	
	/**
	 * Lists all supported export formats.
	 *  
	 * @return list of supported export formats
	 */
	public List<String> getSupportedExportFormats(){
		return exporterSupport.listKeysAsString();
	}
}
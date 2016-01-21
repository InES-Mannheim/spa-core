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
package de.unima.core.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.Maps;

import com.google.common.collect.ImmutableList;

/**
 * A {@code DataPool} represents a pool of {@link DataBucket}s.
 * 
 * A {@link Project} may contain several {@code DataPool}s. In this way, an
 * additional logical distinction between different datasets is introduced. All
 * data in a {@code DataPool} must adhere to the project {@link Schema}s.
 * 
 * A {@code DataBucket} is an aggregate root for {@code DataBucket}s.
 */
public class DataPool extends AbstractEntity<String> {

	private final Map<String, DataBucket> buckets;
	private Project project;
	
	public DataPool(String id){
		this(id, null, null);
	}
	
	public DataPool(String id, String label){
		this(id, label, null);
	}
	
	public DataPool(String id, Project project) {
		this(id, null, project);
	}
	
	public DataPool(String id, String label, Project project) {
		this(id, label, project, Collections.emptyList());
	}
	
	public DataPool(String id, String label, Project project, List<DataBucket> buckets){
		super(id, label);
		this.project = project;
		this.buckets = Maps.newHashMap();
		for(DataBucket bucket: buckets){
			this.buckets.put(bucket.getId(), bucket);
		}
	}

	/**
	 * Adds given {@link DataBucket} to this pool.
	 * 
	 * @param bucket
	 *            which should be added
	 * @return true if successful; false otherwise
	 */
	public boolean addDataBucket(DataBucket bucket){
		buckets.put(bucket.getId(), bucket);
		return true;
	}

	/**
	 * Removes given {@link DataBucket} from this pool.
	 * 
	 * @param id of the bucket which should be removed
	 * @return true if successful; false otherwise
	 */
	public Optional<DataBucket> removeDataBucketById(String id){
		return Optional.ofNullable(buckets.remove(id));
	}

	/**
	 * Searches for a {@code DataBucket} with given id.
	 * 
	 * @param id of the {@code DataBucket}
	 * @return found {@code DataBucket} or empty 
	 */
	public Optional<DataBucket> findDataBucketById(String id){
		return Optional.ofNullable(buckets.get(id));
	}
	
	/**
	 * Replaces current data buckets with given buckets.
	 * 
	 * @param buckets which should replace current buckets
	 * @return which have been removed from the pool
	 */
	public List<DataBucket> replaceDataBuckets(List<DataBucket> buckets){
		final List<DataBucket> removedBuckets = this.buckets.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
		this.buckets.clear();
		buckets.forEach(bucket -> this.buckets.put(bucket.getId(), bucket));
		removedBuckets.removeAll(buckets);
		return removedBuckets;
	}
    
	/**
	 * Retrieves all {@code DataBucket}s in this pool.
	 * 
	 * @return list of contained data buckets
	 */
	public List<DataBucket> getDataBuckets(){
		return ImmutableList.<DataBucket>builder().addAll(buckets.values()).build();
	}
	
	/**
	 * Set the project this {@code DataPool} belongs to.
	 * 
	 * @param project of this {@code DataPool}
	 */
	public void setProject(Project project) {
		this.project = project;
	}
	
	/**
	 * Returns the project this pool belongs to.
	 * 
	 * @return project of this pool
	 */
	public Project getProject(){
		return project;
	}
}
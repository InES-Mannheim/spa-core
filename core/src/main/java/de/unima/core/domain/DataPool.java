package de.unima.core.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.ext.com.google.common.collect.Maps;

import com.google.common.collect.ImmutableList;

import de.unima.core.persistence.AbstractEntity;

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

	final Map<String, DataBucket> buckets;
	final private Project project;
	
	public DataPool(String id, Project project) {
		this(id, null, project);
	}
	
	public DataPool(String id, String label, Project project) {
		super(id, label);
		this.project = project;
		this.buckets = Maps.newHashMap();
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
	 * Retrieves all {@code DataBucket}s in this pool.
	 * 
	 * @return list of contained data buckets
	 */
	public List<DataBucket> getDataBuckets(){
		return ImmutableList.<DataBucket>builder().addAll(buckets.values()).build();
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
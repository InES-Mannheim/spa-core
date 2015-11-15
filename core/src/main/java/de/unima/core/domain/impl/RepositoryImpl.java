package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;

import de.unima.core.domain.Project;
import de.unima.core.domain.Repository;
import de.unima.core.persistence.AbstractEntity;

public class RepositoryImpl extends AbstractEntity<String> implements Repository {

	private Map<String, Project> projects;
	
	public RepositoryImpl(String id) {
		super(id);
		this.projects = new HashMap<String, Project>();
	}

	@Override
	public boolean createProject(String id) {
		return projects.computeIfAbsent(id, key -> new ProjectImpl(id, this)) != null;
	}

	@Override
	public Optional<Project> findProjectById(String id) {
		return Optional.of(projects.get(id));
	}

	@Override
	public Set<Project> getProjects() {
		return Sets.newHashSet(projects.values());
	}
//
//	@Override
//	public boolean registerDataScheme(String i, IOObject<RDFFile> ioo, Importer<? extends DataSource> imp) {
//		if (this.dataschemes.containsKey(i)) {
//			return false;
//		} else {
//			Schema newDataScheme = new Schema(i, ioo.getData(), this, imp, Repository.fake());
//			if (!newDataScheme.save()) {
//				System.err.println("Unable to store scheme " + i + " while registration.");
//				return false;
//			} else {
//				this.dataschemes.put(i, newDataScheme);
//				return true;
//			}
//		}
//		
//	}
//
//
//	@Override
//	public Schema getDataScheme(String id) {
//
//		return this.dataschemes.get(id);
//	}

}

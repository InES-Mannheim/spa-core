package de.unima.core.service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import de.unima.core.domain.Project;
import de.unima.core.persistence.Entity;
import de.unima.core.persistence.Repository;
import de.unima.core.persistence.transformation.Transformation;
import de.unima.core.storage.Store;
import de.unima.core.storage.jena.JenaTDBStore;

public class StorageService {

	public Repository<Project, String> projectRepository;
	
	public Optional<String> saveProject(Project project){
		return projectRepository.save(project);
	}
	
	public static class ProjectRepository implements Repository<Project, String> {

		final Function<Project, Model> projectToRdf = Transformation
				.map(Project.class)
				.to("http://test.de/Project")
				.withId("id")
				.asLiteral("http://test.de/id")
				.get();
		final Store jenaStore = new JenaTDBStore(Paths.get(System.getProperty("user.dir")+"/database"));
		
		@Override
		public Optional<String> save(Project entity) {
			jenaStore.writeWithConnection(connection -> {
				return connection.as(Dataset.class).map(dataset -> {
					final Model rdf = projectToRdf.apply(entity);
					return dataset.getDefaultModel().add(rdf);
				});
			});
			return null;
		}

		@Override
		public List<Entity<String>> findAll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Entity<String>> findById(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Integer> deleteAll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Integer> deleteById(String id) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}

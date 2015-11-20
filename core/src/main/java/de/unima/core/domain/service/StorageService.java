package de.unima.core.domain.service;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;

import org.hashids.Hashids;

import de.unima.core.domain.Repository;
import de.unima.core.domain.Vocabulary;
import de.unima.core.domain.repository.RepositoryRepository;

public class StorageService {

	private final Random rand;
	private final RepositoryRepository repositoryRepository;

	public StorageService(String repositoryPath) {
		this.rand = new Random();
		this.repositoryRepository = new RepositoryRepository(Optional.ofNullable(repositoryPath).map(Paths::get));
	}

	/**
	 * Generates new {@link Repository} with a generated URI.
	 * 
	 * The repository is <b>not</b> persisted and must be saved explicitly after
	 * creation.
	 * 
	 * @return new {@code Repository} with generated id
	 */
	public Repository createRepositorWithGeneratedId() {
		return new Repository(createId(Vocabulary.Repository));
	}

	private String createId(String uri) {
		final Hashids hashIds = new Hashids(uri);
		return appendSlashIfUriHasNoHashOrSlashEnding(uri) + hashIds.encode(Math.abs(rand.nextInt()));
	}

	private String appendSlashIfUriHasNoHashOrSlashEnding(String uri) {
		final boolean hasSlashOrHash = uri.endsWith("#") || uri.endsWith("/");
		return hasSlashOrHash ? uri : uri + "/";
	}

	/**
	 * Saves given repository.
	 * 
	 * This action is cascading: Saving an repository also saves
	 * all associated schemas and repositories.
	 * 
	 * @param repository which should be saved
	 */
	public Optional<String> saveRepository(Repository repository) {
		return repositoryRepository.save(repository);
	}
}

package de.unima.core.io.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.hashids.Hashids;

import com.google.common.base.Throwables;

import de.unima.core.io.file.xes.OntModelToXLogExporter;

public class XESExporter implements FileBasedExporter<Model> {

	private final Hashids hashIds = new Hashids("XESExporter");
	private final Random rand = new Random();
	private final XesXmlSerializer serializer = new XesXmlSerializer();
	private final OntModelToXLogExporter xesExporter = new OntModelToXLogExporter();
	
	@Override
	public File exportToFile(Model data, File location) {
		final Set<XLog> logs = xesExporter.export(data);
		if(location.isDirectory()){
			return exportLogsToDirectory(logs, location);
		}
		return exportLogToFile(logs, location);
	}

	private File exportLogsToDirectory(Set<XLog> logs, File directory) {
		for(XLog log: logs) {
			writeLogToFile(log, createFileInDirectory(directory));
		}
		return directory;
	}

	private Path createFileInDirectory(File directory) {
		try {
			return Files.createFile(directory.toPath().resolve(createXesFileName()));
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private String createXesFileName() {
		return "xes-exporterd-"+hashIds.encode(rand.nextLong())+".xes";
	}
	
	private File exportLogToFile(Set<XLog> logs, File location) {
		if(logs.isEmpty()) {
			return location;
		}
		if(containsMoreThanOneLog(logs)){
			throw new IllegalArgumentException(String.format("%s is a file. "
					+ "Multiple XES logs cannot be serialized to one file. "
					+ "Please provide a directory instead.", location.toString()));
		}
		writeLogToFile(logs.iterator().next(), location.toPath());
		return location;
	}
	
	private boolean containsMoreThanOneLog(Set<XLog> logs) {
		return logs.size() > 1;
	}

	private void writeLogToFile(XLog log, final Path xesFile) {
		try(final OutputStream out = Files.newOutputStream(xesFile)){
			serializer.serialize(log, out);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

}

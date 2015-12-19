package example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import com.google.common.base.Throwables;

import de.unima.core.application.SPA;
import de.unima.core.application.local.LocalSPA;
import de.unima.core.domain.model.DataBucket;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;

public class XesExample extends BaseExample{

	public static void main(String[] args) throws IOException {
		final SPA spa = LocalSPA.withDataInSharedMemory();
		// Create project which may may contain multiple data buckets and schemas
		final Project project = spa.createProject("Test Project");
		// Import schema for XES
		final Schema schema = spa.importSchema(getFilePath("xes.xsd").toFile(), "XSD", "XES Schema");
		// Save changes made to the project
		project.linkSchema(schema);
		spa.saveProject(project);
		// Create data pool and load xes into it
		final DataPool dataPool = spa.createDataPool(project, "Sample Data Pool");
		final DataBucket bucket = spa.importData(getFilePath("running-example.xes").toFile(), "XES", "running example",
				dataPool);
		// Export xes to new location
		final File exportLocation = Files.createTempFile("example", ".xes").toFile();
		spa.exportData(bucket, "XES", exportLocation);
		// Read created events from spa and the events we expect to see
		final List<XTrace> createdEvents = readLogFromFile(exportLocation).get();
		final List<XTrace> expectedEvents = readLogFromFile(getFilePath("running-example.xes").toFile()).get();
		// Make sure, there is the same amount of events
		if(createdEvents.size() == expectedEvents.size()){
			System.out.println("Works as expected.");
		}
	}
	
	private static Optional<XLog> readLogFromFile(File xesFile) {
		for (XParser parser : XParserRegistry.instance().getAvailable()) {
			if (parser.canParse(xesFile)) {
				try {
					return Optional.ofNullable(parser.parse(xesFile).get(0));
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}
		}
		return Optional.empty();
	}
}

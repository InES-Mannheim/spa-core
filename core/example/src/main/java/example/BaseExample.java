package example;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public abstract class BaseExample {

	protected static Path getFilePath(final String fileName) {
		try {
			return Paths.get(Resources.getResource(fileName).toURI());
		} catch (URISyntaxException e) {
			throw Throwables.propagate(e);
		}
	}
}

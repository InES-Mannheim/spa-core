/*******************************************************************************
 *     Copyright 2016 University of Mannheim
 *  
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *******************************************************************************/
package de.unima.core.io.file;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class BPMN20ExporterImporterTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private BPMN20Exporter exporter;
	private String namespace = "http://test.de/ps#";

	@Before
	public void setUp() {
		this.exporter = new BPMN20Exporter(namespace);
	}

	@Test
	public void allGraphicalElementsExisting() throws IOException {
		File file = getFilePath("Muenster.bpmn").toFile();
		final Model importedBpmn = new BPMN20Importer(namespace)
				.importData(getFilePath("Muenster.bpmn").toFile());		
		final File target = folder.newFile();
		exporter.exportToFile(importedBpmn, target);

		BufferedReader oldFileReader = new BufferedReader(new FileReader(file));
		String oldFileLine;
		while ((oldFileLine = oldFileReader.readLine()) != null) {
			oldFileLine = oldFileLine.trim();
			// test Diagrams
			if (oldFileLine.matches("<bpmndi:BPMNDiagram.*")) {				
				assertTrue(searchAndCheckLine(target, oldFileLine));
			}
			// test Plane
			if (oldFileLine.matches("<bpmndi:BPMNPlane.*")) {
				assertTrue(searchAndCheckLine(target, oldFileLine));
			}
			// test shapes
			if (oldFileLine.matches("<bpmndi:BPMNShape.*")) {
				assertTrue(checkShapes(target, oldFileLine, oldFileReader));
			}

			// test edges
			if (oldFileLine.matches("<bpmndi:BPMNEdge.*")) {
				assertTrue(checkEdges(target, oldFileLine, oldFileReader));
			}
		}
		oldFileReader.close();
	}

	private boolean searchAndCheckLine(File newFile, String oldFileLine) throws IOException {
		BufferedReader newFileReader = new BufferedReader(new FileReader(newFile));
		boolean bool = false;
		String newFileLine;
		while ((newFileLine = newFileReader.readLine()) != null) {
			if (newFileLine.trim().matches(oldFileLine)) {
				bool = true;
				break;
			}
		}
		newFileReader.close();
		return bool;
	}

	private boolean checkShapes(File newFile, String oldFileLine, BufferedReader oldFileReader) throws IOException {
		BufferedReader newFileReader = new BufferedReader(new FileReader(newFile));
		boolean shapeCorrect = false;
		String newFileLine;

		while ((newFileLine = newFileReader.readLine()) != null) {
			newFileLine = newFileLine.trim();
			newFileLine = newFileLine.substring(0, newFileLine.length() - 1);

			// check Shape
			if (oldFileLine.matches(newFileLine + "(\\s.*)?>")) {
				shapeCorrect = true;

				// check Bounds
				assertTrue(checkBounds(oldFileReader, newFileReader));

				// checkLabel
				oldFileLine = oldFileReader.readLine();
				if (oldFileLine.matches(".*BPMNLabel.*")) {
					assertTrue(checkLabel(oldFileLine, oldFileReader, newFileReader));
					// checks the </bpmndi:BPMNShape> element
					assertTrue(newFileReader.readLine().trim().equals(oldFileReader.readLine().trim()));

				} else {
					// checks the </bpmndi:BPMNShape> element
					assertTrue(newFileReader.readLine().trim().equals(oldFileLine.trim()));
				}
				break;
			}
		}
		newFileReader.close();
		return shapeCorrect;
	}

	private boolean checkBounds(BufferedReader oldFileReader, BufferedReader newFileReader) throws IOException {
		String newFileLine = newFileReader.readLine().trim(), oldFileLine = oldFileReader.readLine().trim();

		if (oldFileLine.substring(oldFileLine.indexOf("d")).equals(newFileLine.substring(newFileLine.indexOf("d")))) {

			return true;
		} else {
			return false;
		}
	}

	private boolean checkLabel(String oldFileLine, BufferedReader oldFileReader, BufferedReader newFileReader)
			throws IOException {
		oldFileLine = oldFileLine.trim();
		oldFileLine = oldFileLine.substring(0, oldFileLine.indexOf(" ")) + ">";

		String newFileLine = newFileReader.readLine().trim();
		if (newFileLine.equals(oldFileLine)) {

			assertTrue(checkBounds(oldFileReader, newFileReader));

			// checks the </bpmndi:BPMNLabel> element
			assertTrue(newFileReader.readLine().trim().equals(oldFileReader.readLine().trim()));

			return true;
		} else {
			return false;
		}
	}

	private boolean checkEdges(File newFile, String oldFileLine, BufferedReader oldFileReader) throws IOException {
		BufferedReader newFileReader = new BufferedReader(new FileReader(newFile));
		boolean edgeCorrect = false;
		String newFileLine;

		while ((newFileLine = newFileReader.readLine()) != null) {
			newFileLine = newFileLine.trim();

			// check <bpmndi:BPMNEdge element
			if (newFileLine.equals(oldFileLine.trim())) {
				edgeCorrect = true;
				oldFileLine = oldFileReader.readLine();

				// check waypoints
				while (oldFileLine.matches(".*waypoint.*")) {
					oldFileLine = oldFileLine.trim();
					newFileLine = newFileReader.readLine().trim();
					oldFileLine = oldFileLine.substring(oldFileLine.indexOf("d"));
					newFileLine = newFileLine.substring(newFileLine.indexOf("d"));
					assertTrue(oldFileLine.equals(newFileLine));
					oldFileLine = oldFileReader.readLine();
				}

				// check BPMNLabel
				if (oldFileLine.matches(".*BPMNLabel.*")) {

					assertTrue(checkLabel(oldFileLine, oldFileReader, newFileReader));

					// check </bpmndi:BPMNEdge> element
					assertTrue(newFileReader.readLine().trim().equals(oldFileReader.readLine().trim()));
				} else {

					// check </bpmndi:BPMNEdge> element
					assertTrue(oldFileLine.trim().equals(newFileReader.readLine().trim()));
				}

				break;
			}
		}

		newFileReader.close();
		return edgeCorrect;
	}

	protected Model loadFileAsModel(final String fileName) throws IOException {
		final Path path = getFilePath(fileName);
		final InputStream inputStreamForFile = Files.newInputStream(path, StandardOpenOption.READ);
		return ModelFactory.createDefaultModel().read(inputStreamForFile, null);
	}

	protected static Path getFilePath(final String fileName) {
		try {			
			return Paths.get(Resources.getResource(fileName).toURI());
		} catch (URISyntaxException e) {
			throw Throwables.propagate(e);
		}
	}

}

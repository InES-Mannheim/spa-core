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
package de.unima.core.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import com.google.common.base.Throwables;

public class RDFExporter implements FileBasedExporter<Model>{

	@Override
	public File exportToFile(Model data, File location) {
		try(FileOutputStream fos = new FileOutputStream(location)){
			data.write(fos);
			return location;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

}

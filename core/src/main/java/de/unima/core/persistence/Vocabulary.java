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
package de.unima.core.persistence;

public final class Vocabulary {
	private Vocabulary() {
		// No instantiation allowed
		throw new UnsupportedOperationException();
	}
	
	public static final String NS = "http://www.uni-mannheim.de/spa/";
	
	public static final String Repository = uri("Repository");
	public static final String Project = uri("Project");
	public static final String Schema = uri("Schema");
	public static final String containsProject = uri("containsProject");
	public static final String containsSchema = uri("containsSchema");
	public static final String containsDataPool = uri("containsDataPool");
	public static final String linksSchema = uri("linksSchema");
	public static final String belongsToRepository = uri("belongsToRepository");
	public static final String DataBucket = uri("DataBucket");
	public static final String DataPool = uri("DataPool");
	public static final String containsDataBucket = uri("containsDataBucket");
	public static final String belongsToProject = uri("belongsToProject");
	
	public static String uri(String localName){
		return NS + localName;
	}
}

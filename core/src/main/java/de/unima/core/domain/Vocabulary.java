package de.unima.core.domain;

public final class Vocabulary {
	private Vocabulary() {
		// No instantiation allowed
		throw new UnsupportedOperationException();
	}
	
	public static final String NS = "http://www.uni-mannheim.de/spa/";
	
	public static final String Repository = uri("Repository");
	public static final String Project = uri("Project");
	public static final String containsProject = uri("containsProject");
	public static final String containsSchema = uri("containsSchema");
	
	public static String uri(String localName){
		return NS + localName;
	}
}

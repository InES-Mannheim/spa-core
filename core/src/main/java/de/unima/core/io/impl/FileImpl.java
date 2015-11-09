package de.unima.core.io.impl;

import de.unima.core.io.File;

public class FileImpl implements File {

	// TODO Use the Java types to implement the FILE-Management:
	private String path;

	public FileImpl(String p) {
		
		this.path = p;
	}
	
	
	@Override public String getPath() {

		return this.path;
	}

}

package de.unima.core.storage;

import java.util.Random;

import org.hashids.Hashids;

public class StoreSupport {
	// Defined upon class loading 
	public static final String commonMemoryLocation = new Hashids(StoreSupport.class.getName()).encode(Math.abs(new Random().nextInt()));
}

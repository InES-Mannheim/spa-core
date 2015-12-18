package de.unima.core.io;

public final class Key {
	
	private final String key;
	
	public Key(String key) {
		this.key = key;
	}
	
	public static Key of(String id){
		return new Key(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(! obj.getClass().isAssignableFrom(Key.class)) return false;
		return key.equals(((Key) obj).key);
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public String toString() {
		return key;
	}
}
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
package de.unima.core.persistence;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AbstractEntityTest {
	
	@Test
	public void enititiesWithSameIdsShouldBeEqual(){
		final Entity<String> first = new StringEntity("test");
		final Entity<String> second = new StringEntity("test");
		assertThat(first, is(equalTo(second)));
	}
	
	@Test
	public void enititiesWithDifferentIdsShouldBeNonEqual(){
		final Entity<String> first = new StringEntity("test2");
		final Entity<String> second = new StringEntity("test");
		assertThat(first, is(not(equalTo(second))));
	}
	
	private class StringEntity extends AbstractEntity<String>{
		public StringEntity(String id) {
			super(id);
		}
	}
}

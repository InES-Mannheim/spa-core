package de.unima.core.domain.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.unima.core.domain.model.AbstractEntity;
import de.unima.core.domain.model.Entity;

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

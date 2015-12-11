package de.unima.core.storage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

public class StoreTest  {

	@Test
	public void noOpShouldBeCreated() {
		final Store store = Store.noOp();
		assertThat(store, is(notNullValue()));
	}
	
	@Test
	public void noOpShouldReturnEmpty() {
		final Store store = Store.noOp();
		final Optional<String> result = store.runWithConnection(connection -> "test");
		assertThat(result.isPresent(), is(false));
	}
}

package de.unima.core.util;

import com.google.common.base.Throwables;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code FallibleConsumer} is expected
 * to operate via side-effects.
 *
 * <p> Compared to {@link Consumer} a {@code FallibleConsumer} may throw an {@link Exception}.
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface FallibleConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws E which may occur during execution
     */
    <E extends Exception> void accept(T t) throws E;

    /**
     * Converts this operation to a {@code Consumer}.
     *
     * <p> Exceptions are rethrown as {@link RuntimeException}s.
     *
     * @return consumer expecting a {@code T}
     */
    default Consumer<T> toConsumer(){
        return value -> {
            try {
                accept(value);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        };
    }
}

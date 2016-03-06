package de.unima.core.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.jena.rdf.model.Model;

/**
 * This type determines how data in a specific format can be imported and
 * exported.
 * 
 * <p> It exposes functions to deserialize, read, write and serialize data. A
 * {@link Source} composes them for import and export. The general pattern is
 * derived from the types of the functions and is depicted as following chain.
 * 
 * <blockquote> {@link Format#deserialize()} -{@link IN}-> {@link Format#read()}
 * -{@link Model}-> {@link Format#write()} -{@link OUT}-> {@link
 * Format#serialize()} </blockquote>
 * 
 * That is, for a given {@link InputStream} the function returned by {@code
 * Format#deserialize()} returns the data as type {@code IN}. For example,
 * {@code XML} could be deserialzed to a corresponding {@code DOM}. Next, the
 * {@code Format#read()} transforms {@code IN} to {@link Model}. In this form,
 * the data can be consumed by the system. When data should be exported, {@link
 * Format#write()} transforms a given {@code Model} to an {@code OUT}. For
 * example, a {@code DOM} instance in case of {@code XML}. {@link
 * Format#serialize()} takes @ {@code OUT} and an {@link OutputStream} and
 * writes {@code OUT} to the stream.
 * 
 * @param <IN> data type after deserialization 
 * @param <OUT> data type before serialization
 */
public interface Format<IN,OUT> {
	
	// Type Aliases
	interface Writes<A> extends Function<Model, A> {}

	interface Reads<A> extends Function<A, Model> {}

	interface Serialize<A> extends Function<A, Consumer<OutputStream>>{}

	interface Deserialize<A> extends Function<InputStream, A> {}

	/**
	 * Returns a function which takes a {@code Model} and transforms
	 * it to an {@code OUT}.
	 * 
	 * @return function mapping from {@code Model} to {@code OUT}
	 */
    Writes<? extends Collection<OUT>> write();
    
    /**
     * Returns a function which takes an {@code OUT} and an {@code OutputStream} and writes
     * to the stream.
     * 
     * <p>Note: It is safe, to leave the {@code OutputStream} open.
     * 
     * @return function mapping from {@code OUT} and {@code OutputStream} to {@code Void}.
     */
    Serialize<OUT> serialize();
    
    /**
     * Returns a function which takes an {@code IN} and transforms it to a {@code Model}.
     * 
     * @return function mapping from {@code IN} to {@code Model}
     */
    Reads<IN> read();
    
    /**
     * Returns a function which takes an {@code InputStream} and transforms it to a {@code IN}.
     * 
     * <p>Note: It is safe, to leave the {@code InputStream} open.
     * 
     * @return function mapping from {@code InputStream} to {@code IN}
     */
    Deserialize<IN> deserialize();
}
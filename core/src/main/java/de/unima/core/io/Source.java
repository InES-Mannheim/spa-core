package de.unima.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.jena.ext.com.google.common.base.Throwables;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * This type provides type-save access to data from different sources.
 * 
 * @param <IN> source if input stream
 * @param <OUT> source of output stream
 * @see Format
 */
public interface Source<IN, OUT> {

    /**
     * Returns a function which takes a {@link Model} and writes it to the given
     * {@code OUT}.
     * 
     * @return function writing {@code OUT} to {@code Model}
     */
    Function<Model, Consumer<OUT>> write();
    
    /**
     * Returns a function which takes {@link IN} and reads it as an {@code Model}.
     * 
     * @return function reading a {@code Model} from {@code IN} 
     */
    Function<IN, Model> read();
    
    /**
     * Determines the format of the data.
     * 
     * @param format of the data
     * @return {@link SourceRef} API
     */
    static <IN, OUT> Source.SourceRef<IN, OUT> as(Format<IN, OUT> format){
    	return new Source.SourceRef<>(format);
    }

    /**
     * This type determines the source of the data.
     * 
     * @param <IN> source of the input stream
     * @param <OUT> source of the output stream
     */
    class SourceRef<IN, OUT> {

        private final Format<IN, OUT> format;

        public SourceRef(Format<IN, OUT> format) {
            this.format = format;
        }

        /**
         * Returns a {@code Source} which reads from a {@link File} and
         * write to a {@link File}.
         * 
         * @return {@code Source}
         */
        public Source<File, File> fromFile(){
            return new Source<File, File>() {
                @Override
                public Function<Model, Consumer<File>> write() {
                    return model -> file -> {
                        try(OutputStream out = Files.newOutputStream(file.toPath())) {
                            format.write()
                                    .apply(model)
                                    .forEach(a -> format.serialize().apply(a).accept(out));
                        } catch (IOException e) {
                            throw Throwables.propagate(e);
                        }
                    };
                }

                @Override
                public Function<File, Model> read() {
                    return file -> {
                        try(InputStream in = Files.newInputStream(file.toPath())){
                            return ifNotEmpty(in)
                                    .map(deserializeAndRead()::apply)
                                    .orElse(emptyModel());
                        } catch (IOException e) {
                            throw Throwables.propagate(e);
                        }
                    };
                }

                private Optional<InputStream> ifNotEmpty(InputStream fos) throws IOException {
                    return fos.available() == 0?Optional.empty():Optional.of(fos);
                }

                private Function<InputStream, Model> deserializeAndRead() {
                    return format
                            .deserialize()
                            .andThen(format.read());
                }

                private Model emptyModel() {
                    return ModelFactory.createDefaultModel();
                }
            };
        }

    }
}
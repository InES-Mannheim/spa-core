package de.unima.core.io.file;


import com.google.common.base.Throwables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;


public class OutputStreamBasedExporterShould {

    /*
     * Violation of SRP?
     *
     * Exporter is responsible for transforming the model and to serialize it.
     * Maybe a separation of these phases would result into more flexibility?
     *
     * 1. Convert process format to other format
     * m: T <: Model -> out :Any
     * 2. Serialize this format
     * in: Any -> Void
     *
     * However, the boundary is clear: OutputStream is used as demarcation.
     */

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    public OutputStream os;

    @Mock
    public Model model;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void notBeNull(){
        final Exporter<Model, FileOutputStream> exporter = model -> fos -> fos.write("test".getBytes());
        assertThat(exporter, is(notNullValue()));
    }

    @Test
    public void closeOutputStreamAfterConsumption() throws IOException {
        final Exporter<Model, OutputStream> exporter = model -> fos -> fos.write("test".getBytes());

        exporter.apply(model).accept(os);

        verify(os).close();
    }

    @Test
    public void writeToOutputStream() throws IOException {
        final Exporter<Model, OutputStream> exporter = model -> fos -> fos.write("test".getBytes());

        exporter.apply(model).accept(os);

        verify(os).write("test".getBytes());
    }

    @Test
    public void writeContentOfModelToStream() throws IOException {
        final Exporter<Model, OutputStream> exporter = model -> os -> os.write(model.content().getBytes());
        final Model model = () -> "hello";

        exporter.apply(model).accept(os);

        verify(os).write("hello".getBytes());
    }

    @Test
    public void failWhenExceptionIsThrown() {
        expected.expect(instanceOf(IllegalArgumentException.class));

        final Exporter<Model, OutputStream> exporter = model -> fos -> {throw new IllegalArgumentException();};

        exporter.apply(model).accept(os);
    }

    // T < Model -> U < Outputstream -> Void
    // Catching Exceptions and rethrow them might be replaced by a Try type.
    public interface Exporter<T extends Model, U extends OutputStream> extends Function<T, Consumer<U>> {
        default Consumer<U> apply(T t){
            return fos -> {
                try(U in = fos){
                    consume(t).accept(in);
                } catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            };
        }

        FailableConsumer<U> consume(T t);
    }


    @FunctionalInterface
    public interface FailableConsumer<T>{
        void accept(T value) throws Exception;
    }

    public interface Model {
        String content();
    }
}

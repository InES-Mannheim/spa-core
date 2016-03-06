package de.unima.core.io;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.com.google.common.base.Throwables;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Resources;

import de.unima.core.io.file.XMLImporter;
import de.unima.core.io.file.XSDImporter;
import de.unima.core.io.file.xes.OntModelToXLogExporter;

public class IOShould {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final Source<File, File> source = Source.as(new Xes()).fromFile();

    @Test
    public void beBuiltNotNull(){
        final Function<Model, Consumer<File>> exporter = source.write();
        final Function<File, Model> importer = source.read();

        assertThat(exporter, is(notNullValue()));
        assertThat(importer, is(notNullValue()));
    }

    @Test
    public void emptyModelIfFileIsEmpty() throws IOException{
        final Function<File, Model> importer = source.read();
        final Model imported = importer.apply(folder.newFile());

        assertThat(imported.isEmpty(), is(true));
    }

    @Test
    public void createStatementsOfFileWithOneLog() throws IOException{
        final Function<File, Model> importer = source.read();
        final File oneEventLog = folder.newFile("oneLog.xes");
        appendOneEvent(oneEventLog);

        final Model importedLog = importer.apply(oneEventLog);
        importedLog.write(System.out);

        assertThat(importedLog.isEmpty(), is(false));
        final List<RDFNode> importedObjects = importedLog.listObjects().toList();
        assertThat(importedObjects, hasItem(ResourceFactory.createTypedLiteral("Costs", XSDDatatype.XSDName)));
        assertThat(importedObjects, hasItem(ResourceFactory.createPlainLiteral("50")));
    }

    @Ignore("XES Exporter assumes that the Schema is present in the data.")
    @Test
    public void writeModelToGivenFile() throws IOException {
        final Function<Model, Consumer<File>> exporter = source.write();
        final Model model = createModelWithOneEvent();
        final File target = folder.newFile("oneLog.xes");

        exporter.apply(model).accept(target);

        final String content = Files.readAllLines(target.toPath()).stream().collect(Collectors.joining(" "));
        assertThat(content, is(equalTo(oneLogEntry())));

    }

    private Model createModelWithOneEvent() {
        return ModelFactory.createDefaultModel().read(new ByteArrayInputStream(rdfWithOneEvent().getBytes()), null);
    }

    private String rdfWithOneEvent(){
        return "<rdf:RDF\n" +
                "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                "    xmlns:dtype=\"http://www.srdc.com.tr/ontmalizer#\"\n" +
                "    xmlns=\"http://www.srdc.com.tr/ontmalizer/instance#\"\n" +
                "    xmlns:xes=\"http://code.deckfour.org/xes#\"\n" +
                "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" > \n" +
                "  <rdf:Description rdf:about=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_EventType_1\">\n" +
                "    <rdf:type rdf:resource=\"http://code.deckfour.org/xes#EventType\"/>\n" +
                "    <xes:string rdf:resource=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_AttributeStringType_1\"/>\n" +
                "  </rdf:Description>\n" +
                "  <rdf:Description rdf:about=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_AttributeStringType_1\">\n" +
                "    <rdf:type rdf:resource=\"http://code.deckfour.org/xes#AttributeStringType\"/>\n" +
                "    <xes:key rdf:datatype=\"http://www.w3.org/2001/XMLSchema#Name\">Costs</xes:key>\n" +
                "    <xes:value>50</xes:value>\n" +
                "  </rdf:Description>\n" +
                "  <rdf:Description rdf:about=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_log_1\">\n" +
                "    <rdf:type rdf:resource=\"http://code.deckfour.org/xes#log\"/>\n" +
                "    <xes:xes.version rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">1.0</xes:xes.version>\n" +
                "    <xes:trace rdf:resource=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_TraceType_1\"/>\n" +
                "  </rdf:Description>\n" +
                "  <rdf:Description rdf:about=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_TraceType_1\">\n" +
                "    <rdf:type rdf:resource=\"http://code.deckfour.org/xes#TraceType\"/>\n" +
                "    <xes:event rdf:resource=\"http://www.srdc.com.tr/ontmalizer/instance#INS438394_EventType_1\"/>\n" +
                "  </rdf:Description>\n" +
                "</rdf:RDF>";
    }

    private void appendOneEvent(File oneLogEntry) throws IOException {
        final String logEntry = oneLogEntry();
        Files.write(oneLogEntry.toPath(), logEntry.getBytes(), StandardOpenOption.APPEND);
    }

    private String oneLogEntry() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> "
                    + " <log xes.version=\"1.0\" xmlns=\"http://code.deckfour.org/xes\"> "
                    + " <trace>"
                    + " <event>"
                    + " <string key=\"Costs\" value=\"50\"/>"
                    + " </event>"
                    + " </trace>"
                    + " </log>";
    }

    public static class Xes implements Format<Model,XLog> {

        private final OntModel xesOntology;
        private final XMLImporter dataImporter;

        public Xes(){
            try(InputStream stream = openStream("xml/xes.xsd")){
                xesOntology = new XSDImporter().importData(stream);
            } catch (IOException e) {
                throw com.google.common.base.Throwables.propagate(e);
            }
            dataImporter = new XMLImporter(xesOntology);
        }

        private static InputStream openStream(final String resourceName) {
            final URL fileUrl = Resources.getResource(resourceName);
            try {
                return Resources.asByteSource(fileUrl).openStream();
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        public Format.Writes<Set<XLog>> write() {
            return model -> new OntModelToXLogExporter().export(model);
        }

        @Override
        public Format.Serialize<XLog> serialize() {
            return log -> os -> {
                try {
                    new XesXmlSerializer().serialize(log, os);
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            };
        }

        @Override
        public Format.Reads<Model> read() {
            return m -> m;
        }

        @Override
        public Format.Deserialize<Model> deserialize() {
            return dataImporter::importData;
        }
    }

}

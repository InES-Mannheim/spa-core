package de.unima.core.io.bpmn;

import java.io.File;

import org.apache.jena.rdf.model.Model;

public interface BpmnMapper {

    public Model mapBpmnToRdf(File bpmnFile);
    
    public File mapRdfToBpmn(Model processModel);
    
}

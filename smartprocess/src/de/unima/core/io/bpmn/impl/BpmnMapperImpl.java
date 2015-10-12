package de.unima.core.io.bpmn.impl;

import java.io.File;
import java.util.Collection;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import de.unima.core.io.bpmn.BpmnMapper;

public class BpmnMapperImpl implements BpmnMapper{

    private String bpmnNs = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    private String testNs = "http://test.de/id/abc#";
    
    @Override
    public Model mapBpmnToRdf(File bpmnFile) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFile);
        
        Collection<Process> modelProcess = modelInstance.getModelElementsByType(Process.class);
        OntModel model = ModelFactory.createOntologyModel();
        model.read("tmp/BPMN_2.0_ontology.owl", "RDFXML");
        
        for(Process p : modelProcess){
            Collection<FlowNode> flowNodes = p.getChildElementsByType(FlowNode.class);
            System.out.println(flowNodes.size());
            for(FlowNode flowNode : flowNodes){
                OntClass c = model.createClass(bpmnNs + "startEvent");
                c.createIndividual(testNs + flowNode.getId());
                System.out.println(flowNode.getElementType().getTypeName());
            }
            
        }
        
        
        
        
        
        return model;
    }

    @Override
    public File mapRdfToBpmn(Model processModel) {
        // TODO Auto-generated method stub
        return null;
    }

}

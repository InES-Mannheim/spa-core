package de.unima.core.test;

import java.io.File;
import java.util.Collection;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;

public class Testing2 {

    public static void main(String[] args) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("tmp/example-spa.bpmn"));
        
//        Collection<ModelElementType> types = modelInstance.getModel().getTypes();
//        
//        for(ModelElementType type:types){
//            System.out.println(type.getTypeName());
//            
//        }
        
        Collection<BpmnDiagram> diagram =  modelInstance.getDefinitions().getBpmDiagrams();
        
//        diagram.iterator().next().
        
        Collection<Process> modelProcess = modelInstance.getModelElementsByType(Process.class);
        
        for(Process p : modelProcess){
            Collection<FlowNode> flowNodes = p.getChildElementsByType(FlowNode.class);
            for(FlowNode flowNode : flowNodes){
                System.out.println(flowNode.getElementType().getTypeName());
                System.out.println(flowNode.getId());
            }
        }
        
        
        
        //Jenastuff
        
        String NS = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
        
        String NSinstance = "http://test.org/ont#";
        
        OntModel m = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
        
        m.read("schema/BPMN_2.0_ontology.owl");
        
        OntClass startEvent = m.getOntClass(NS + "startEvent");
        
        Individual p1 = m.createIndividual(NSinstance + modelProcess.iterator().next().getChildElementsByType(FlowNode.class).iterator().next().getId(), startEvent);
        
        
        
        
//        p1.
        
        for(Individual i : m.listIndividuals().toList()){
          System.out.println(i);
        }
        
        //back to bpmn
        
        
        BpmnModelInstance modelInstance2 = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance2.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        
        
        StartEvent startEvent2 = modelInstance2.newInstance(StartEvent.class);
        Process proc2 = modelInstance2.newInstance(Process.class);
        
        startEvent2.setId("StartEvent_1");
        
        proc2.addChildElement(startEvent2);
        proc2.setId("Process_1");
        proc2.setExecutable(false);
        
        definitions.addChildElement(proc2);
        modelInstance2.setDefinitions(definitions);
        
        Bpmn.writeModelToFile(new File("tmp/test.bpmn"), modelInstance2);
        
        
//        System.out.println(modelInstance.getModel().);
        
//        ModelElementInstance mei = modelInstance.getModelElementById("Process_1");
        
//        System.out.println(mei.getTextContent());
        
        

    }

}

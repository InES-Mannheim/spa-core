package de.unima.core.test;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import de.unima.core.spp.impl.SPPControllerImpl;

public class Testing3 {

  public static void main(String[] args) {
    String NS = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    String NSinstance = "http://test.org/ont#";
    
    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("tmp/example-spa.bpmn"));
    
    System.out.println(modelInstance.getDefinitions().getTargetNamespace());
    
    Collection<Process> modelProcess = modelInstance.getModelElementsByType(Process.class);
    
    OntModel m = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
    
    m.read("schema/BPMN_2.0_ontology.owl");
    
    System.out.println(m.listStatements().toList().size());
    
    for(Process p : modelProcess){
      OntClass process = m.getOntClass(NS + "process");
      String processId = p.getId();
      String isExecutable = "" + p.isExecutable();
      OntProperty idProp = m.getOntProperty(NS + "id");
      OntProperty isExecutableProp = m.getOntProperty(NS + "isExecutable");
      Individual processInd = process.createIndividual(NSinstance + UUID.randomUUID().toString());
      
      processInd.addProperty(idProp,  p.getId());
      processInd.addProperty(isExecutableProp, isExecutable);
      
      System.out.println(m.listStatements().toList().size());
      
        Collection<FlowNode> flowNodes = p.getChildElementsByType(FlowNode.class);
        for(FlowNode flowNode : flowNodes){
          
          OntClass flowNodeOWL = m.getOntClass(NS + "startEvent");
          
          Individual flowNodeInd = flowNodeOWL.createIndividual(NSinstance + UUID.randomUUID());
          
         
          
          flowNodeInd.addProperty(idProp, flowNode.getId());
          
          
          
        }
    }
    
    System.out.println(m.listStatements().toList().size());
    
    SPPControllerImpl controller = new SPPControllerImpl();
    
    String storeId = controller.storeProcessModel(m);
    
    Model m2 = controller.retrieveProcessModel(storeId);
    
    OntModel om = 
        ModelFactory.createOntologyModel(
                                   OntModelSpec.OWL_MEM_RULE_INF,
                                   m2);
    
    System.out.println(m2.listStatements().toList().size());
    
    
    //create bpmn from ontmodel
    
    
    BpmnModelInstance modelInstance2 = Bpmn.createEmptyModel();
    Definitions definitions = modelInstance2.newInstance(Definitions.class);
    definitions.setTargetNamespace("http://camunda.org/examples");
//    definitions.get
    
    
    StartEvent startEvent2 = modelInstance2.newInstance(StartEvent.class);
    Process proc2 = modelInstance2.newInstance(Process.class);
    
    for(Individual i : om.listIndividuals().toList()){
      OntProperty idProp = m.getOntProperty(NS + "id");
      System.out.println(i.getOntClass().getLocalName());
      
      if(i.getOntClass().getLocalName().equals("startEvent")){
        
        startEvent2.setId(i.getPropertyValue(idProp).toString());
               
      }else{
        proc2.setId(i.getPropertyValue(idProp).toString());
        proc2.setExecutable(Boolean.getBoolean(i.getPropertyValue(om.getProperty(NS +"isExecutable")).toString()));
       
      }
      
    }
    
    proc2.addChildElement(startEvent2);
    definitions.addChildElement(proc2);
    modelInstance2.setDefinitions(definitions);

    
    Bpmn.writeModelToFile(new File("tmp/test2.bpmn"), modelInstance2);
    
  }
  
}

package de.unima.core.test;

import java.io.File;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
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
            System.out.println(flowNodes.size());
            for(FlowNode flowNode : flowNodes){
                System.out.println(flowNode.getElementType().getTypeName());
                System.out.println(flowNode.getId());
            }
            
            Collection<SequenceFlow> sequenceFlows = p.getChildElementsByType(SequenceFlow.class);
            System.out.println(sequenceFlows.size());
            for(SequenceFlow flow: sequenceFlows){
                System.out.println(flow.getId());
            }
            
        }
        
//        System.out.println(modelInstance.getModel().);
        
//        ModelElementInstance mei = modelInstance.getModelElementById("Process_1");
        
//        System.out.println(mei.getTextContent());
        
        System.out.println(modelInstance.getDocumentElement());
        
        

    }

}

package de.unima.core.test;

import java.io.File;
import java.util.Collection;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelInstanceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.Incoming;
import org.camunda.bpm.model.bpmn.impl.instance.Outgoing;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.Artifact;
import org.camunda.bpm.model.bpmn.instance.Auditing;
import org.camunda.bpm.model.bpmn.instance.CatchEvent;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.CorrelationSubscription;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;


public class ExportExperiment {

  public static void main(String[] args) {
    OntModel ontModelInstance = readModel();
    
    //Preparing jena models
    OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
    schemaModel.read("src/integration/resources/BPMN_2.0_ontology.owl");
    schemaModel.addSubModel(ontModelInstance);
    
    String schemaNs = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    String targetNs = "http://test.org/projectSpace/randomuuid#";
    
    //Preparing camunda 
    BpmnModelInstanceImpl bpmnMI = (BpmnModelInstanceImpl) Bpmn.createEmptyModel();
    
    Definitions definitions = bpmnMI.newInstance(Definitions.class);
    definitions.setTargetNamespace("");
    bpmnMI.setDefinitions(definitions);
    
    
    ExtendedIterator<Individual> processIndividuals = (ExtendedIterator<Individual>) schemaModel.getOntClass(schemaNs + "process").listInstances();
    
    
    for(Individual processInd : processIndividuals.toList()){
      Process process = bpmnMI.newInstance(Process.class);
      definitions.addChildElement(process);
      
      String processId = processInd.getPropertyValue(schemaModel.getProperty(schemaNs + "id")).toString();
      process.setId(processId);
      
      boolean isExecutable = Boolean.parseBoolean(processInd.getPropertyValue(schemaModel.getProperty(schemaNs + "isExecutable")).toString());
      process.setExecutable(isExecutable);
      
      
       
      //task
      NodeIterator taskIterator = processInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_task"));
      
      for(RDFNode taskNode : taskIterator.toList()){
        String taskId = taskNode.asNode().getLocalName();
        Task bpmnTask = bpmnMI.newInstance(Task.class);
        bpmnTask.setId(taskId);
        Individual taskInd = schemaModel.getIndividual(targetNs + taskId);
        
        String name = taskInd.getPropertyValue(schemaModel.getProperty(schemaNs + "name")).toString();
        bpmnTask.setName(name);
 
        for(RDFNode outgoingNode : taskInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_outgoing")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on outgoingNode object
          String outgoingId = outgoingNode.toString().split("#")[1];
          Outgoing outgoingSeqFlow =  bpmnMI.newInstance(Outgoing.class);
          outgoingSeqFlow.setTextContent(outgoingId);
          bpmnTask.addChildElement(outgoingSeqFlow);
        }

        for(RDFNode incomingNode : taskInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_incoming")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on incomingNode object
          String incomingId = incomingNode.toString().split("#")[1];
          Incoming incomingSeqFlow =  bpmnMI.newInstance(Incoming.class);
          incomingSeqFlow.setTextContent(incomingId);
          bpmnTask.addChildElement(incomingSeqFlow);
        }
        
        
        
        process.addChildElement(bpmnTask);
      }
      
      //startEvent
      NodeIterator startEventIterator = processInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_startEvent"));
      
      for(RDFNode startEventNode : startEventIterator.toList()){
        String startEventId = startEventNode.asNode().getLocalName();
        StartEvent bpmnStartEvent = bpmnMI.newInstance(StartEvent.class);
        Individual startEventInd = schemaModel.getIndividual(targetNs + startEventId);
        bpmnStartEvent.setId(startEventId);
        
        for(RDFNode outgoingNode : startEventInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_outgoing")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on outgoingNode object
          String outgoingId = outgoingNode.toString().split("#")[1];
          Outgoing outgoingSeqFlow =  bpmnMI.newInstance(Outgoing.class);
          outgoingSeqFlow.setTextContent(outgoingId);
          bpmnStartEvent.addChildElement(outgoingSeqFlow);
        }

        for(RDFNode incomingNode : startEventInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_incoming")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on incomingNode object
          String incomingId = incomingNode.toString().split("#")[1];
          Incoming incomingSeqFlow =  bpmnMI.newInstance(Incoming.class);
          incomingSeqFlow.setTextContent(incomingId);
          bpmnStartEvent.addChildElement(incomingSeqFlow);
        }
        
        
        process.addChildElement(bpmnStartEvent);
      }
      
    //endEvent
      NodeIterator endEventIterator = processInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_endEvent"));
      
      for(RDFNode endEventNode : endEventIterator.toList()){
        String endEventId = endEventNode.asNode().getLocalName();
        EndEvent bpmnEndEvent = bpmnMI.newInstance(EndEvent.class);
        Individual endEventInd = schemaModel.getIndividual(targetNs + endEventId);
        bpmnEndEvent.setId(endEventId);
        
        for(RDFNode outgoingNode : endEventInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_outgoing")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on outgoingNode object
          String outgoingId = outgoingNode.toString().split("#")[1];
          Outgoing outgoingSeqFlow =  bpmnMI.newInstance(Outgoing.class);
          outgoingSeqFlow.setTextContent(outgoingId);
          bpmnEndEvent.addChildElement(outgoingSeqFlow);
        }

        for(RDFNode incomingNode : endEventInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_incoming")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on incomingNode object
          String incomingId = incomingNode.toString().split("#")[1];
          Incoming incomingSeqFlow =  bpmnMI.newInstance(Incoming.class);
          incomingSeqFlow.setTextContent(incomingId);
          bpmnEndEvent.addChildElement(incomingSeqFlow);
        }
        
        
        process.addChildElement(bpmnEndEvent);
      }
      
      //exclusiveGateway
      NodeIterator exclusiveGatewayIterator = processInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_exclusiveGateway"));
      
      for(RDFNode exclusiveGatewayNode : exclusiveGatewayIterator.toList()){
        String exclusiveGatewayId = exclusiveGatewayNode.asNode().getLocalName();
        ExclusiveGateway bpmnExGw = bpmnMI.newInstance(ExclusiveGateway.class);
        Individual exGwInd = schemaModel.getIndividual(targetNs + exclusiveGatewayId);
        bpmnExGw.setId(exclusiveGatewayId);
        
        for(RDFNode outgoingNode : exGwInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_outgoing")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on outgoingNode object
          String outgoingId = outgoingNode.toString().split("#")[1];
          Outgoing outgoingSeqFlow =  bpmnMI.newInstance(Outgoing.class);
          outgoingSeqFlow.setTextContent(outgoingId);
          bpmnExGw.addChildElement(outgoingSeqFlow);
        }

        for(RDFNode incomingNode : exGwInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_incoming")).toList()){
          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on incomingNode object
          String incomingId = incomingNode.toString().split("#")[1];
          Incoming incomingSeqFlow =  bpmnMI.newInstance(Incoming.class);
          incomingSeqFlow.setTextContent(incomingId);
          bpmnExGw.addChildElement(incomingSeqFlow);
        }
        
        process.addChildElement(bpmnExGw);
      }
      
      //Has to be done after FlowNodes have been initialized
      //SequenceFlow
      NodeIterator sequenceIterator = processInd.listPropertyValues(schemaModel.getProperty(schemaNs + "has_sequenceFlow"));
      
      for(RDFNode sequenceFlow : sequenceIterator.toList()){
        String sequenceFlowId = sequenceFlow.asNode().getLocalName();
        SequenceFlow bpmnSeqFlow = bpmnMI.newInstance(SequenceFlow.class);
        bpmnSeqFlow.setId(sequenceFlowId);
        Individual seqFlowInd = schemaModel.getIndividual(targetNs + sequenceFlowId);
        
        RDFNode nameProperty = seqFlowInd.getPropertyValue(schemaModel.getProperty(schemaNs + "name"));
        if(nameProperty != null){
          bpmnSeqFlow.setName(nameProperty.toString());
        }
        
            
        //dirtyfix split #    
        String sourceRef = seqFlowInd.getPropertyValue(schemaModel.getProperty(schemaNs + "has_sourceRef")).toString().split("#")[1];
        FlowNode bpmnSource = bpmnMI.getModelElementById(sourceRef);
        bpmnSeqFlow.setSource(bpmnSource);
        
        //dirtyfix split #
        String targetRef = seqFlowInd.getPropertyValue(schemaModel.getProperty(schemaNs + "has_targetRef")).toString().split("#")[1];
        FlowNode bpmnTarget = bpmnMI.getModelElementById(targetRef);
        bpmnSeqFlow.setTarget(bpmnTarget);
        
        
        process.addChildElement(bpmnSeqFlow);
      }
      
    }
    
    
    
    Bpmn.writeModelToStream(System.out, bpmnMI);
    
    
  }
  
  
  
  public static OntModel readModel(){
    String targetNs = "http://test.org/projectSpace/randomuuid#";
    String schemaNS = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    
    
    //Preparing jena models
    OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
    schemaModel.read("src/integration/resources/BPMN_2.0_ontology.owl");
    
    OntModel ontModelInstance = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
    schemaModel.addSubModel(ontModelInstance);
    
    String blankSubnodeProperty = schemaNS + "has_";
    
    //Preparing camunda
    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("src/test/resources/control-flow-example.bpmn"));
    
    Definitions defs = modelInstance.getDefinitions();
    
    
    Collection<Process> modelProcesses = modelInstance.getModelElementsByType(Process.class);

    for(Process process : modelProcesses){
      String nodeType = process.getElementType().getTypeName();
      OntClass processClass = schemaModel.getOntClass(schemaNS + nodeType);
      
      String processId   = process.getId();     
      Individual processInd = ontModelInstance.createIndividual(targetNs + processId, processClass);
      
      //setting attributes
      processInd.addLiteral(ontModelInstance.getProperty(schemaNS + "id"), processId);
      processInd.addLiteral(ontModelInstance.getProperty(schemaNS + "isExecutable"), process.isExecutable());
      processInd.addLiteral(ontModelInstance.getProperty(schemaNS + "isClosed"), process.isClosed());
      processInd.addLiteral(ontModelInstance.getProperty(schemaNS + "processType"), process.getProcessType().name());
      String processName = process.getName();
      if(processName != null){
        processInd.addLiteral(ontModelInstance.getProperty(schemaNS + "name"), processName);
      }
      
      
      Collection<Artifact> processArtifacts = process.getArtifacts();
      if(processArtifacts.size() > 0){
        //TODO handle process artifacts
        
      }
      
      Auditing processAuditing = process.getAuditing();
      if(processAuditing != null){
        //TODO handle process auditing
        
      }
      
      Collection<CorrelationSubscription> processCorrelationSubscription = process.getCorrelationSubscriptions();
      if(processCorrelationSubscription.size() > 0){
        //TODO handle process correlation subscroption
        
      }
      
      Collection<LaneSet> processLaneSets = process.getLaneSets();
      for(LaneSet laneSet : processLaneSets){
        String laneSetNodeType = laneSet.getElementType().getTypeName();
        OntClass laneSetClass = schemaModel.getOntClass(schemaNS + laneSetNodeType);          
        
        String laneSetId = laneSet.getId();
        Individual laneSetInd = ontModelInstance.createIndividual(targetNs + laneSetId, laneSetClass);
        
        laneSetInd.addLiteral(ontModelInstance.getProperty(schemaNS + "id"), laneSetId);
        
        for(Lane lane : laneSet.getLanes()){
          String laneNodeType = lane.getElementType().getTypeName();
          OntClass laneClass = schemaModel.getOntClass(schemaNS + laneNodeType);          
          
          String laneId = lane.getId();
          Individual laneInd = ontModelInstance.createIndividual(targetNs + laneId, laneClass);
          
          laneInd.addLiteral(ontModelInstance.getProperty(schemaNS + "id"), laneId);
          String laneName = lane.getName();
          if(laneName != null){
            laneInd.addLiteral(ontModelInstance.getProperty(schemaNS + "name"), laneName);
          }
          
          
          for(FlowNode flowNodeRef : lane.getFlowNodeRefs()){
            laneInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + "flowNode"), targetNs + flowNodeRef.getId());
          }
          
        }
      }
      
      Collection<FlowElement> processFlowElements  = process.getFlowElements();
      if(processFlowElements.size() > 0){
        for(FlowElement flowElement : processFlowElements){
          String flowElementNodeType = flowElement.getElementType().getTypeName();
          OntClass flowElementClass = schemaModel.getOntClass(schemaNS + flowElementNodeType);          
          
          String flowElementId = flowElement.getId();
          Individual flowElementInd = ontModelInstance.createIndividual(targetNs + flowElementId, flowElementClass);
          
          //set the subnode for the process
          processInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + flowElementNodeType), flowElementInd);
          
          //set attribute for flowElement
          flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "id"), flowElementId);
          String flowElementName = flowElement.getName();
          if(flowElementName != null){
            flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "name"), flowElementName);
          }
          
          if(flowElement instanceof FlowNode){
            for(SequenceFlow seqFlowIncoming : ((FlowNode) flowElement).getIncoming()){
              flowElementInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + "incoming"), targetNs + seqFlowIncoming.getId());
            }
            
            for(SequenceFlow seqFlowOutgoing : ((FlowNode) flowElement).getOutgoing()){
              flowElementInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + "outgoing"), targetNs + seqFlowOutgoing.getId());
            }
            
            if(flowElement instanceof Event){
              
              if(flowElement instanceof CatchEvent){
                
                if(flowElement instanceof StartEvent){
                  
                  flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "isInterrupting"), ((StartEvent) flowElement).isInterrupting());
                } 
              }      
            }
            
            if(flowElement instanceof Activity){
              flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "completionQuantity"), ((Activity) flowElement).getCompletionQuantity());
              flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "startQuantity"), ((Activity) flowElement).getStartQuantity());
              flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "isForCompensation"), ((Activity) flowElement).isForCompensation());
            }
            
            if(flowElement instanceof Gateway) {
              flowElementInd.addLiteral(ontModelInstance.getProperty(schemaNS + "gatewayDirection"), ((Gateway) flowElement).getGatewayDirection().name());
            }
            
          }
          
          if(flowElement instanceof SequenceFlow){
            String sourceId = ((SequenceFlow) flowElement).getSource().getId();
            flowElementInd.addLiteral(ontModelInstance.getProperty(blankSubnodeProperty + "sourceRef"), targetNs + sourceId);
            String targetId = ((SequenceFlow) flowElement).getTarget().getId();
            flowElementInd.addLiteral(ontModelInstance.getProperty(blankSubnodeProperty + "targetRef"), targetNs + targetId);
          }
          
          
        }
        
        
      }
      
      
      
      
//      for(FlowElement flowElement : process.getFlowElements()){
//        System.out.println(flowElement.getElementType().getTypeName());
//      }
      
      
    }
    
    Collection<Collaboration> collaborations = modelInstance.getModelElementsByType(Collaboration.class);
    
    for(Collaboration collaboration : collaborations){
      String collaborationNodeType = collaboration.getElementType().getTypeName();
      OntClass collaborationClass = schemaModel.getOntClass(schemaNS + collaborationNodeType);
      
      String collaborationId   = collaboration.getId();     
      Individual collaborationInd = ontModelInstance.createIndividual(targetNs + collaborationId, collaborationClass);
      
      collaborationInd.addLiteral(ontModelInstance.getProperty(schemaNS + "id"), collaborationId);
      
      Collection<Participant> participants = collaboration.getParticipants();
      for(Participant participant : participants){
        String participantNodeType = participant.getElementType().getTypeName();
        OntClass participantClass = schemaModel.getOntClass(schemaNS + participantNodeType);
        
        String participantId = participant.getId();
        Individual participantInd = ontModelInstance.createIndividual(targetNs + participantId, participantClass);
        
        collaborationInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + participantNodeType), participantId);
        
        
        participantInd.addLiteral(ontModelInstance.getProperty(schemaNS + "id"), participantId);
        
        String participantName = participant.getName();
        if(participantName != null){
          participantInd.addLiteral(ontModelInstance.getProperty(schemaNS + "name"), participantName);
        }
        
        participantInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + "processRef"), targetNs + participant.getProcess().getId());
        
        
      }
      
      
    }
    for(Statement stmt : ontModelInstance.listStatements().toList()){
      System.out.println(stmt.toString());
    }
//
//    System.out.println(ontModelInstance.listClasses().toList());
//    System.out.println(schemaModel.listIndividuals().toList());
    
    return ontModelInstance;
  }
  
  
}

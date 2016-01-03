package de.unima.core.io.file;

import java.io.File;
import java.util.Collection;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.Artifact;
import org.camunda.bpm.model.bpmn.instance.Auditing;
import org.camunda.bpm.model.bpmn.instance.CatchEvent;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.CorrelationSubscription;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class BPMN20Importer implements FileBasedImporter<Model> {
    
    private final String individualNameSpace;
    
    private static final String SCHEMAPATH = "ontologies/BPMN_2.0_ontology.owl";
    private static final String SCHEMA_NAMESPACE = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    
    public BPMN20Importer(String individualNameSpace) {
      this.individualNameSpace = individualNameSpace;
    }

    @Override
    public Model importData(File bpmnSource) {
    //Preparing jena models
      OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
      schemaModel.read(SCHEMAPATH);
      
      OntModel ontModelInstance = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
      schemaModel.addSubModel(ontModelInstance);
      
      String blankSubnodeProperty = SCHEMA_NAMESPACE + "has_";
      
      //Preparing camunda
      BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnSource);
      
      Definitions defs = modelInstance.getDefinitions();
      
      Collection<Process> modelProcesses = modelInstance.getModelElementsByType(Process.class);

      for(Process process : modelProcesses){
        String nodeType = process.getElementType().getTypeName();
        OntClass processClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + nodeType);
        
        String processId   = process.getId();     
        Individual processInd = ontModelInstance.createIndividual(individualNameSpace + processId, processClass);
        
        //setting attributes
        processInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), processId);
        processInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "isExecutable"), process.isExecutable());
        processInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "isClosed"), process.isClosed());
        processInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "processType"), process.getProcessType().name());
        String processName = process.getName();
        if(processName != null){
          processInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"), processName);
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
          OntClass laneSetClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + laneSetNodeType);          
          
          String laneSetId = laneSet.getId();
          Individual laneSetInd = ontModelInstance.createIndividual(individualNameSpace + laneSetId, laneSetClass);
          processInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "laneSet"), laneSetInd);
          
          laneSetInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), laneSetId);
          
          for(Lane lane : laneSet.getLanes()){
            String laneNodeType = lane.getElementType().getTypeName();
            OntClass laneClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + laneNodeType);          
            
            String laneId = lane.getId();
            Individual laneInd = ontModelInstance.createIndividual(individualNameSpace + laneId, laneClass);
            
            laneSetInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "lane"), laneInd);
            
            laneInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), laneId);
            String laneName = lane.getName();
            if(laneName != null){
              laneInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"), laneName);
            }
            
            
            for(FlowNode flowNodeRef : lane.getFlowNodeRefs()){
              laneInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "flowNode"), ResourceFactory.createResource(individualNameSpace + flowNodeRef.getId()));
            }
            
          }
        }
        
        Collection<FlowElement> processFlowElements  = process.getFlowElements();
        if(processFlowElements.size() > 0){
          for(FlowElement flowElement : processFlowElements){
            String flowElementNodeType = flowElement.getElementType().getTypeName();
            OntClass flowElementClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + flowElementNodeType);          
            
            String flowElementId = flowElement.getId();
            Individual flowElementInd = ontModelInstance.createIndividual(individualNameSpace + flowElementId, flowElementClass);
            
            //set the subnode for the process
            
            
            //set attribute for flowElement
            flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), flowElementId);
            String flowElementName = flowElement.getName();
            if(flowElementName != null){
              flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"), flowElementName);
            }
            
            //add type specific attributes e.g. incoming or outgoing properties for flownodes.
            
            if(flowElement instanceof FlowNode){
              processInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "flowNode"), flowElementInd);
              
              for(SequenceFlow seqFlowIncoming : ((FlowNode) flowElement).getIncoming()){
                flowElementInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "incoming"), ResourceFactory.createResource(individualNameSpace + seqFlowIncoming.getId()));
              }
              
              for(SequenceFlow seqFlowOutgoing : ((FlowNode) flowElement).getOutgoing()){
                flowElementInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "outgoing"), ResourceFactory.createResource(individualNameSpace + seqFlowOutgoing.getId()));
              }
              
              if(flowElement instanceof Event){
                
                if(flowElement instanceof CatchEvent){
                  for(EventDefinition eventDefinition:((CatchEvent) flowElement).getEventDefinitions()){
                    
                    if(eventDefinition instanceof MessageEventDefinition){
                      Individual messageEventInd = ontModelInstance.createIndividual(individualNameSpace + eventDefinition.getId(), schemaModel.getOntClass(SCHEMA_NAMESPACE + "messageEventDefinition"));
                      messageEventInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), eventDefinition.getId());
                      flowElementInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "eventDefinition"), ResourceFactory.createResource(individualNameSpace + eventDefinition.getId()));
                      
                    }
                  }
                  
                  if(flowElement instanceof StartEvent){
                    
                    flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "isInterrupting"), ((StartEvent) flowElement).isInterrupting());
                  } 
                }      
              }
              
              if(flowElement instanceof Activity){
                flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "completionQuantity"), ((Activity) flowElement).getCompletionQuantity());
                flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "startQuantity"), ((Activity) flowElement).getStartQuantity());
                flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "isForCompensation"), ((Activity) flowElement).isForCompensation());
              }
              
              if(flowElement instanceof Gateway) {
                flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "gatewayDirection"), ((Gateway) flowElement).getGatewayDirection().name());
                
                if(flowElement instanceof EventBasedGateway){
                  flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "instantiate"), ((EventBasedGateway) flowElement).isInstantiate());
                  flowElementInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "eventBasedGatewayType"), ((EventBasedGateway) flowElement).getEventGatewayType().toString());

                }
              }
              
            }
            
            if(flowElement instanceof SequenceFlow){
              processInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "sequenceFlow"), flowElementInd);
              String sourceId = ((SequenceFlow) flowElement).getSource().getId();
              flowElementInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "sourceRef"), ResourceFactory.createResource(individualNameSpace + sourceId));
              String targetId = ((SequenceFlow) flowElement).getTarget().getId();
              flowElementInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "targetRef"), ResourceFactory.createResource(individualNameSpace + targetId));
            }  
            
          }
           
        }
        
      }
      
      Collection<Collaboration> collaborations = modelInstance.getModelElementsByType(Collaboration.class);
      
      for(Collaboration collaboration : collaborations){
        String collaborationNodeType = collaboration.getElementType().getTypeName();
        OntClass collaborationClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + collaborationNodeType);
        
        String collaborationId   = collaboration.getId();     
        Individual collaborationInd = ontModelInstance.createIndividual(individualNameSpace + collaborationId, collaborationClass);
        
        collaborationInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), collaborationId);
        
        Collection<Participant> participants = collaboration.getParticipants();
        for(Participant participant : participants){
          String participantNodeType = participant.getElementType().getTypeName();
          OntClass participantClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + participantNodeType);
          
          String participantId = participant.getId();
          Individual participantInd = ontModelInstance.createIndividual(individualNameSpace + participantId, participantClass);
          
          collaborationInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "participant"), ResourceFactory.createResource(individualNameSpace + participantId));
               
          participantInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), participantId);
          
          String participantName = participant.getName();
          if(participantName != null){
            participantInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"), participantName);
          }
          
          participantInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "processRef"), ResourceFactory.createResource(individualNameSpace + participant.getProcess().getId()));
          
          
        }
        Collection<MessageFlow> messageFlows = collaboration.getMessageFlows();
        for(MessageFlow messageFlow : messageFlows){
          String messageFlowNodeType = messageFlow.getElementType().getTypeName();
          OntClass messageFlowClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + messageFlowNodeType);
          
          String messageFlowId = messageFlow.getId();
          Individual messageFlowInd = ontModelInstance.createIndividual(individualNameSpace + messageFlowId, messageFlowClass);
          
          collaborationInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "messageFlow"), ResourceFactory.createResource(individualNameSpace + messageFlowId));
          
          messageFlowInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), messageFlowId);
          
          String messageFlowName = messageFlow.getName();
          if(messageFlowName != null){
            messageFlowInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"), messageFlowName);
          }
          
          String messageFlowSourceRef = messageFlow.getSource().getId();
          messageFlowInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "sourceRef"), ResourceFactory.createResource(individualNameSpace + messageFlowSourceRef));
          String messageFlowTargetRef = messageFlow.getTarget().getId();
          messageFlowInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "targetRef"), ResourceFactory.createResource(individualNameSpace + messageFlowTargetRef));
              
        }
        
      }
      
      return ontModelInstance;
  }

}

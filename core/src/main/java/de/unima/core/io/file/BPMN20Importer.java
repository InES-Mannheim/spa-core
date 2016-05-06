/*******************************************************************************
 *    Copyright 2016 University of Mannheim
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
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
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

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
      

      createBpmnDiagramIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, modelInstance);
      return ontModelInstance;
  }

    private void createBpmnDiagramIndividual(OntModel schemaModel, OntModel ontModelInstance,
            String blankSubnodeProperty, BpmnModelInstance modelInstance) {
        Collection<BpmnDiagram> bpmnDiagrams = modelInstance.getModelElementsByType(BpmnDiagram.class);
        for (BpmnDiagram bpmnDiagram : bpmnDiagrams) {			
            Individual bpmnDiagramInd = createBpmnIndividual(schemaModel, ontModelInstance, bpmnDiagram, bpmnDiagram.getId());
            addBpmnPlaneIndividualToBpmnDiagramIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnDiagram, bpmnDiagramInd);
        }
    }

    private void addBpmnPlaneIndividualToBpmnDiagramIndividual(OntModel schemaModel, OntModel ontModelInstance, String blankSubnodeProperty,
            BpmnDiagram bpmnDiagram, Individual bpmnDiagramInd) {
        BpmnPlane bpmnPlane = bpmnDiagram.getBpmnPlane();
        String bpmnPlaneId = bpmnPlane.getId();
        Individual bpmnPlaneInd = createBpmnPlaneIndividual(schemaModel, ontModelInstance, bpmnPlane, bpmnPlaneId);
        bpmnDiagramInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "bpmnPlane"),
                ResourceFactory.createResource(individualNameSpace + bpmnPlaneId));


        addBpmnShapeIndividualToBpmnPlaneIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnPlane, bpmnPlaneInd);

        addBpmnEdgeIndividualsToBpmnPlaneIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnPlane, bpmnPlaneInd);
    }

    private Individual createBpmnPlaneIndividual(OntModel schemaModel, OntModel ontModelInstance, BpmnPlane bpmnPlane,
            String bpmnPlaneId) {		
        Individual bpmnPlaneInd = createBpmnIndividual(schemaModel, ontModelInstance, bpmnPlane,bpmnPlaneId);
        bpmnPlaneInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "bpmnElement"),
                bpmnPlane.getBpmnElement().getId());
        return bpmnPlaneInd;
    }

    private void addBpmnEdgeIndividualsToBpmnPlaneIndividual(OntModel schemaModel, OntModel ontModelInstance, String blankSubnodeProperty,
            BpmnPlane bpmnPlane, Individual bpmnPlaneInd) {
        Collection<BpmnEdge> bpmnEdges = bpmnPlane.getChildElementsByType(BpmnEdge.class);
        for (BpmnEdge bpmnEdge : bpmnEdges) {

            String bpmnEdgeId = bpmnEdge.getId();
            Individual bpmnEdgeInd = createBpmnEdgeIndivdual(schemaModel, ontModelInstance, bpmnEdge, bpmnEdgeId);
            bpmnPlaneInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "bpmnEdge"),
                    ResourceFactory.createResource(individualNameSpace + bpmnEdgeId));

            addWaypointIndividualsToBpmnEdgeIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnEdge, bpmnEdgeId, bpmnEdgeInd);

            BpmnLabel bpmnLabel = bpmnEdge.getBpmnLabel();
            if (bpmnLabel != null){
                addBpmnLabelIndividualToBpmnPlaneIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnLabel, bpmnEdgeId, bpmnEdgeInd);
            }
        }
    }

    private Individual createBpmnEdgeIndivdual(OntModel schemaModel, OntModel ontModelInstance, BpmnEdge bpmnEdge,
            String bpmnEdgeId) {
        Individual bpmnEdgeInd = createBpmnIndividual(schemaModel, ontModelInstance, bpmnEdge,bpmnEdgeId);
        bpmnEdgeInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "bpmnElement"),
                bpmnEdge.getBpmnElement().getId());
        return bpmnEdgeInd;
    }	

    private void addWaypointIndividualsToBpmnEdgeIndividual(OntModel schemaModel, OntModel ontModelInstance, String blankSubnodeProperty,
            BpmnEdge bpmnEdge, String bpmnEdgeId, Individual bpmnEdgeInd) {
        Collection<Waypoint> waypoints = bpmnEdge.getWaypoints();
        int counter = 0;
        for (Waypoint waypoint : waypoints) {
            counter++;
            createWaypointIndividual(schemaModel, ontModelInstance, bpmnEdgeId, counter, waypoint);
            bpmnEdgeInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "waypoint"),
                    ResourceFactory.createResource(individualNameSpace + bpmnEdgeId + counter));
        }
    }

    private void createWaypointIndividual(OntModel schemaModel, OntModel ontModelInstance, String bpmnEdgeId,
            int counter, Waypoint waypoint) {
        String waypointNodeType = waypoint.getElementType().getTypeName();
        OntClass waypointClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + waypointNodeType);		
        Individual waypointInd = ontModelInstance
                .createIndividual(individualNameSpace + bpmnEdgeId + counter, waypointClass);			
        Double x = waypoint.getX();
        Double y = waypoint.getY();
        waypointInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "x"), x);
        waypointInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "y"), y);		
    }

    private void addBpmnShapeIndividualToBpmnPlaneIndividual(OntModel schemaModel, OntModel ontModelInstance, String blankSubnodeProperty,
            BpmnPlane bpmnPlane, Individual bpmnPlaneInd) {
        Collection<BpmnShape> bpmnShapes = bpmnPlane.getChildElementsByType(BpmnShape.class);
        for (BpmnShape bpmnShape : bpmnShapes) {

            String bpmnShapeId = bpmnShape.getId();
            Individual bpmnShapeInd = createBpmnShapeIndividual(schemaModel, ontModelInstance, bpmnShape, bpmnShapeId);
            bpmnPlaneInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "bpmnShape"),
                    ResourceFactory.createResource(individualNameSpace + bpmnShapeId));	

            Bounds bound = bpmnShape.getBounds();
            createBoundsIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnShapeId, bpmnShapeInd, bound);
            bpmnShapeInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "bound"),
                    ResourceFactory.createResource(individualNameSpace + bpmnShapeId));

            BpmnLabel bpmnLabel = bpmnShape.getBpmnLabel();
            if (bpmnLabel != null){
                addBpmnLabelIndividualToBpmnPlaneIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, bpmnLabel, bpmnShapeId, bpmnShapeInd);
            }
        }
    }

    private Individual createBpmnShapeIndividual(OntModel schemaModel, OntModel ontModelInstance, BpmnShape bpmnShape,
            String bpmnShapeId) {		
        Individual bpmnShapeInd = createBpmnIndividual(schemaModel, ontModelInstance, bpmnShape, bpmnShapeId);
        bpmnShapeInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "bpmnElement"),
                bpmnShape.getBpmnElement().getId());
        return bpmnShapeInd;
    }

    private Individual createBpmnIndividual(OntModel schemaModel, OntModel ontModelInstance, ModelElementInstance modelElement, String newElementId) {
        String bpmnNodeType = modelElement.getElementType().getTypeName();
        OntClass bpmnClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + bpmnNodeType);		
        Individual bpmnInd = ontModelInstance.createIndividual(individualNameSpace + newElementId,
                bpmnClass);
        bpmnInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "id"), newElementId);		
        return bpmnInd;
    }

    private void addBpmnLabelIndividualToBpmnPlaneIndividual(OntModel schemaModel, OntModel ontModelInstance,
            String blankSubnodeProperty, BpmnLabel bpmnLabel, String bpmnShapeId, Individual bpmnShapeInd) {

        String bpmnLabelNodeType = bpmnLabel.getElementType().getTypeName();
        OntClass bpmnLabelClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + bpmnLabelNodeType);
        Individual bpmnLabelInd = ontModelInstance.createIndividual(individualNameSpace + "edited-" + bpmnShapeId,
                bpmnLabelClass);
        bpmnShapeInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "bpmnLabel"),
                ResourceFactory.createResource(individualNameSpace + "edited-" + bpmnShapeId));

        Bounds bound = bpmnLabel.getBounds();
        createBoundsIndividual(schemaModel, ontModelInstance, blankSubnodeProperty, "edited-" + bpmnShapeId,
                bpmnLabelInd, bound);
        bpmnLabelInd.addProperty(schemaModel.getProperty(blankSubnodeProperty + "bound"),
                ResourceFactory.createResource(individualNameSpace + "edited-" + bpmnShapeId));

    }

    private Individual createBoundsIndividual(OntModel schemaModel, OntModel ontModelInstance, String blankSubnodeProperty,
            String bpmnShapeId, Individual bpmnShapeInd, Bounds bound) {

        String boundNodeType = bound.getElementType().getTypeName();
        OntClass boundClass = schemaModel.getOntClass(SCHEMA_NAMESPACE + boundNodeType);
        double x = bound.getX();
        double y = bound.getY();
        double width = bound.getWidth();
        double height = bound.getHeight();
        Individual boundInd = ontModelInstance.createIndividual(individualNameSpace + bpmnShapeId, boundClass);
        boundInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "x"), x);
        boundInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "y"), y);
        boundInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "width"), width);
        boundInd.addLiteral(schemaModel.getProperty(SCHEMA_NAMESPACE + "height"), height);
        return boundInd;
    }
}

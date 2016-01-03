package de.unima.core.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.EventBasedGatewayType;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.ProcessType;
import org.camunda.bpm.model.bpmn.impl.BpmnModelInstanceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.FlowNodeRef;
import org.camunda.bpm.model.bpmn.impl.instance.Incoming;
import org.camunda.bpm.model.bpmn.impl.instance.Outgoing;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.CatchEvent;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.InteractionNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class BPMN20Exporter implements FileBasedExporter<Model> {
	
    private final String individualNameSpace;
    
    private static final String SCHEMAPATH = "ontologies/BPMN_2.0_ontology.owl";
    private static final String SCHEMA_NAMESPACE = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    
    public BPMN20Exporter(String individualNameSpace) {
      this.individualNameSpace = individualNameSpace;
    }

	
	@SuppressWarnings("unchecked")
	@Override
	public File exportToFile(Model data, File location) {
	  OntModel dataOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, data);
		 //Preparing jena models
	    OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
		try (InputStream schemaInputStream = Resources.asByteSource(Resources.getResource(SCHEMAPATH)).openBufferedStream()){
			schemaModel.read(schemaInputStream, null);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	    schemaModel.addSubModel(data);
	    
	    
	    //Preparing camunda 
	    BpmnModelInstanceImpl bpmnMI = (BpmnModelInstanceImpl) Bpmn.createEmptyModel();
	    
	    Definitions definitions = bpmnMI.newInstance(Definitions.class);
	    definitions.setTargetNamespace("");
	    bpmnMI.setDefinitions(definitions);
	    
	    
	    ExtendedIterator<Individual> processIndividuals = (ExtendedIterator<Individual>) schemaModel.getOntClass(SCHEMA_NAMESPACE + "process").listInstances();
	    List<Individual> processIndividualsList = processIndividuals.toList();
	    for(Individual processInd : processIndividualsList){
	      Process process = bpmnMI.newInstance(Process.class);
	      definitions.addChildElement(process);
	      
	      String processId = processInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
	      process.setId(processId);
	      
	      boolean isExecutable = Boolean.parseBoolean(processInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "isExecutable")).toString());
	      process.setExecutable(isExecutable);
	      
	      boolean isClosed = Boolean.parseBoolean(processInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "isClosed")).toString());
	      process.setClosed(isClosed);
	      
	      String processType = processInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "processType")).toString();
	      process.setProcessType(ProcessType.valueOf(processType));
	      
	      RDFNode processNameNode = processInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"));
	      if(processNameNode != null){
	        process.setName(processNameNode.toString());
	      }
	      
	       
	      //flowNodes
	      NodeIterator flowNodeIterator = processInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_flowNode"));
	      
	      for(RDFNode flowNode : flowNodeIterator.toList()){
	        String flowNodeId = flowNode.asResource().getProperty(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).getObject().toString();
	        Individual flowNodeInd = schemaModel.getIndividual(individualNameSpace + flowNodeId);
	        String type = flowNodeInd.getRDFType().getLocalName();
	        type = type.substring(0, 1).toUpperCase() + type.substring(1);
	        Class typeClass = null;
	        try {
              typeClass = Class.forName("org.camunda.bpm.model.bpmn.instance." + type);
	        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
              e.printStackTrace();
            }
	        
	        FlowNode bpmnFlowNode = (FlowNode)bpmnMI.newInstance(typeClass);
	        bpmnFlowNode.setId(flowNodeId);
	        
	        
	        if(flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "name")) != null){
	          String name = flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "name")).toString();
	          bpmnFlowNode.setName(name);
	        }
	        
	 
	        for(RDFNode outgoingNode : flowNodeInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_outgoing")).toList()){
	          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on outgoingNode object
	          String outgoingId = outgoingNode.asNode().getLocalName();
	          Outgoing outgoingSeqFlow =  bpmnMI.newInstance(Outgoing.class);
	          outgoingSeqFlow.setTextContent(outgoingId);
	          bpmnFlowNode.addChildElement(outgoingSeqFlow);
	        }

	        for(RDFNode incomingNode : flowNodeInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_incoming")).toList()){
	          //dirtyfix since one sequence does not get recognized as a node. Should use .asNode.getLocalName() on incomingNode object
	          String incomingId = incomingNode.asNode().getLocalName();
	          Incoming incomingSeqFlow =  bpmnMI.newInstance(Incoming.class);
	          incomingSeqFlow.setTextContent(incomingId);
	          bpmnFlowNode.addChildElement(incomingSeqFlow);
	        }
	        
	        if(bpmnFlowNode instanceof Activity){
	          int completionQuantity = Integer.parseInt(flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "completionQuantity")).asLiteral().getValue().toString());
	          ((Activity) bpmnFlowNode).setCompletionQuantity(completionQuantity);
	          int startQuantity = Integer.parseInt(flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "startQuantity")).asLiteral().getValue().toString());
	          ((Activity) bpmnFlowNode).setStartQuantity(startQuantity);
	          boolean isForCompensation = Boolean.parseBoolean(flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "isForCompensation")).toString());
	          ((Activity) bpmnFlowNode).setForCompensation(isForCompensation);
	        }
	        
	        if(bpmnFlowNode instanceof Gateway){
	          String gatewayDirection = flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "gatewayDirection")).toString();
	          ((Gateway) bpmnFlowNode).setGatewayDirection(GatewayDirection.valueOf(gatewayDirection));
	          if(bpmnFlowNode instanceof EventBasedGateway){
	            boolean isInstantiate = Boolean.parseBoolean(flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "instantiate")).toString());
	            ((EventBasedGateway) bpmnFlowNode).setInstantiate(isInstantiate);
	            String eventBasedGatewayType = flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "eventBasedGatewayType")).toString();
	            ((EventBasedGateway) bpmnFlowNode).setEventGatewayType(EventBasedGatewayType.valueOf(eventBasedGatewayType));
	          }
	        }
	        
	        
	        if(bpmnFlowNode instanceof Event){
	          if(bpmnFlowNode instanceof CatchEvent){
	            NodeIterator eventDefinitionIterator = flowNodeInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_eventDefinition"));
	            for(RDFNode eventDefinitionNode : eventDefinitionIterator.toList()){
	              Individual eventDefinitionInd = dataOntModel.getIndividual(eventDefinitionNode.asNode().getURI());
	              String eventDefintionClassType = eventDefinitionInd.getRDFType().getLocalName();
	              eventDefintionClassType = eventDefintionClassType.substring(0, 1).toUpperCase() + eventDefintionClassType.substring(1);
	              Class eventDefinitionClass = null;
	              try {
	                eventDefinitionClass = Class.forName("org.camunda.bpm.model.bpmn.instance." + eventDefintionClassType);
	              } catch (ClassNotFoundException e) {
	              // TODO Auto-generated catch block
	                e.printStackTrace();
	              }
	              EventDefinition bpmnEventDef = (EventDefinition)bpmnMI.newInstance(eventDefinitionClass);
	              System.out.println(eventDefinitionInd);
	              String eventDefinitionId = eventDefinitionInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
	              bpmnEventDef.setId(eventDefinitionId);
	              
	              
	              bpmnFlowNode.addChildElement(bpmnEventDef);
	            }
	            
	            if(bpmnFlowNode instanceof StartEvent){
	              boolean isInterrupting = flowNodeInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "isInterrupting")).asLiteral().getBoolean();
	              ((StartEvent) bpmnFlowNode).setInterrupting(isInterrupting);
	            }
	          }
	        }
	        
	        
	        
	        process.addChildElement(bpmnFlowNode);
	      }
	      
	      
	      
	      
	      //LaneSet has to be done when all flownodes of a process are initialized because of the ref.
	      
	      NodeIterator laneSetIterator = processInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_laneSet"));
	      
	      for(RDFNode laneSetNode : laneSetIterator.toList()){
	        String laneSetId = laneSetNode.asNode().getLocalName();
	        LaneSet bpmnLaneSet = bpmnMI.newInstance(LaneSet.class);
	        Individual laneSetInd = schemaModel.getIndividual(individualNameSpace + laneSetId);
	        bpmnLaneSet.setId(laneSetId);
	        
	        
	        
	        
	        for(RDFNode laneNode : laneSetInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_lane")).toList()){
	          String laneId = laneNode.asNode().getLocalName();
	          Lane bpmnLane = bpmnMI.newInstance(Lane.class);
	          Individual laneInd = schemaModel.getIndividual(individualNameSpace + laneId);
	          bpmnLane.setId(laneId);
	          
	          for(RDFNode flowNodeRef : laneInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_flowNode")).toList()){
	            FlowNodeRef bpmnFlowNodeRef = bpmnMI.newInstance(FlowNodeRef.class);
	            String flowNodeRefId = flowNodeRef.asNode().getLocalName();
	            bpmnFlowNodeRef.setTextContent(flowNodeRefId);
	            bpmnLane.addChildElement(bpmnFlowNodeRef);
	          }
	          
	          bpmnLaneSet.addChildElement(bpmnLane);
	        }
	        
	        process.addChildElement(bpmnLaneSet);
	      }
	      
	      
	    }
	    
	    //Has to be done after FlowNodes have been initialized because of the sourceRef and targetRef attribute.
        
	    for(Individual processInd : processIndividualsList){
	      String processId = processInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
	      Process process = bpmnMI.getModelElementById(processId);
	      
	      //SequenceFlow
          NodeIterator sequenceIterator = processInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_sequenceFlow"));
          
          for(RDFNode sequenceFlow : sequenceIterator.toList()){
            
            String sequenceFlowId = sequenceFlow.asNode().getLocalName();
            SequenceFlow bpmnSeqFlow = bpmnMI.newInstance(SequenceFlow.class);
            bpmnSeqFlow.setId(sequenceFlowId);
            Individual seqFlowInd = schemaModel.getIndividual(individualNameSpace + sequenceFlowId);
            
            RDFNode nameProperty = seqFlowInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"));
            if(nameProperty != null){
              bpmnSeqFlow.setName(nameProperty.toString());
            }
            
                
            String sourceRefUri = seqFlowInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_sourceRef")).toString();
            String sourceRef = dataOntModel.getIndividual(sourceRefUri).getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
            FlowNode bpmnSource = bpmnMI.getModelElementById(sourceRef);
            bpmnSeqFlow.setSource(bpmnSource);
            
            String targetRefUri = seqFlowInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_targetRef")).toString();
            String targetRef = dataOntModel.getIndividual(targetRefUri).getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
            FlowNode bpmnTarget = bpmnMI.getModelElementById(targetRef);
            bpmnSeqFlow.setTarget(bpmnTarget);
            
            process.addChildElement(bpmnSeqFlow);
          }
	      
	      
	    }
	    
	    ExtendedIterator<Individual> collaborationIndividuals = (ExtendedIterator<Individual>) schemaModel.getOntClass(SCHEMA_NAMESPACE + "collaboration").listInstances();
	    for(Individual collaborationInd : collaborationIndividuals.toList()){
	      String collaborationId = collaborationInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
	      Collaboration bpmnCollaboration = bpmnMI.newInstance(Collaboration.class);
	      bpmnCollaboration.setId(collaborationId);
	      
	      List<RDFNode> participants = collaborationInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_participant")).toList();
	      
	      for(RDFNode participantNode : participants){
	        String participantId = dataOntModel.getProperty(participantNode.asResource(), schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).getObject().toString();
	        Individual participantInd = dataOntModel.getIndividual(participantNode.asResource().getURI());
	        Participant bpmnParticipant = bpmnMI.newInstance(Participant.class);
	        bpmnParticipant.setId(participantId);
	        
	        RDFNode nameNode = participantInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"));
	        
	        if(nameNode != null){
	          bpmnParticipant.setName(nameNode.toString());
	        }
	        
	        String processRefUri = participantInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_processRef")).toString();
	        String processRefId = dataOntModel.getIndividual(processRefUri).getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
	        Process processRef = (Process)bpmnMI.getModelElementById(processRefId);
	        bpmnParticipant.setProcess(processRef);
	        
	     
	        
	        bpmnCollaboration.addChildElement(bpmnParticipant);
	      }
	      
	      List<RDFNode> messageFlows = collaborationInd.listPropertyValues(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_messageFlow")).toList();
	      for(RDFNode messageFlowNode : messageFlows){
	        String messageFlowId = dataOntModel.getProperty(messageFlowNode.asResource(), schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).getObject().toString();
	        Individual messageFlowInd = dataOntModel.getIndividual(messageFlowNode.asResource().getURI());
	        MessageFlow bpmnMessageFlow = bpmnMI.newInstance(MessageFlow.class);
	        bpmnMessageFlow.setId(messageFlowId);
	        
	        RDFNode nameNode = messageFlowInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "name"));
	        if(nameNode != null){
	          bpmnMessageFlow.setName(nameNode.toString());
	        }
	        
	        String sourceUri = messageFlowInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_sourceRef")).toString();
	        String sourceId = dataOntModel.getIndividual(sourceUri).getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
	        InteractionNode source = (InteractionNode)bpmnMI.getModelElementById(sourceId);
	        bpmnMessageFlow.setSource(source);
	        
	        String targetUri = messageFlowInd.getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "has_targetRef")).toString();
            String targetId = dataOntModel.getIndividual(targetUri).getPropertyValue(schemaModel.getProperty(SCHEMA_NAMESPACE + "id")).toString();
            InteractionNode target = (InteractionNode)bpmnMI.getModelElementById(targetId);
            bpmnMessageFlow.setTarget(target);
	        
	        bpmnCollaboration.addChildElement(bpmnMessageFlow);
	      }
	      
	      definitions.addChildElement(bpmnCollaboration);
	    }
	    
	    Bpmn.writeModelToFile(location, bpmnMI);
	    return location;
	
	}

}

package de.unima.core.io.impl;

import java.io.File;
import java.util.Collection;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
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
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import de.unima.core.io.BPMN20File;
import de.unima.core.io.Importer;

public class BPMN20ImporterImpl implements Importer<BPMN20File> {
	
	private String id;
	private String targetNs;
	
	private static final String SCHEMAPATH = "schema/BPMN_2.0_ontology.owl";
	private static final String SCHEMANA = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
	
	public BPMN20ImporterImpl(){
	  
	}
	
	public BPMN20ImporterImpl(String targetNs) {
	  this.targetNs = targetNs;
    }

	@Override
	public OntModel importData(BPMN20File bpmn_source) {
	    //Preparing jena models
	    OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	    schemaModel.read(SCHEMAPATH);
	    
	    OntModel ontModelInstance = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	    schemaModel.addSubModel(ontModelInstance);
	    
	    String blankSubnodeProperty = SCHEMANA + "has_";
	    
	    //Preparing camunda
	    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(bpmn_source.getPath()));
	    
	    Definitions defs = modelInstance.getDefinitions();
	    
//	    System.out.println(System.currentTimeMillis());
	    
	    Collection<Process> modelProcesses = modelInstance.getModelElementsByType(Process.class);

	    for(Process process : modelProcesses){
	      String nodeType = process.getElementType().getTypeName();
	      OntClass processClass = schemaModel.getOntClass(SCHEMANA + nodeType);
	      
	      String processId   = process.getId();     
	      Individual processInd = ontModelInstance.createIndividual(targetNs + processId, processClass);
	      
	      //setting attributes
	      processInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "id"), processId);
	      processInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "isExecutable"), process.isExecutable());
	      processInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "isClosed"), process.isClosed());
	      processInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "processType"), process.getProcessType().name());
	      String processName = process.getName();
	      if(processName != null){
	        processInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "name"), processName);
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
	        OntClass laneSetClass = schemaModel.getOntClass(SCHEMANA + laneSetNodeType);          
	        
	        String laneSetId = laneSet.getId();
	        Individual laneSetInd = ontModelInstance.createIndividual(targetNs + laneSetId, laneSetClass);
	        
	        laneSetInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "id"), laneSetId);
	        
	        for(Lane lane : laneSet.getLanes()){
	          String laneNodeType = lane.getElementType().getTypeName();
	          OntClass laneClass = schemaModel.getOntClass(SCHEMANA + laneNodeType);          
	          
	          String laneId = lane.getId();
	          Individual laneInd = ontModelInstance.createIndividual(targetNs + laneId, laneClass);
	          
	          laneInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "id"), laneId);
	          String laneName = lane.getName();
	          if(laneName != null){
	            laneInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "name"), laneName);
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
	          OntClass flowElementClass = schemaModel.getOntClass(SCHEMANA + flowElementNodeType);          
	          
	          String flowElementId = flowElement.getId();
	          Individual flowElementInd = ontModelInstance.createIndividual(targetNs + flowElementId, flowElementClass);
	          
	          //set the subnode for the process
	          processInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + flowElementNodeType), flowElementInd);
	          
	          //set attribute for flowElement
	          flowElementInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "id"), flowElementId);
	          String flowElementName = flowElement.getName();
	          if(flowElementName != null){
	            flowElementInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "name"), flowElementName);
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
	                  
	                  flowElementInd.addLiteral(ontModelInstance.getProperty(targetNs + "isInterrupting"), ((StartEvent) flowElement).isInterrupting());
	                } 
	              }      
	            }
	            
	            if(flowElement instanceof Activity){
	              flowElementInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "completionQuantity"), ((Activity) flowElement).getCompletionQuantity());
	              flowElementInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "startQuantity"), ((Activity) flowElement).getStartQuantity());
	              flowElementInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "isForCompensation"), ((Activity) flowElement).isForCompensation());
	            }
	            
	            if(flowElement instanceof Gateway) {
	              flowElementInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "gatewayDirection"), ((Gateway) flowElement).getGatewayDirection().name());
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
	      
	      
	      
	      
//	      for(FlowElement flowElement : process.getFlowElements()){
//	        System.out.println(flowElement.getElementType().getTypeName());
//	      }
	      
	      
	    }
	    
	    Collection<Collaboration> collaborations = modelInstance.getModelElementsByType(Collaboration.class);
	    
	    for(Collaboration collaboration : collaborations){
	      String collaborationNodeType = collaboration.getElementType().getTypeName();
	      OntClass collaborationClass = schemaModel.getOntClass(SCHEMANA + collaborationNodeType);
	      
	      String collaborationId   = collaboration.getId();     
	      Individual collaborationInd = ontModelInstance.createIndividual(targetNs + collaborationId, collaborationClass);
	      
	      collaborationInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "id"), collaborationId);
	      
	      Collection<Participant> participants = collaboration.getParticipants();
	      for(Participant participant : participants){
	        String participantNodeType = participant.getElementType().getTypeName();
	        OntClass participantClass = schemaModel.getOntClass(SCHEMANA + participantNodeType);
	        
	        String participantId = participant.getId();
	        Individual participantInd = ontModelInstance.createIndividual(targetNs + participantId, participantClass);
	        
	        collaborationInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + participantNodeType), participantId);
	        
	        
	        participantInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "id"), participantId);
	        
	        String participantName = participant.getName();
	        if(participantName != null){
	          participantInd.addLiteral(ontModelInstance.getProperty(SCHEMANA + "name"), participantName);
	        }
	        
	        participantInd.addProperty(ontModelInstance.getProperty(blankSubnodeProperty + "processRef"), targetNs + participant.getProcess().getId());
	        
	        
	      }
	      
	      
	    }
	    
	    
	    return ontModelInstance;
	}

	@Override
	public String getID() {

		return this.id;
	}
}

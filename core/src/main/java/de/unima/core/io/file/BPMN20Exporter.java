package de.unima.core.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.impl.BpmnModelInstanceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.Incoming;
import org.camunda.bpm.model.bpmn.impl.instance.Outgoing;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class BPMN20Exporter implements FileBasedExporter<Model> {
	
	private static final String SCHEMAPATH = "ontologies/BPMN_2.0_ontology.owl";

	@SuppressWarnings("unchecked")
	@Override
	public File exportToFile(Model data, File location) {
		 //Preparing jena models
	    OntModel schemaModel = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
		try (InputStream schemaInputStream = Resources.asByteSource(Resources.getResource(SCHEMAPATH)).openBufferedStream()){
			schemaModel.read(schemaInputStream, null);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	    schemaModel.addSubModel(data);
	    
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
	    Bpmn.writeModelToFile(location, bpmnMI);
	    return location;
	}

}

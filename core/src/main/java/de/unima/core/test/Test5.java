package de.unima.core.test;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test5 {

  public static void main(String[] args) throws Exception{
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    
    
    
    Document doc = parser.parse(new File("tmp/control-flow-example.bpmn"));
    
    Node definitions = doc.getElementsByTagName("bpmn2:definitions").item(0);
    String uriId = "http://test.org/projectSpace/randomuuid#";
    
    OntModel m = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
    
    m.read("schema/BPMN_2.0_ontology.owl");
    OntModel mi = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
    m.addSubModel(mi);
    System.out.println(m.size());
    
    String schemaNS = "http://dkm.fbk.eu/index.php/BPMN2_Ontology#";
    
    for(Node n : toList(definitions.getChildNodes())){
      String nodeNameSubDefinitions =  n.getNodeName();
      if(nodeNameSubDefinitions.contains(":")){
        nodeNameSubDefinitions = nodeNameSubDefinitions.substring(nodeNameSubDefinitions.indexOf(":")+1);
      }
      switch(nodeNameSubDefinitions){
        case "process": {
          
          String typeU = n.getNodeName().replace("bpmn2:", "");
          String idU = n.getAttributes().getNamedItem("id").getNodeValue();
          
          OntClass cU = m.getOntClass(schemaNS + typeU);
          
          Individual indU = mi.createIndividual(uriId + idU, cU);
          
          
          NamedNodeMap attributesU = n.getAttributes();
          
          
          //add attributes
          for(int i = 0; i < attributesU.getLength(); i++){
            String name = attributesU.item(i).getNodeName();
            String value = attributesU.item(i).getNodeValue();
            
            Property attrPropU = mi.getProperty(schemaNS + name);
            
            
            indU.addLiteral(attrPropU, value);
          }
          
          //add subnodes
          for(Node subElements : toList(n.getChildNodes())){
            if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
              //no supp of extension yet
              continue;
            }
            
            String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
            Property prop = mi.getProperty(property);
            indU.addProperty(prop, uriId + subElements.getAttributes().getNamedItem("id").getNodeValue());

          }
          
          
          for(Node elements : toList(n.getChildNodes())){
            switch(elements.getNodeName().replace("bpmn2:", "")){
              case "startEvent":{
                String type = elements.getNodeName().replace("bpmn2:", "");
                String id = elements.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = elements.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(elements.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
              case "endEvent":{
                String type = elements.getNodeName().replace("bpmn2:", "");
                String id = elements.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = elements.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(elements.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
              case "task":{
                String type = elements.getNodeName().replace("bpmn2:", "");
                String id = elements.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = elements.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(elements.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
              case "sequenceFlow":{
                String type = elements.getNodeName().replace("bpmn2:", "");
                String id = elements.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = elements.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(elements.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
              case "laneSet":{
                String type = elements.getNodeName().replace("bpmn2:", "");
                String id = elements.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = elements.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(elements.getChildNodes())){
                  switch(subElements.getNodeName().replace("bpmn2:", "")){
                    case "extensionElement":{
                      continue;
                    }
                    case "lane": {
                      for(Node subLane : toList(subElements.getChildNodes())){
                        switch(subLane.getNodeName().replace("bpmn2:", "")){
                          case "extensionElement":{
                            continue;
                          }
                          case "flowNodeRef":{
                            String property = schemaNS + "has_" + subLane.getNodeName().replace("bpmn2:", "");
                            Property prop = mi.getProperty(property);
                            ind.addProperty(prop, uriId + subLane.getTextContent());
                          }
                        }
                        
                      }
                    } 
                  }
                }
                
                
                break;
              }
              
              case "exclusiveGateway":{
                String type = elements.getNodeName().replace("bpmn2:", "");
                String id = elements.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = elements.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(elements.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
            }
            
          }
          
          break;
        }
        case "collaboration" :{
          
          String typeU = nodeNameSubDefinitions;
          String idU = n.getAttributes().getNamedItem("id").getNodeValue();
          
          OntClass cU = m.getOntClass(schemaNS + typeU);
          
          Individual indU = mi.createIndividual(uriId + idU, cU);
          
          
          NamedNodeMap attributesU = n.getAttributes();
          
          
          //add attributes
          for(int i = 0; i < attributesU.getLength(); i++){
            String name = attributesU.item(i).getNodeName();
            String value = attributesU.item(i).getNodeValue();
            
            Property attrPropU = mi.getProperty(schemaNS + name);
            
            
            indU.addLiteral(attrPropU, value);
          }
          
          //add subnodes
          for(Node subElements : toList(n.getChildNodes())){
            if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
              //no supp of extension yet
              continue;
            }
            
            String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
            Property prop = mi.getProperty(property);
            indU.addProperty(prop, uriId + subElements.getAttributes().getNamedItem("id").getNodeValue());

          }
          
          
          for(Node element : toList(n.getChildNodes())){
            switch(element.getNodeName().replace("bpmn2:", "")){
              case "participant": {
                String type = element.getNodeName().replace("bpmn2:", "");
                String id = element.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = element.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(element.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
              case "messageFlow": {
                String type = element.getNodeName().replace("bpmn2:", "");
                String id = element.getAttributes().getNamedItem("id").getNodeValue();
                
                OntClass c = m.getOntClass(schemaNS + type);
                
                Individual ind = mi.createIndividual(uriId + id, c);
                
                
                NamedNodeMap attributes = element.getAttributes();
                
                
                //add attributes
                for(int i = 0; i < attributes.getLength(); i++){
                  String name = attributes.item(i).getNodeName();
                  String value = attributes.item(i).getNodeValue();
                  
                  Property attrProp = mi.getProperty(schemaNS + name);
                  
                  ind.addLiteral(attrProp, value);
                }
                
                //add subnodes
                for(Node subElements : toList(element.getChildNodes())){
                  if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
                    //no supp of extension yet
                    continue;
                  }
                  String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
                  Property prop = mi.getProperty(property);
                  ind.addProperty(prop, uriId + subElements.getTextContent());
                  
                }
                
                
                break;
              }
            }
          }
          
          break;
        }
        case "BPMNDiagram": {
          String typeU = nodeNameSubDefinitions;
          String idU = n.getAttributes().getNamedItem("id").getNodeValue();
          
          OntClass cU = m.getOntClass(schemaNS + typeU);
          
          Individual indU = mi.createIndividual(uriId + idU, cU);
          
          
          NamedNodeMap attributesU = n.getAttributes();
          
          
          //add attributes
          for(int i = 0; i < attributesU.getLength(); i++){
            String name = attributesU.item(i).getNodeName();
            String value = attributesU.item(i).getNodeValue();
            
            Property attrPropU = mi.getProperty(schemaNS + name);
            
            
            indU.addLiteral(attrPropU, value);
          }
          for(Node subElements : toList(n.getChildNodes())){
            if(subElements.getNodeName().replace("bpmn2:", "").contains("extensionElements")){
              //no supp of extension yet
              continue;
            }
            
            String property = schemaNS + "has_" + subElements.getNodeName().replace("bpmn2:", "");
            Property prop = mi.getProperty(property);
            indU.addProperty(prop, uriId + subElements.getAttributes().getNamedItem("id").getNodeValue());

          }

          
          break;
        }
      }

    }
    
    System.out.println(mi.size());
    
    for(Statement s:mi.listStatements().toList()){
//      if(s.toString().contains("id-d6dd67"))
      System.out.println(s.toString());
    }
    
//    for(Individual ind : mi.listIndividuals().toList()){
//      System.out.println(ind.getURI());
//    }


  }
  
  private static ArrayList<Node> toList(NodeList nl){
    ArrayList<Node> result = new ArrayList<Node>();
    for(int i = 0; i < nl.getLength(); i++){
      if(!nl.item(i).getNodeName().equals("#text"))
      result.add(nl.item(i));
    }
    return result;
  }

}

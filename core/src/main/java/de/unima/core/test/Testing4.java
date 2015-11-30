package de.unima.core.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Testing4 {

  public static void main(String[] args) {
    try {
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      
      
      String url = "tmp/Semantic.xsd";
      
      Document doc = parser.parse(url);
      
      
      NodeList list = doc.getDocumentElement().getChildNodes();
      MappedTreeStructure<String> tree = new MappedTreeStructure<String>();
      
      for(int i = 0; i < list.getLength(); i++){
        if(list.item(i).getNodeName().equals("xsd:element")){
          tree.add(list.item(i).getAttributes().getNamedItem("type").getNodeValue(), list.item(i).getAttributes().getNamedItem("name").getNodeValue());
//          System.out.println(list.item(i).getAttributes().getNamedItem("name").getNodeValue() + "\t" + list.item(i).getAttributes().getNamedItem("type"));
          if(list.item(i+2).getNodeName().equals("xsd:complexType")){
//            System.out.println(list.item(i+2).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("base"));
            if(list.item(i+2).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("base") != null){
              tree.add(list.item(i+2).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("base").getNodeValue() , list.item(i).getAttributes().getNamedItem("type").getNodeValue());
            }
              
          }
        }
      }
      
//      System.out.println(tree);
//      
//      
//      System.out.println(tree.getAllParents("category"));
      
      
      
      BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("tmp/control-flow-example.bpmn"));
      
      System.out.println(modelInstance.getDefinitions().getTargetNamespace());
      
      Collection<Process> modelProcess = modelInstance.getModelElementsByType(Process.class);
      
      boolean foundOne = false;
      
      for(Process p : modelProcess){
        
        for(String node:tree.getAllNodes()){
          if (!node.matches("t([A-Z].*)")) {
//            System.out.println(node);
            Class type = null;
            try{
              type = Class.forName("org.camunda.bpm.model.bpmn.instance." + firstCharToUpper(node));
            } catch (Exception e){
              
            }
            if(type == null) continue;            
            for (Object object : p.getChildElementsByType(type)) {
              ModelElementInstance inst = (ModelElementInstance) object;
              
              if (node.equals(inst.getElementType().getTypeName())){
                List<String> parents = tree.getAllParents(node);
                System.out.println(parents);
                System.out.println(inst.getAttributeValue("id"));
                List<String> propertyList = getNestedElements(parents, doc);
                System.out.println(propertyList.toString());
                for(String prop : propertyList){
                  Class type2 = null;
                  try{
                    type2 = Class.forName("org.camunda.bpm.model.bpmn.instance." + firstCharToUpper(prop));
                  } catch (Exception e){
                    
                  }
                  System.out.println(type2);
                  if(type2 == null)continue;
                  System.out.println((inst.getChildElementsByType(type2).size()));
                }
                List<String> attributes = getAttributes(parents, doc);
                for(String attribute: attributes){
                  System.out.println(attribute + " : " + inst.getAttributeValue(attribute));
                }
                System.out.println(attributes.toString());
                foundOne=true;
              }
              
            }
          }
//            if(foundOne)break;
        }
        
      }
      
      
  }
  catch (Exception exp) {
      exp.printStackTrace(System.out);
  }

  }
  
  private static List<String> getNestedElements(List<String> parentNodes, Document doc){
    ArrayList<String> result = new ArrayList<>();
    NodeList nl = doc.getElementsByTagName("xsd:complexType");
    for(int i = 0; i < nl.getLength(); i++){
      Node n = nl.item(i);
      for(String parent : parentNodes){
        if(n.getAttributes().getNamedItem("name").getNodeValue().equals(parent)){
          for(int j = 0 ;  j < n.getChildNodes().getLength(); j++){
            Node nj = n.getChildNodes().item(j);
            if(isExtended(nj)){
              nj = nj.getChildNodes().item(1);
              if(nj.getChildNodes().getLength() != 0){
                nj = nj.getChildNodes().item(1);
              }
              
            }
          
            NodeList nl2 = nj.getChildNodes();
            for(int k = 1; k < nl2.getLength(); k = k+2){

              if(nl2.item(k).getAttributes().getNamedItem("ref") != null){
                result.add(nl2.item(k).getAttributes().getNamedItem("ref").getNodeValue());
              }
              if(nl2.item(k).getAttributes().getNamedItem("name") != null){
                result.add(nl2.item(k).getAttributes().getNamedItem("name").getNodeValue());
              }
            }
           }
         }
      }
//      
    }
    
    return result;
  }

  private static List<String> getAttributes(List<String> parentNodes, Document doc){
    ArrayList<String> result = new ArrayList<>();
    NodeList nl = doc.getElementsByTagName("xsd:complexType");
    for(int i = 0; i < nl.getLength(); i++){
      Node n = nl.item(i);
      for(String parent : parentNodes){
       
        if(n.getAttributes().getNamedItem("name").getNodeValue().equals(parent)){
          for(int j = 0 ;  j < n.getChildNodes().getLength(); j++){
            Node nj = n.getChildNodes().item(j);
            if(isExtended(nj)){
              nj = nj.getChildNodes().item(1);
              if(nj.getChildNodes().getLength() != 0){
               
                NodeList nl2 = nj.getChildNodes();
                for(int k = 1; k < nl2.getLength(); k = k+2){
                  Node nk = nl2.item(k);
                  if(nk.getNodeName().equals("xsd:attribute"))
                  result.add(nk.getAttributes().getNamedItem("name").getNodeValue());
                  
                }
              }

              if(nj.getNodeName().equals("xsd:attribute")){
                result.add(nj.getAttributes().getNamedItem("name").getNodeValue());
              }
            }else{
              if(nj.getNodeName().equals("xsd:attribute")){
                result.add(nj.getAttributes().getNamedItem("name").getNodeValue());
              }
            }
           }
         }
      }
//      
    }
    
    return result;
  }

  
  private static boolean isExtended(Node n){
    NodeList nl = n.getChildNodes();
    
    for(int i = 0; i < nl.getLength(); i++){
      if (nl.item(i).getNodeName().equals("xsd:extension")) {
        return true;
      }
    }
    
    return false;
  }
  

  private static String firstCharToUpper(String node) {
    return node.substring(0, 1).toUpperCase() + node.substring(1 , node.length());
  }

}

class MappedTreeStructure <N> {

  private final Map<N, N> nodeParent = new HashMap<N, N>();
  private final LinkedHashSet<N> nodeList = new LinkedHashSet<N>();

  public boolean add (N parent, N node) {

      boolean added = nodeList.add(node);
      nodeList.add(parent);
//      if (added) {
          nodeParent.put(node, parent);
//      }
      return added;
  }

  public boolean remove (N node, boolean cascade) {
      if (!nodeList.contains(node)) {
          return false;
      }
      if (cascade) {
          for (N child : getChildren(node)) {
              remove(child, true);
          }
      } else {
          for (N child : getChildren(node)) {
              nodeParent.remove(child);
          }
      }
      nodeList.remove(node);
      return true;
  }

  public List<N> getRoots () {
      return getChildren(null);
  }

  public N getParent (N node) {
      return nodeParent.get(node);
  }
  
  public List<N> getAllParents(N node){
    List<N> parents = new LinkedList<N>();
    N parent = nodeParent.get(node);
    while(parent != null){
      parents.add(parent);
      node = parent;
      parent = nodeParent.get(parent);
    }
    return parents;
  }
  
  public Set<N> getAllNodes(){
    return (Set<N>) this.nodeList;
  }

  public List<N> getChildren (N node) {
      List<N> children = new LinkedList<N>();
      for (N n : nodeList) {
          N parent = nodeParent.get(n);
          if (node == null && parent == null) {
              children.add(n);
          } else if (node != null && parent != null && parent.equals(node)) {
              children.add(n);
          }
      }
      return children;
  }

  @Override
  public String toString () {
      StringBuilder builder = new StringBuilder();
      dumpNodeStructure(builder, null, "- ");
      return builder.toString();
  }
  
  public void printTree(N node){
    StringBuilder builder = new StringBuilder();
    dumpNodeStructure(builder, node, "- ");
    System.out.println(builder.toString());
  }

  private void dumpNodeStructure (StringBuilder builder, N node, String prefix) {
      if (node != null) {
          builder.append(prefix);
          builder.append(node.toString());
          builder.append('\n');
          prefix = "    " + prefix;
      }
      for (N child : getChildren(node)) {
          dumpNodeStructure(builder, child, prefix);
      }
  }
}

package de.unima.core.test;

import java.io.File;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class Test6 {

  public static void main(String[] args) {
    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("tmp/Hohenheim.bpmn"));
    Class proClass = org.camunda.bpm.model.bpmn.instance.Process.class;
    System.out.println(modelInstance.getModelElementsByType(proClass).size());
    

  }

}

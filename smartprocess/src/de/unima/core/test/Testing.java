package de.unima.core.test;

import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import de.unima.core.spa.SPAController;
import de.unima.core.spa.impl.SPAControllerImpl;

public class Testing {

    public static void main(String[] args) {
        
//      
        
        Model model = RDFDataMgr.loadModel("tmp/rdf-example.rdf");
//        
        SPAController controller = new SPAControllerImpl();
//        
//        System.out.println(model.listStatements().toList());
//        
//        String id = controller.storeProcessModel(model);
//
//        System.out.println(id);
        
        
        String id = "http://spa.org/id/7cbb6db5-ca13-4e7f-8e95-b991e2fe6636";
        
        Model model2 = controller.retrieveProcessModel(id);
        
        System.out.println(model2.size());
        
//        Map<String,String> map =model2.getNsPrefixMap();
        
        System.out.println(model2.listStatements().toList());
        
    }

}

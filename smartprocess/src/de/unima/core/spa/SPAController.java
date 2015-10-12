package de.unima.core.spa;

import org.apache.jena.rdf.model.Model;

public interface SPAController {

    public String storeProcessModel(Model processModel);
    
    public Model retrieveProcessModel(String id);
    
    public Model updateProcessModel(String id, Model processModel);
    
}

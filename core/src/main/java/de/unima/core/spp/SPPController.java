package de.unima.core.spp;

import org.apache.jena.rdf.model.Model;

public interface SPPController {

    public String storeProcessModel(Model processModel);
    
    public Model retrieveProcessModel(String id);
    
    public Model updateProcessModel(String id, Model processModel);
    
}

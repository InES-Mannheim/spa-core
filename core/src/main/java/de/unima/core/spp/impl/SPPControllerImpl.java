package de.unima.core.spp.impl;

import java.util.UUID;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;
import de.unima.core.spp.SPPController;

public class SPPControllerImpl implements SPPController{
    
    
    private final String DBDIRECTORY = "db/test";

    @Override
    public String storeProcessModel(Model processModel) {
        String id = "http://spa.org/id/" + UUID.randomUUID().toString();
        Dataset dataset = TDBFactory.createDataset(this.DBDIRECTORY);
        
        dataset.addNamedModel(id, processModel);
        
        dataset.end();
        
        TDB.sync(dataset);
        
        return id;
    }

    @Override
    public Model retrieveProcessModel(String id) {
        Dataset dataset = TDBFactory.createDataset(this.DBDIRECTORY);
        
        Model model = dataset.getNamedModel(id);     
        
        dataset.end();
        return model;
    }

    @Override
    public Model updateProcessModel(String id, Model processModel) {
        // TODO Auto-generated method stub
        return null;
    }

    
}

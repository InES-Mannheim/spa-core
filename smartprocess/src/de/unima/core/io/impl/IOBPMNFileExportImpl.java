package de.unima.core.io.impl;

import java.io.File;

import de.unima.core.io.IOFileExportAbstract;

public class IOBPMNFileExportImpl extends IOFileExportAbstract {

    private static IOBPMNFileExportImpl instance = null;
    
    private IOBPMNFileExportImpl() {
        
    }
    
    
    @Override
    public File exportFile(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public static IOBPMNFileExportImpl getInstance() {
        if (instance == null) {
            instance = new IOBPMNFileExportImpl();
        }
        return instance;
    }
    
}

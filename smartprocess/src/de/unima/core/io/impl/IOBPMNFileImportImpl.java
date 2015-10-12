package de.unima.core.io.impl;

import java.io.File;

import de.unima.core.io.IOFileImportAbstract;

public class IOBPMNFileImportImpl extends IOFileImportAbstract {

    private static IOBPMNFileImportImpl instance = null;
    
    private IOBPMNFileImportImpl() {
        
    }
    
    @Override
    public String importFile(File file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String replaceFile(File file, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public static IOBPMNFileImportImpl getInstance() {
        if (instance == null) {
            instance = new IOBPMNFileImportImpl();
        }
        return instance;
    }
    
}

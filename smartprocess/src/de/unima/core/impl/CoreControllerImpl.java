package de.unima.core.impl;

import java.io.File;

import de.unima.core.CoreController;
import de.unima.core.io.impl.IOBPMNFileExportImpl;
import de.unima.core.io.impl.IOBPMNFileImportImpl;

public class CoreControllerImpl implements CoreController {

    @Override
    public String importBpmn(File bpmnFile) {
        IOBPMNFileImportImpl fileImporter = IOBPMNFileImportImpl.getInstance();
        
        String id = fileImporter.importFile(bpmnFile);
        
        return id;
    }

    @Override
    public File exportBpmn(String id) {
        IOBPMNFileExportImpl fileExporter = IOBPMNFileExportImpl.getInstance();
        
        File bpmnFile = fileExporter.exportFile(id);
        
        return bpmnFile;
    }

    @Override
    public String replaceBpmn(File bpmnFile, String id) {
        IOBPMNFileImportImpl fileImporter = IOBPMNFileImportImpl.getInstance();
        
        String newId = fileImporter.replaceFile(bpmnFile, id);
        
        return newId;
    }

}

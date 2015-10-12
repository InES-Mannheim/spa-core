package de.unima.core.io;

import java.io.File;

public interface FileImport {

    public String importFile(File file);
    
    public String replaceFile(File file, String id);
    
}

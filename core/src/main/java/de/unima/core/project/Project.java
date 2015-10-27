package de.unima.core.project;

import de.unima.core.api.ProjectType;
import de.unima.core.api.Source;

public interface Project {

  ProjectType getProjectType();
  
  String importDataToProject(Source src);
  
  Source exportDataFromProject(String id);
  
}

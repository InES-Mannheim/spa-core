package de.unima.core.project.impl;

import java.util.Set;

import de.unima.core.api.ProjectType;
import de.unima.core.api.Source;
import de.unima.core.datamodel.DataStore;
import de.unima.core.project.Project;

public class ProjectImpl implements Project {
  
  private String id;
  private ProjectType type;
  private Set<DataStore> dataStores;

  public ProjectType getProjectType() {
    // TODO Auto-generated method stub
    return null;
  }

  public String importDataToProject(Source src) {
    // TODO Auto-generated method stub
    return null;
  }

  public Source exportDataFromProject(String id) {
    // TODO Auto-generated method stub
    return null;
  }

}

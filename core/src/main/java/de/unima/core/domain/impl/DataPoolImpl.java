package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.domain.DataPool;
import de.unima.core.domain.DataModel;
import de.unima.core.domain.Project;
import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;

public class DataPoolImpl implements DataPool {

  private OntModel data;
  private Project project;
  private String id;
  private Map<String, DataModel> datamodels;
  
  public DataPoolImpl(String i, Project p) {
	  
	  this.id = i;
	  this.datamodels = new HashMap<String, DataModel>();
	  this.data = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	  this.project = p;
	  this.loadSchemes();	  
  }

  public void loadDataPool() {

	  for (String dm : this.datamodels.keySet()) {
		  
		  this.loadDataIntoDataPool(dm);
	  }
	  this.loadSchemes();
  }
  
  @Override
  public boolean addDataModel(String i, IOObject<DataSource> ioo) {

	  this.loadDataPool();
	  OntModel data_bak = this.data;
	  DataModel dm = new DataModelImpl(i, ioo.getData());
	  this.data.add(dm.getData());
	  
	  if (this.isValid()) {
		  
		  this.datamodels.put(i, dm);
		  return true;
	  
	  } else {
		  
		  this.data = data_bak;
		  System.err.println("New data model " + i + " is invalid for current data pool.");
		  return false;
	  }	  
  }
  
  @Override
  public boolean isValid() {
  	// TODO Auto-generated method stub
  	return true;
  }
  
  private void loadDataIntoDataPool(String id) {
	  
	  // TODO load rdf data from triple store and fill this.data
  }
  
  private void loadSchemes() {
	  
	  for (String ds : this.project.getSchemeIDs()) {
		  
		  this.loadDataIntoDataPool(ds);
	  }

  }





}

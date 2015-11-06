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
  public boolean addDataModel(String id, IOObject<DataSource> ioo) {

	  this.loadDataPool();
	  OntModel data_bak = this.data;
	  DataModel dm = new DataModelImpl(id, ioo.getData());
	  this.data.add(dm.getData());
	  
	  if (this.validate()) {
		  
		  return true;
	  
	  } else {
		  
		  this.data = data_bak;
		  this.datamodels.remove(id);
		  System.err.println("New data model " + id + " is invalid for current data pool");
		  return false;
	  }	  
  }
  
  @Override
  public boolean validate() {
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

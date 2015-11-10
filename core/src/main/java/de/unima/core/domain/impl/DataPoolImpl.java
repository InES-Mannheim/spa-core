package de.unima.core.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import de.unima.core.domain.DataModel;
import de.unima.core.domain.DataPool;
import de.unima.core.domain.Project;
import de.unima.core.io.DataSource;
import de.unima.core.io.IOObject;
import de.unima.core.persistence.Store;

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

  public boolean updateDataPool() {

	  boolean state = true;
	  
	  state = this.loadSchemes();

	  for (DataModel dm : this.datamodels.values()) {
		  
		  if (!dm.load()) {
			  	  
			  System.err.println("Data model " + dm.getId() + " is unable to load from storage system.");
			  state = false;
		  
		  } else {
			
			  this.data.add(dm.getData());
		  }
	  }
	  
	  return state;
  }
  
  @Override
  public boolean addDataModel(String i, IOObject<? extends DataSource> ioo) {

	  this.updateDataPool();
	  OntModel data_bak = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_MEM));
	  data_bak.add(this.data);
	  DataModel dm = new DataModelImpl(i, ioo.getData(), Store.fake());
	  this.data.add(dm.getData());
	  
	  if (this.isValid()) {
		  
		  this.datamodels.put(i, dm);
		  dm.save();
		  return true;
	  
	  } else {
		  
		  this.data = data_bak;
		  System.err.println("New data model " + i + " is invalid related to current data pool.");
		  return false;
	  }	  
  }
  
  @Override
  public boolean isValid() {
  	// TODO Auto-generated method stub
  	return true;
  }
  
  private boolean loadSchemes() {
	  
	  boolean state = true;
	  
	  for (String sID : this.project.getSchemeIDs()) {
		  
		  if (!this.project.getRepository().getDataScheme(sID).load()) {
			  
			  System.err.println("Data scheme " + sID + " is unable to load from storage system.");
			  state = false;
		  }
		  
		  this.data.add(this.project.getRepository().getDataScheme(sID).getData());
	  }

	  return state;
  }

}

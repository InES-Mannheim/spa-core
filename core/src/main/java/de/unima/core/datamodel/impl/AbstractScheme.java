package de.unima.core.datamodel.impl;

import org.apache.jena.rdf.model.Model;

import de.unima.core.datamodel.Scheme;

public abstract class AbstractScheme implements Scheme {
  
  protected String id;
  protected Model jenaModel;
  
}

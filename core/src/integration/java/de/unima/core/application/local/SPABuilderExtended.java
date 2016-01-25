package de.unima.core.application.local;

import org.apache.commons.lang3.NotImplementedException;

import de.unima.core.persistence.local.LocalPersistenceService;

public class SPABuilderExtended extends SPABuilder {
	
	@Override
	public RemoteBuilderExtended remote() {
		return new RemoteBuilderExtended();
	}
	
	public class RemoteBuilderExtended extends RemoteBuilder {
		
		public FusekiBuilder fuseki() {
			return new FusekiBuilder();
		}
		
		class FusekiBuilder extends VirtuosoBuilder {
			
			@Override
			protected LocalPersistenceService getPersistenceService() {
				throw new NotImplementedException("Fuseki Builder is not implemented yet.");
			}
		}
	}
	

}

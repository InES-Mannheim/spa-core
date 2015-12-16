/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.info.impl;

import java.util.HashMap;

import org.deckfour.xes.info.XAttributeNameMap;
import org.deckfour.xes.model.XAttribute;

/**
 * Implements an attribute name mapping.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XAttributeNameMapImpl implements XAttributeNameMap {
	
	/**
	 * Name of the mapping.
	 */
	private final String name;
	/**
	 * Map for storing the name mapping.
	 */
	private HashMap<String,String> mapping;
	
	/**
	 * Creates a new attribute name mapping instance.
	 * 
	 * @param name Name of the mapping.
	 */
	public XAttributeNameMapImpl(String name) {
		this.name = name;
		this.mapping = new HashMap<String,String>();
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XAttributeNameMap#getMappingName()
	 */
	public String getMappingName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XAttributeNameMap#map(org.deckfour.xes.model.XAttribute)
	 */
	public String map(XAttribute attribute) {
		return map(attribute.getKey());
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XAttributeNameMap#map(java.lang.String)
	 */
	public String map(String attributeKey) {
		return mapping.get(attributeKey);
	}
	
	/**
	 * Registers a mapping for a given attribute.
	 * 
	 * @param attribute Attribute for which to register a mapping.
	 * @param alias Alias string to map the attribute to.
	 */
	public void registerMapping(XAttribute attribute, String alias) {
		registerMapping(attribute.getKey(), alias);
	}
	
	/**
	 * Registers a mapping for a given attribute key.
	 * 
	 * @param attributeKey Attribute key for which to register a mapping.
	 * @param alias Alias string to map the attribute key to.
	 */
	public void registerMapping(String attributeKey, String alias) {
		mapping.put(attributeKey, alias);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Attribute name map: ");
		sb.append(name);
		for(String key : mapping.keySet()) {
			sb.append("\n");
			sb.append(key);
			sb.append(" -> ");
			sb.append(mapping.get(key));
		}
		return sb.toString();
	}
}

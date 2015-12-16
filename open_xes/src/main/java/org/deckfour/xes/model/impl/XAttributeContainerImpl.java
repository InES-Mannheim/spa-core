/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2014 Christian W. Guenther (christian@deckfour.org)
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
package org.deckfour.xes.model.impl;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttributeContainer;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public class XAttributeContainerImpl extends XAttributeCollectionImpl implements XAttributeContainer {

	/*
	 * For backwards compatibility, Container extends from Literal. As a result, software
	 * that is unaware of the Container may consider it to be a Literal. 
	 */
	
	public XAttributeContainerImpl(String key, XExtension extension) {
		// Dummy (but unique) key, dummy value, no extension.
		super(key, extension);
		// No separate collection required, existing map will do fine.
		collection = null;
	}
}

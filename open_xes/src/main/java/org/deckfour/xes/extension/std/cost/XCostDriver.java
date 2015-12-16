/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2012 Christian W. Guenther (christian@deckfour.org)
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
package org.deckfour.xes.extension.std.cost;

import org.deckfour.xes.extension.std.XAbstractNestedAttributeSupport;
import org.deckfour.xes.extension.std.XCostExtension;
import org.deckfour.xes.model.XAttribute;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public class XCostDriver extends XAbstractNestedAttributeSupport<String> {

	private transient static XCostDriver singleton = new XCostDriver();
	
	public static XCostDriver instance() {
		return singleton;
	}
	
	private XCostDriver() {	
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.extension.std.cost.XCostGenericSupport#extractValue(org.deckfour.xes.model.XAttributable)
	 */
	@Override
	public String extractValue(XAttribute attribute) {
		return XCostExtension.instance().extractDriver(attribute);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.extension.std.cost.XCostGenericSupport#assignValue(org.deckfour.xes.model.XAttributable, java.lang.Object)
	 */
	@Override
	public void assignValue(XAttribute attribute, String value) {
		XCostExtension.instance().assignDriver(attribute, value);
	}

}
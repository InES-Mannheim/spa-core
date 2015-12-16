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
package org.deckfour.xes.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;

/**
 * Implementation for the XLog interface.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XLogImpl extends ArrayList<XTrace> implements XLog {

	/**
	 * serial version UID.
	 */
	private static final long serialVersionUID = -9192919845877466525L;
	
	/**
	 * Map of attributes for this log.
	 */
	private XAttributeMap attributes;
	/**
	 * Extensions.
	 */
	private Set<XExtension> extensions;
	/**
	 * Classifiers.
	 */
	private List<XEventClassifier> classifiers;
	/**
	 * Global trace attributes.
	 */
	private List<XAttribute> globalTraceAttributes;
	/**
	 * Global event attributes.
	 */
	private List<XAttribute> globalEventAttributes;
	
	/**
	 * Single-item cache. Only the last info is cached. 
	 * Typically, only one classifier will be used for a log.
	 */
	private XEventClassifier cachedClassifier;
	private XLogInfo cachedInfo;
	
	/**
	 * Creates a new log.
	 * 
	 * @param attributeMap The attribute map used to store this
	 * 	log's attributes.
	 */
	public XLogImpl(XAttributeMap attributeMap) {
		this.attributes = attributeMap;
		this.extensions = new HashSet<XExtension>();
		this.classifiers = new ArrayList<XEventClassifier>();
		this.globalTraceAttributes = new ArrayList<XAttribute>();
		this.globalEventAttributes = new ArrayList<XAttribute>();
		this.cachedClassifier = null;
		this.cachedInfo = null;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XAttributable#getAttributes()
	 */
	public XAttributeMap getAttributes() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XAttributable#setAttributes(java.util.Map)
	 */
	public void setAttributes(XAttributeMap attributes) {
		this.attributes = attributes;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XAttributable#hasAttributes()
	 */
	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XAttributable#getExtensions()
	 */
	public Set<XExtension> getExtensions() {
		return extensions;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#clone()
	 */
	public Object clone() {
		XLogImpl clone = (XLogImpl) super.clone();
		clone.attributes = (XAttributeMap)attributes.clone();
		clone.extensions = new HashSet<XExtension>(extensions);
		clone.classifiers = new ArrayList<XEventClassifier>(classifiers);
		clone.globalTraceAttributes = new ArrayList<XAttribute>(globalTraceAttributes);
		clone.globalEventAttributes = new ArrayList<XAttribute>(globalEventAttributes);
		clone.cachedClassifier = null;
		clone.cachedInfo = null;
		clone.clear();
		for(XTrace trace : this) {
			clone.add((XTrace)trace.clone());
		}
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XLog#getClassifiers()
	 */
	public List<XEventClassifier> getClassifiers() {
		return classifiers;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XLog#getGlobalEventAttributes()
	 */
	public List<XAttribute> getGlobalEventAttributes() {
		return globalEventAttributes;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XLog#getGlobalTraceAttributes()
	 */
	public List<XAttribute> getGlobalTraceAttributes() {
		return globalTraceAttributes;
	}

	/*
	 * Runs the given visitor on this log.
	 * 
	 * (non-Javadoc)
	 * @see org.deckfour.xes.model.XLog#accept(org.deckfour.xes.model.XVisitor)
	 */
	public boolean accept(XVisitor visitor) {
		/*
		 * Check whether the visitor may run.
		 */
		if (visitor.precondition()) {
			/*
			 * Yes, it may. Now initialize.
			 */
			visitor.init(this);
			/*
			 * First call.
			 */
			visitor.visitLogPre(this);
			/*
			 * Visit the extensions.
			 */
			for (XExtension extension: extensions) {
				extension.accept(visitor, this);
			}
			/*
			 * Visit the classifiers.
			 */
			for (XEventClassifier classifier: classifiers) {
				classifier.accept(visitor, this);
			}
			/*
			 * Visit the attributes.
			 */
			for (XAttribute attribute: attributes.values()) {
				attribute.accept(visitor, this);
			}
			/*
			 * Visit the traces.
			 */
			for (XTrace trace: this) {
				trace.accept(visitor, this);
			}
			/*
			 * Last call.
			 */
			visitor.visitLogPost(this);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the cached info if the given classifier is the cached classifier.
	 * Returns null otherwise.
	 */
	public XLogInfo getInfo(XEventClassifier classifier) {
		return classifier.equals(cachedClassifier) ? cachedInfo : null;
	}
	
	/**
	 * Sets the cached classifier and info to the given objects.
	 */
	public void setInfo(XEventClassifier classifier, XLogInfo info) {
		cachedClassifier = classifier;
		cachedInfo = info;
	}
}

/*
 * Copyright (c) 2013 F. Mannhardt (f.mannhardt@tue.nl)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package org.deckfour.xes.util;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.deckfour.xes.util.XsDateTimeConversion;

/**
 * Provides a faster conversion of DateTime for XES serialization using the new
 * parse patterns of Java 7
 * 
 * @author F. Mannhardt
 */
public class XsDateTimeConversionJava7 extends XsDateTimeConversion {

	public static boolean IS_JAVA7 = System.getProperty("java.version").startsWith("1.7");
	private static final ThreadLocal<SoftReference<DateFormat>> THREAD_LOCAL_DF = new ThreadLocal<SoftReference<DateFormat>>();

	/**
	 * Returns a DateFormat for each calling thread, using {@link ThreadLocal}.
	 * 
	 * @return a DateFormat that is safe to use in multi-threaded environments
	 */
	private static DateFormat getDateFormat() {
		if (IS_JAVA7) {
			SoftReference<DateFormat> softReference = THREAD_LOCAL_DF.get();
			if (softReference != null) {
				DateFormat dateFormat = softReference.get();
				if (dateFormat != null) {
					return dateFormat;
				}
			}
			DateFormat result = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
			softReference = new SoftReference<DateFormat>(result);
			THREAD_LOCAL_DF.set(softReference);
			return result;
		} else {
			throw new RuntimeException("Error parsing XES log. This method should not be called unless running on Java 7!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.util.XsDateTimeConversion#parseXsDateTime(java.lang.
	 * String)
	 */
	public Date parseXsDateTime(String xsDateTime) {
		// Try Java 7 parsing method
		if (IS_JAVA7) {
			// Use with ParsePosition to avoid throwing and catching a lot of exceptions, if our parsing method does not work
			Date parsedDate = getDateFormat().parse(xsDateTime, new ParsePosition(0));
			if (parsedDate == null) {	
				// Fallback to old Java 6 method
				return super.parseXsDateTime(xsDateTime);
			} else {
				return parsedDate;
			}
		} else {
			// Fallback to old Java 6 method
			return super.parseXsDateTime(xsDateTime);
		}
	}

}
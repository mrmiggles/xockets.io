/*
 * © Copyright Tek Counsel LLC 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */


package com.tc.utils;

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * @author Mark W Ambler
 * mambler@tekcounsel.net
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class SearchCompare extends BeanCompare {

	private static final Logger logger = Logger.getLogger(SearchCompare.class.getName());
	private ReflectionUtils reflect=new ReflectionUtils();

	@Override
	public int compare(Object o1, Object o2) {
		
		Comparable data1 = null;
		Comparable data2 = null;
		int comp = 0;


		if (o1.getClass().equals(o2.getClass())) {
			comp = super.compare(o1, o2);
		} else {

			try {
				data1 = (Comparable) reflect.invokeMethod(getter, o1);
				data2 = (Comparable) o2;
			} catch (java.lang.ClassCastException e) {
				logger.log(Level.SEVERE,null, e);
			}

			if (desc) {
				comp = data2.compareTo(data1);
			} else {
				comp = data1.compareTo(data2);
			}
		}
		return comp;
	}
}

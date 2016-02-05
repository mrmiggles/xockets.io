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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

@SuppressWarnings({"unchecked","rawtypes"})
public class BeanBinder {
	

	private final static Logger logger = Logger.getLogger(BeanBinder.class.getName());
	
	 

	public void bind(Document doc, Object bean){
		ReflectionUtils reflect = new ReflectionUtils();
		PropertyDescriptor[] props = reflect.getProperties(bean);
		
		try {
			for(PropertyDescriptor prop : props){
				Vector values =doc.getItemValue(prop.getName());
				Object value = values.elementAt(0);
				
				if(value instanceof DateTime){
					DateTime dt = (DateTime) value;
					value = dt.toJavaDate();
					if(values.size() > 1){
						value = this.toDateList(values);
					}
				
				}else if(value instanceof String && ("true".equals(value) || "false".equals(value))){
					
					if("true".equals(value)){
						value = true;
					}else{
						value = false;
					}
				}
				
				
				else{
					//check to see if its a multi-value field
					if(values.size() > 1){
						value = this.toList(values);
					}
				}
				
				//write to the bean.
				
				if("".equals(value) || null == value){
					reflect.invokeSetter(prop.getWriteMethod(), bean, null);
				}else{
					reflect.invokeSetter(prop.getWriteMethod(), bean, value);
				}
			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Uncaught exception", e);
		}

	}
	

	
	
	
	public void bind(Object bean, Document doc){
		ReflectionUtils reflect = new ReflectionUtils();
		PropertyDescriptor[] props = reflect.getProperties(bean);
		Object values=null;
		try {
			for(PropertyDescriptor prop : props){
				
				values = reflect.invokeMethod(prop.getReadMethod(), bean);
				
				if(values instanceof List){
					List list = (List) values;
					Object o = list.get(0);
					if(o instanceof Date){
						Vector vec = this.toDateTimeVec(doc.getParentDatabase().getParent(), list);
						doc.replaceItemValue(prop.getName(), vec);
					
					}else{
						Vector vec = this.toVec(list);
						doc.replaceItemValue(prop.getName(), vec);
					}
				}else if(values instanceof Class){
					doc.replaceItemValue(prop.getName(), String.valueOf(values));
					
				}else if(values instanceof Boolean){
					doc.replaceItemValue(prop.getName(), String.valueOf(values).toLowerCase());
				
				}else{
					if(values==null){
						values="";
					}
					logger.log(Level.FINE,"saving " + prop.getName() + " to document ");
					doc.replaceItemValue(prop.getName(), values);
				}

			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Uncaught exception", e);
		}

	}
	
	
	
	public List toList(Vector vec){
		List list =new ArrayList(vec.size());
		list.addAll(vec);
		return list;
	}
	
	
	
	public List<Date> toDateList(Vector vec) throws NotesException{
		List list =new ArrayList(vec.size());
		for(Object o : vec){
			DateTime dt = (DateTime) o;
			list.add(dt.toJavaDate());
		}
		return list;
	}
	
	
	
	
	public Vector toVec(List list){
		Vector vec = new Vector(list.size());
		vec.addAll(list);
		return vec;
	}
	
	
	
	public Vector toDateTimeVec(Session session, List list) throws NotesException{
		Vector vec = new Vector (list.size());
		for(Object o: list){
			Date dt = (Date) o;
			DateTime dateTime = session.createDateTime(dt);
			vec.add(dateTime);
		}
		return vec;
	}
	

}

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


package com.tc.utils.converters;

import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.tc.utils.StrUtils;


public class MultiValueNewLineConverter implements Converter {
	
	private static final String SPLIT_CHARS="\r\n";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String newValue) {
		Vector<String> vector = new Vector<String>();
		String[] arr = newValue.split(SPLIT_CHARS);
		for(String str: arr){
			if(!StrUtils.isEmpty(str)){
				vector.add(str);
			}
		}
		return vector;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		
		if(value instanceof String ){
			return (String) value;
		}
		
		
		Vector<String> vector = (Vector<String>) value;
		StringBuilder builder = new StringBuilder();
		
		for(String str : vector){
			builder.append(str);
			builder.append(SPLIT_CHARS);
		}
		
		return builder.toString().substring(0, builder.length() -1);
		
		
	}

}

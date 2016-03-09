/*
 * © Copyright Tek Counsel LLC 2016
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

package com.tc.websocket.jsf;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Activator;

public class GuiceVariableResolver extends VariableResolver {

	private static final String TARGET_PACKAGE="com.tc.websocket";
	private VariableResolver resolver;

	public GuiceVariableResolver(VariableResolver resolver){
		this.resolver=resolver;
	}

	
	@Override
	public Object resolveVariable(FacesContext facesContext, String varName)throws EvaluationException {
		IGuicer guicer = Guicer.getInstance(Activator.bundle);

		Object o = null;

		//create instance standard jsf/xpage way.
		o = resolver.resolveVariable(facesContext, varName);
		
		//if the object belongs to com.tc.websocket, attempt to inject the object if needed
		if(o!=null && o.getClass().getName().startsWith(TARGET_PACKAGE)){
			guicer.inject(o);
		}

		return o;
	}

}

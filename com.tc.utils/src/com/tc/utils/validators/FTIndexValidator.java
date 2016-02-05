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


package com.tc.utils.validators;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import lotus.domino.Database;
import lotus.domino.NotesException;

import com.ibm.domino.osgi.core.context.ContextInfo;


public class FTIndexValidator implements Validator {

	public static final Logger logger = Logger.getLogger(FTIndexValidator.class.getName());

	
	public FTIndexValidator(){
		
	}


	@Override
	public void validate(FacesContext context, UIComponent ui, Object value)throws ValidatorException {
		try {
			Database db = ContextInfo.getUserDatabase();
			if(!db.isFTIndexed()){
				String errorMsg = "Application is not full text index.  Please contact your administrator.";
				FacesMessage message = new FacesMessage();
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				message.setDetail(errorMsg);
				message.setSummary(errorMsg);
				throw new ValidatorException(message);
			}
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);
		}
	}

}

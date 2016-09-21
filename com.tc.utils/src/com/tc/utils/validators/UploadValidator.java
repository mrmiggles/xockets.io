package com.tc.utils.validators;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.ibm.xsp.complex.Attr;
import com.ibm.xsp.component.xp.XspFileUpload;
import com.ibm.xsp.http.UploadedFile;
import com.tc.utils.ColUtils;
import com.tc.utils.StringCache;
import com.tc.utils.XSPUtils;

public class UploadValidator implements Validator {
	
	private static final String ALLOWED_FILE_TYPES="allowedFileTypes";
	private static final String ERROR_MSG="errorMessage";
	private static final String MAX_SIZE="maxSize";

	@Override
	public void validate(FacesContext context, UIComponent comp, Object value)throws ValidatorException {
		
		String[] allowed = null;
		
		XspFileUpload fileUpload = (XspFileUpload) comp;
		
		
		//get a handle on the custom attributes.
		List<Attr> list = fileUpload.getAttrs();
		Attr attrAllowed = ColUtils.findObject(list, "getName", ALLOWED_FILE_TYPES, false);
		Attr maxSize = ColUtils.findObject(list,"getName", MAX_SIZE, false);
		Attr attrError = ColUtils.findObject(list,"getName", ERROR_MSG, false);

		//allowedFileTypes is required.
		if(attrAllowed == null) throw new IllegalArgumentException("Please add allowedFileTypes to custom attributes.");
		
		//create our array.
		allowed = attrAllowed.getValue().split(StringCache.COMMA);
		
		UploadedFile ufile = (UploadedFile) value;
		boolean passed = false;

		for(String str : allowed){
			if(ufile.getClientFileName().toLowerCase().endsWith(str)){
				passed = true;
				break;
			}
		}	
		
		
		if(!passed){
			String msg = attrError!=null ? attrError.getValue().replace("{fileName}", ufile.getClientFileName()) : ufile.getClientFileName() + " is not an allowed type.";
			ufile.getServerFile().delete();
			XSPUtils.throwValidatorException(msg);
			return;
		}
		
		
		if(ufile.getServerFile().length() > Long.parseLong(maxSize.getValue())){
			String msg = attrError!=null ? attrError.getValue().replace("{fileName}", ufile.getClientFileName()) : ufile.getClientFileName() + " " + ufile.getServerFile().length() +  " is too large.";
			ufile.getServerFile().delete(); //delete the file.
			XSPUtils.throwValidatorException(msg);
			return;
		}
		

	}

}

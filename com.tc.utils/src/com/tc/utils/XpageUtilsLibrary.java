package com.tc.utils;

import com.ibm.xsp.library.AbstractXspLibrary;

public class XpageUtilsLibrary extends AbstractXspLibrary{
	 
	public static final String PLUGIN_ID="com.tc.utils";
	public static final String LIB_ID="com.tc.utils.library";

	@Override
	public String getLibraryId() {
		return LIB_ID;
	}
   
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

}

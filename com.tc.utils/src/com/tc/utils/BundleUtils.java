package com.tc.utils;

import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class BundleUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> T load(String bundleName, String className){
		Bundle bundle = Platform.getBundle(bundleName);
		T t = null;
		try {
			t = (T) bundle.loadClass(className).newInstance();
			if(t==null){
				throw new UnsupportedOperationException("instantiated class cannot be null " + className);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	@SuppressWarnings("rawtypes")
	public static String getVersion(Bundle bundle){
		Dictionary dict = bundle.getHeaders();
		return String.valueOf(dict.get(Constants.BUNDLE_VERSION));
	}
	
	@SuppressWarnings("rawtypes")
	public static String getName(Bundle bundle){
		Dictionary dict = bundle.getHeaders();
		return String.valueOf(dict.get(Constants.BUNDLE_NAME));
	}
	

}

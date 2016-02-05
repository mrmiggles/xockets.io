package com.tc.utils;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

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

}

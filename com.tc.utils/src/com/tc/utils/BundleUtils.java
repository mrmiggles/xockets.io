package com.tc.utils;

import java.lang.reflect.Constructor;
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
	
	public static <T> T loadWithArg(String bundleName, String className, Object arg){
		return loadWithArgs(bundleName, className, arg);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T loadWithArgs(String bundleName, String className, Object ...args){
		Bundle bundle = Platform.getBundle(bundleName);
		T t = null;
		try {
			
			Constructor[] cons = bundle.loadClass(className).getConstructors();
			
			for(int i =0;i<cons.length; i++){
				Constructor con = cons[i];
				if(con.getParameterTypes().length == args.length){
					t = (T) con.newInstance(args);
					break;
				}
			}
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

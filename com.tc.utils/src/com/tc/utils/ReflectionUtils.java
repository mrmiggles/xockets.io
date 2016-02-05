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

/**
 * @author Mark W Ambler
 * 
 */

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark Ambler
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ReflectionUtils {

 
	private static Map cache = new ConcurrentHashMap(100);
    private static Logger logger = Logger.getLogger(ReflectionUtils.class.getName());


    /**
     * Creates a new instance of ReflectUtils
     */
    public ReflectionUtils() {
    }

    public  Object invokeMethod(Method meth, Object o) {
        Object[] args = null;
        Object obj = null;
        
        try {
            obj = meth.invoke(o, args);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE,null,ex);
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE,null,ex);;
        }

        return obj;
    }

    public  Object invokeMethod(String getter, Object o) {
        Object[] args = null;
        Object obj = null;
        Method meth = getMethod(getter, o);
        try {
            obj = meth.invoke(o, args);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE,null,ex);;
        }

        return obj;
    }
    
    public  void invokeSetter(Method meth, Object o, Object value) {
    	
    	if(meth==null || o==null || value==null){
    		return;
    	}
    	
    	
        Object[] args = new Object[1];
        Class[] cls = new Class[1];
        cls[0] = value.getClass();

        

        try {
            args[0] = value;
            meth.invoke(o, args);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (IllegalAccessException ex) {
           logger.log(Level.SEVERE,null,ex);;
        }
    }

    public  void invokeSetter(String setter, Object o, Object value) {
    	
    	if(o==null || value==null){
    		return;
    	}
    	
        Object[] args = new Object[1];
        Class[] cls = new Class[1];
        cls[0] = value.getClass();

        Method meth = getMethod(setter, o);

        try {
        	if(meth==null){
        		return;
        	}
        	
            args[0] = value;
            meth.invoke(o, args);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE,null,ex);;
        } catch (IllegalAccessException ex) {
           logger.log(Level.SEVERE,null,ex);;
        }
    }

    public  Method getMethod(String methName, Object o) {
        Method meth = null;
        String key = o.getClass().getName() + "." + methName;

        if (cache.containsKey(key)) {
            meth = (Method) cache.get(key);
        }

        try {
            if (meth == null) {
                meth = findMethod(methName, o);
                cache.put(key, meth);
            }
        } catch (SecurityException ex) {
           logger.log(Level.SEVERE,null,ex);;
        }
        
        return meth;
    }

    public  Method findMethod(String method, Object o) {
        Method[] methods = o.getClass().getMethods();
        Method meth = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase(method)) {
                meth = methods[i];
                break;
            }
        }
        return meth;
    }
    
    
    public Annotation getMethodAnnotation(String methodName,Object o, Class annotationClass){
        Method method = this.findMethod(methodName, o);
        Annotation a = null;
        if(method.isAnnotationPresent(annotationClass)){
            a = method.getAnnotation(annotationClass);
        }
        return a;
    }
    
    
    public Annotation getMethodAnnotation(Method method, Class annotationClass){
        Annotation a = null;
        if(method.isAnnotationPresent(annotationClass)){
            a = method.getAnnotation(annotationClass);
        }
        return a;
    }

    public Annotation getClassAnnotation(Object o, Class annotationClass){
        Annotation a = o.getClass().getAnnotation(annotationClass);
        return a;
    }
    
    public Method[] getMethods(Object o){
        return o.getClass().getMethods();
    }
    
    
    public Method[] getDeclaredMethods(Object o){
    	return o.getClass().getDeclaredMethods();
    }
    
    public Field[] getDeclaredFields(Object o){
    	return o.getClass().getDeclaredFields();
    }
    
    public PropertyDescriptor[] getProperties(Object bean){
    	PropertyDescriptor[] descriptors=null;
    	try {
    		descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
    	} catch (IntrospectionException e) {
    		logger.log(Level.SEVERE,null,e);
    	}

    	return descriptors;
    }
    
    public boolean hasAnnotation(Object o, Class clazz){
    	return o.getClass().isAnnotationPresent(clazz);
    }
    
}

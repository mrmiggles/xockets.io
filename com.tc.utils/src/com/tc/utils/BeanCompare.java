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
 */

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings({"unchecked","rawtypes"})
public class BeanCompare implements Comparator<Object> {
    protected String getter;
    protected boolean desc;

    private ReflectionUtils reflection=new ReflectionUtils();

    private static final Logger logger = Logger.getLogger(BeanCompare.class.getName());
    

    
    
    public void setCompareAttribute(String getter){
        this.getter = getter;
    }
    
    /**
     * Allows caller to over-ride the default ascending sort by setting value to true
     */
    public void sortDescending(boolean b){
        this.desc = b;
    }
    
    @Override
	public int compare(Object o1, Object o2){
       
		Comparable data1=null;
        Comparable data2=null;
        int comp=0;

        try{
            data1 =(Comparable) reflection.invokeMethod(getter,o1);
            data2 =(Comparable) reflection.invokeMethod(getter,o2);
        }catch(java.lang.ClassCastException e){
        	logger.log(Level.SEVERE,null,e);
        }
            
        
        if(data1==null || data2==null){
        	return comp;
        }
        
           if (desc){
               comp= data2.compareTo(data1);
           }else{
              comp=data1.compareTo(data2);
           }
           
           
           return comp;
    }    
}


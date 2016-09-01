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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;



public class OrderedProperties extends LinkedHashMap<String, String>{
    /**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger(OrderedProperties.class.getName());
	
	private static final long serialVersionUID = 3671354594921382177L;
	private static final char keyValueSeparator='=';
	
    
    public void read(Reader in){
    	try{
       BufferedReader input=(BufferedReader)in;
       
       String line=null;
       line=input.readLine();
       while(line!=null){
           int pos=line.indexOf(keyValueSeparator);
           if(pos>=0){
             super.put(line.substring(0,pos),line.substring(pos+1));
          }
           line=input.readLine();
       }
       if(input!=in){
       		input.close();
		} 
    	}catch(java.io.IOException e){
    		logger.log(Level.SEVERE,null, e);
    	}
    }
    
    
    public void read(InputStream in){
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    	this.read(reader);
    }
    

}



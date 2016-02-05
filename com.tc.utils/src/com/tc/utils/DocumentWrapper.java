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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Form;
import lotus.domino.Item;
import lotus.domino.Session;


@SuppressWarnings("unchecked")
public class DocumentWrapper extends LinkedHashMap<Object, Object> {

	public static final Logger logger = Logger.getLogger(DocumentWrapper.class.getName());

	private static final long serialVersionUID = 7965789228726255021L;

	//private String unid;

	public DocumentWrapper(){
		//no arg constructor so we can use for writing.
	}

	public DocumentWrapper (Document doc){
		this.readDocument(doc);
	}

	public void readDocument (Document doc){
		Vector<Item> items;
		try {
			items = doc.getItems();
			Form form = doc.getParentDatabase().getForm(doc.getItemValueString("Form"));
			Vector<String> fields = form.getFields();

			for(Item item : items){
				String fieldName = this.resolveFieldName(item.getName(), fields);
				this.processItem(item, fieldName);
			}

			//make sure we add the UNID
			this.put("unid", doc.getUniversalID());
			

		} catch (Exception e) {
			logger.log(Level.SEVERE,null, e);
		}
	}


	private void processItem(Item item, String fieldName){
		Vector<?> values = null;

		try{
			
			//get out if item.type is unknown
			if("$FILE".equals(item.getName())){
				return;
			}
			
			values = item.getValues();
			
			String check = item.getValueString();
			if(check==null || "".equals(check) || fieldName.toLowerCase().contains("password")){
				return;
			}

			if(values!=null && !values.isEmpty()){
	
				if (values.size()==1){
					Object value =values.elementAt(0);
					if(value instanceof DateTime){
						DateTime dateTime = (DateTime) value;
						this.put(fieldName,dateTime.toJavaDate());
						
					}else if(value instanceof String){
						String data = (String) value;
						this.put(fieldName, data.trim());
						
					}else{
						this.put(fieldName, values.elementAt(0));
						
					}
				}else if(values.size() > 1){
					//if the values are of date time we need to convert.
					if(values.elementAt(0) instanceof DateTime){
						List<Date> list = this.convertToDateList(values);
						this.put(fieldName,list);
					}else{
						this.put(fieldName,values);
					}
				}
			}
		}catch(Exception n){
			logger.log(Level.SEVERE,"DocumentWrapper.field." + fieldName + " exception");
			logger.log(Level.SEVERE,null, n);
		}
	}

	private List<Date> convertToDateList(Vector<?> vector){
		List<Date> list = new ArrayList<Date> (vector.size());

		try{
			for(Object o : vector){
				DateTime dateTime = (DateTime) o;
				list.add(dateTime.toJavaDate());
			}
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}
		return list;
	}


	public void writeDocument(Document doc, Map<?, ?> map){
		
		try{
			Session session = doc.getParentDatabase().getParent();
			Form form = doc.getParentDatabase().getForm(doc.getItemValueString("Form"));
			Vector<String> fields = form.getFields();
			String fieldName=null;
			Object value=null;


			for(Object key : map.keySet()){
				//its case sensitive...
				fieldName = this.resolveFieldName(String.valueOf(key),fields);

				if(!fieldName.contains("%") && !fieldName.contains("$")){
					value = map.get(key);

					int fieldType = form.getFieldType(fieldName);

					if(fieldType==Item.TEXT){
						doc.replaceItemValue(fieldName, value);

					}else if(fieldType==Item.NUMBERS){
						double d = Double.parseDouble(String.valueOf(value));
						doc.replaceItemValue(fieldName, d);

					}else if(fieldType==Item.DATETIMES){
						
						if(value==null || String.valueOf(value).trim().equals("")){
							value = "";
							doc.replaceItemValue(fieldName, value);
						}else{
							DateTime dt = session.createDateTime(String.valueOf(value));
							doc.replaceItemValue(fieldName, dt);	
						}

					}else if(fieldType==Item.AUTHORS){
						Item item = doc.replaceItemValue(fieldName, value);
						item.setAuthors(true);

					}else if(fieldType==Item.READERS){
						Item item = doc.replaceItemValue(fieldName, value);
						item.setReaders(true);

					}else{ //catch all
						doc.replaceItemValue(fieldName, value);
					}
				}else{
					System.out.println("fieldName " + fieldName + " tried to store value " + value);
				}
			}

			doc.save();

		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}



	}//end method

	//make sure we get the right fieldName using correct case or above code won't work.
	private String resolveFieldName(String fieldName, Vector<String> fields){

		for(String field : fields){
			if(fieldName.equalsIgnoreCase(field)){
				fieldName=field;
				break;
			}
		}
		return fieldName;
	}

}

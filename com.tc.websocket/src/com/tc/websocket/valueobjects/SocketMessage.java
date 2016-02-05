/*
 * © Copyright Tek Counsel LLC 2016
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


package com.tc.websocket.valueobjects;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class SocketMessage {
	
	private String id;
	private String from;
	private String to;
	protected String text;
	protected Map<String,Object> data = new HashMap<String,Object>();
	private Date date=new Date();
	private String string;
	private String json;//serialized form of this object.
	private boolean durable;
	private boolean persisted;

	public void setValid(boolean valid){
		//do nothing.
	}
	
	public boolean isValid(){
		return from!=null && to!=null && text!=null;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	@Override
	public String toString(){
		if(string==null){
			string = this.id + "." + this.from + "." + this.to + "." + this.data.size() +  "." + this.data.hashCode() + "." + text;
		}
		return string;
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		boolean b = false;
		if(o instanceof SocketMessage){
			b = o.toString().equals(this.toString());
		}
		return b;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
 
	@JsonSerialize(using = CustomJsonDateTime.class)
	public Date getDate() {
		return date;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setDate(Date date) {
		this.date = date;
	}


	
	

	@JsonIgnore
	public String getJson() {
		return json;
	}

	@JsonIgnore
	public void setJson(String json) {
		this.json = json;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public boolean isPersisted() {
		return persisted;
	}

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	protected void finalize() throws Throwable{
		this.data.clear();
		this.data = null;
	}
	

}

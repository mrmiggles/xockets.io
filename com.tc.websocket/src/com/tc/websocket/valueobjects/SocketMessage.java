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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.inject.Inject;
import com.tc.utils.JSONUtils;
import com.tc.websocket.server.IDominoWebSocketServer;


// TODO: Auto-generated Javadoc
/**
 * The Class SocketMessage.
 */
public class SocketMessage {
	
	/** The id. */
	private String id;
	
	/** The from. */
	private String from;
	
	/** The to. */
	private String to;
	
	/** The targets. */
	private Collection<String> targets= new ArrayList<String>();
	
	/** The text. */
	protected String text;
	
	/** The data. */
	protected Map<String,Object> data = new HashMap<String,Object>();
	
	/** The date. */
	private Date date=new Date();
	
	/** The string. */
	private String string;
	
	/** The durable. */
	private boolean durable;
	
	/** The persisted. */
	private boolean persisted;
	
	private boolean shortCircuit;
	
	private boolean onlyText;
	
	private boolean onlyData;








	/** The server. */
	@Inject
	private IDominoWebSocketServer server;

	/**
	 * Sets the valid.
	 *
	 * @param valid the new valid
	 */
	public void setValid(boolean valid){
		//do nothing.
	}
	
	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid(){
		return from!=null && to!=null && (text!=null || !data.isEmpty());
	}
	
	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}
	
	/**
	 * Sets the from.
	 *
	 * @param from the new from
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	
	/**
	 * Sets the to.
	 *
	 * @param to the new to
	 */
	public void setTo(String to) {
		this.to = to;
	}
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Map<String, Object> getData() {
		return data;
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the data
	 */
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(string==null){
			string = this.id + "." + this.from + "." + this.to + "." + this.data.size() +  "." + this.data.hashCode() + "." + text;
		}
		return string;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		boolean b = false;
		if(o instanceof SocketMessage){
			b = o.toString().equals(this.toString());
		}
		return b;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
	}
 
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	@JsonSerialize(using = CustomJsonDateTime.class)
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setDate(Date date) {
		this.date = date;
	}


	
	/**
	 * To json.
	 *
	 * @return the string
	 */
	@JsonIgnore
	public String toJson(){
		return JSONUtils.toJson(this);
	}


	/**
	 * Checks if is durable.
	 *
	 * @return true, if is durable
	 */
	public boolean isDurable() {
		return durable;
	}

	/**
	 * Sets the durable.
	 *
	 * @param durable the new durable
	 */
	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	/**
	 * Checks if is persisted.
	 *
	 * @return true, if is persisted
	 */
	public boolean isPersisted() {
		return persisted;
	}

	/**
	 * Sets the persisted.
	 *
	 * @param persisted the new persisted
	 */
	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable{
		this.data.clear();
		this.data = null;
	}
	
	
	/**
	 * To.
	 *
	 * @param to the to
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage to(String to){
		this.setTo(to);
		return this;
	}
	
	/**
	 * From.
	 *
	 * @param from the from
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage from(String from){
		this.setFrom(from);
		return this;
	}
	
	/**
	 * Durable.
	 *
	 * @param durable the durable
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage durable(boolean durable){
		this.setDurable(durable);
		return this;
	}
	
	/**
	 * Text.
	 *
	 * @param text the text
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage text(String text){
		this.setText(text);
		return this;
	}
	
	@JsonIgnore
	public SocketMessage id(String id){
		this.setId(id);
		return this;
	}
	
	/**
	 * Data.
	 *
	 * @param data the data
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage data(Map<String,Object> data){
		this.setData(data);
		return this;
	}
	
	/**
	 * Persisted.
	 *
	 * @param p the p
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage persisted(boolean p){
		this.setPersisted(p);
		return this;
	}
	
	/**
	 * Date.
	 *
	 * @param date the date
	 * @return the socket message
	 */
	@JsonIgnore
	public SocketMessage date(Date date){
		this.setDate(date);
		return this;
	}

	/**
	 * Gets the targets.
	 *
	 * @return the targets
	 */
	public Collection<String> getTargets() {
		return targets;
	}

	/**
	 * Sets the targets.
	 *
	 * @param targets the new targets
	 */
	public void setTargets(Collection<String> targets) {
		this.targets.clear();
		this.targets.addAll(targets);
	}
	
	/**
	 * Checks for multiple targets.
	 *
	 * @return true, if successful
	 */
	@JsonIgnore
	public boolean hasMultipleTargets(){
		return this.targets!=null && this.targets.isEmpty() == false;
	}

	/**
	 * Adds the target.
	 *
	 * @param target the target
	 */
	public void addTarget(String target){
		if(this.targets!=null && target!=null && !this.targets.contains(target)){
			this.targets.add(target);
		}
	}

	
	/**
	 * Send.
	 */
	public void send(){
		server.onMessage(this.getTo(),this.toJson());
	}
	
	
	public boolean isShortCircuit() {
		return shortCircuit;
	}

	public void setShortCircuit(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

	public boolean isOnlyText() {
		return onlyText;
	}

	public void setOnlyText(boolean onlyText) {
		this.onlyText = onlyText;
	}
	
	public boolean isOnlyData() {
		return onlyData;
	}

	public void setOnlyData(boolean onlyData) {
		this.onlyData = onlyData;
	}
	
	
}

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import org.apache.commons.io.IOUtils;

import com.ibm.domino.osgi.core.context.ContextInfo;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.model.domino.DominoUtils;

/**
 * @author Mark W Ambler
 *
 */
@SuppressWarnings("unchecked")
public class XSPUtils {

	private static final Logger logger = Logger.getLogger(XSPUtils.class.getName());

	public static void nullifyFacesObject(String objectName){
		try{
			String facesName="#{" + objectName + "}";
			Application app = FacesContext.getCurrentInstance().getApplication();
			app.createValueBinding(facesName).setValue(FacesContext.getCurrentInstance(),null);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(objectName, null);
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}
	}

	public static Object getBean(String objectName){
		return getFacesObject(objectName);
	}
	
	public static String uuid(){
		return UUID.randomUUID().toString();
	}


	public static Object getValue(String objectName){
		return getFacesObject(objectName);
	}

	public static Object getFacesObject(String objectName){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String facesName="#{" + objectName + "}";
		Object obj=facesContext.getApplication().createValueBinding(facesName).getValue(facesContext);
		return obj;
	}

	public static void setFacesObject(String objectName,Object obj){
		try{
			String facesName="#{" + objectName + "}";
			Application app = FacesContext.getCurrentInstance().getApplication();

			//remove
			XSPUtils.nullifyFacesObject(objectName);

			//reset.
			app.createValueBinding(facesName).setValue(FacesContext.getCurrentInstance(),obj);
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}
	}

	public static HttpServletRequest getRequest(){
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	public static HttpServletResponse getResponse(){
		return (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}
	
	public static String getParam(String key){
		return getRequest().getParameter(key);
	}



	public static void generateError(String errMsg){
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,errMsg,null));
	}

	public static void clearMessages(){
		Iterator<?> i = FacesContext.getCurrentInstance().getMessages();
		while(i.hasNext()){
			FacesMessage msg = (FacesMessage) i.next();
			msg.setDetail(null);
			msg.setSummary(null);
			msg=null;
		}
	}


	public static UIComponent findFirstInstance(Class<?> cls) {
		UIComponent component = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			UIComponent root = facesContext.getViewRoot();
			component = findFirstInstance(root, cls);
		}

		return component;
	}

	public static UIComponent findFirstInstance(UIComponent base, Class<?> cls) {
		if (cls.getName().equals(base.getClass().getName())){
			return base;
		}
		UIComponent kid = null;
		UIComponent result = null;
		Iterator<?> kids = base.getFacetsAndChildren();
		while (kids.hasNext() && (result == null)) {
			kid = (UIComponent) kids.next();
			if (cls.getName().equals(kid.getClass().getName())) {
				result = kid;
				break;
			}
			result = findFirstInstance(kid, cls);
			if (result != null) {
				break;
			}
		}
		return result;
	}


	public static List<String> findSelectedDocuments(String id){
		List<String> list = new ArrayList<String>();
		UIComponent c = XSPUtils.findComponent(id);
		List<UIComponent> viewPanels = new ArrayList<UIComponent>();
		XSPUtils.findAllInstances(c, XspViewPanel.class, viewPanels);

		for(Object viewPanel : viewPanels){
			XspViewPanel p = (XspViewPanel) viewPanel;
			for(String strid : p.getSelectedIds()){
				list.add(strid);
			}
		}
		return list;
	}

	public static void findAllInstances(UIComponent base, Class<?> cls, List<UIComponent> list) {
		for(Object o : base.getChildren()){
			UIComponent c = (UIComponent) o;
			if(c.getClass().getName().equals(cls.getName())){
				list.add(c);

			}else{
				findAllInstances(c, cls, list);
			}
		}
	}
	
	
	public static UIComponent findComponent(String id) {
		return FacesContext.getCurrentInstance().getViewRoot().findComponent(id);
	}

	public static Locale getLocale(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return locale;
	}

	public static Application app(){
		return FacesContext.getCurrentInstance().getApplication();
	}

	public static UIViewRoot getViewRoot(){
		return FacesContext.getCurrentInstance().getViewRoot();
	}

	public static String webPath(){
		String filePath = null;
		try {
			filePath = DominoUtils.getCurrentDatabase().getFilePath();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
		return StringCache.FORWARD_SLASH + filePath.replace(StringCache.BACK_SLASH, StringCache.FORWARD_SLASH);

	}


	public static String getURL() {

		HttpServletRequest req = XSPUtils.getRequest();
		String scheme = req.getScheme();             // http
		String serverName = req.getServerName();     // hostname.com
		int serverPort = req.getServerPort();        // 80
		String contextPath = req.getContextPath();   // /mywebapp
		String servletPath = req.getServletPath();   // /servlet/MyServlet
		String pathInfo = req.getPathInfo();         // /a/b;c=123
		String queryString = req.getQueryString();          // d=789

		// Reconstruct original requesting URL
		StringBuffer url =  new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath).append(servletPath);

		if (pathInfo != null) {
			url.append(pathInfo);
		}
		if (queryString != null) {
			url.append("?").append(queryString);
		}
		return url.toString();
	}


	public static String getAppURL() {
		HttpServletRequest req = XSPUtils.getRequest();
		String scheme = req.getScheme(); 
		StringBuffer url =  new StringBuffer();
		url.append(scheme).append("://").append(XSPUtils.hostName()).append('/').append(XSPUtils.webPath());
		return url.toString();
	}




	public static String computeClientSideId(UIComponent c){
		String expression = "#{id:" + c.getId() + "}";
		ValueBinding vb = XSPUtils.app().createValueBinding(expression);
		String clientId = (String) vb.getValue(FacesContext.getCurrentInstance());
		return clientId;
	}


	public static InputStream getResource(String resourceName){
		InputStream inStream = context().getExternalContext().getResourceAsStream(resourceName);
		return inStream;
	}

	public static byte[] getResourceBytes(String resourceName){
		InputStream in = context().getExternalContext().getResourceAsStream(resourceName);
		byte[] byteMe=null;
		try {
			byteMe = IOUtils.toByteArray(in);
		} catch (IOException e) {
			logger.log(Level.SEVERE,null, e);
		}finally{
			IOUtils.closeQuietly(in);
		}
		return byteMe;
	}

	public static FacesContext context(){
		return FacesContext.getCurrentInstance();
	}


	public static Session sessionAsSigner(){
		return (Session) XSPUtils.getFacesObject("sessionAsSigner");
	}

	public static Session createTrustedSession(){
		NotesThread.sinitThread();
		Session s=null;
		try {
			s = NotesFactory.createTrustedSession();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
		return s;
	}


	public static String hostName(){
		HttpServletRequest req= (HttpServletRequest) XSPUtils.context().getExternalContext().getRequest();
		return req.getServerName().toLowerCase();
	}

	public static String userName(){
		HttpServletRequest req= (HttpServletRequest) XSPUtils.context().getExternalContext().getRequest();
		return req.getRemoteUser();
	}

	public static void gotoPage(String xpage){
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(xpage);
		} catch (IOException e) {
			logger.log(Level.SEVERE,null, e);
		}
		return;
	}

	public static void goHome(){
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(XSPUtils.webPath());
		} catch (IOException e) {
			logger.log(Level.SEVERE,null, e);
		}
		return;
	}

	public static boolean isGet(){
		HttpServletRequest req = XSPUtils.getRequest();
		return "GET".equals(req.getMethod());
	}

	public static boolean isPost(){
		HttpServletRequest req = XSPUtils.getRequest();
		return "POST".equals(req.getMethod());
	}

	public static void throwValidatorException(String message){
		FacesMessage msg = new FacesMessage();
		msg.setDetail(message);
		msg.setSummary(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(msg);
	}

	public static void addMessage(String message, Severity severity){
		FacesMessage msg = new FacesMessage();
		msg.setDetail(message);
		msg.setSummary(message);
		msg.setSeverity(severity);
		XSPUtils.context().addMessage(null, msg);
	}

	public static void addMessage(String message){
		FacesMessage msg = new FacesMessage();
		msg.setDetail(message);
		msg.setSummary(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		XSPUtils.context().addMessage(null, msg);
	}

	public static void addMessageToSession(String key, String message){
		FacesMessage msg = new FacesMessage();
		msg.setDetail(message);
		msg.setSummary(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		XSPUtils.sessionScope().put(key, message);

	}

	public static Map<?, ?> requestScope(){
		return XSPUtils.context().getExternalContext().getRequestMap();
	}

	@SuppressWarnings("rawtypes")
	public static Map sessionScope(){
		return XSPUtils.context().getExternalContext().getSessionMap();
	}

	@SuppressWarnings("rawtypes")
	public static Map viewScope(){
		return XSPUtils.context().getViewRoot().getViewMap();
	}

	@SuppressWarnings("rawtypes")
	public static Map appScope(){
		return XSPUtils.context().getExternalContext().getApplicationMap();
	}

	public static String buildXpageUrl(HttpServletRequest request,String xpage, Document doc) throws NotesException{
		final StringBuilder url = new StringBuilder(48); 

		String webPath  = doc.getParentDatabase().getFilePath().replace("\\", "/");

		String scheme = request.getScheme(); 

		url.append(scheme); 
		url.append("://"); 
		url.append(request.getServerName()); 

		int port = request.getServerPort(); 
		if (port > 0 && 
				(("http".equalsIgnoreCase(scheme) && port != 80) || 
						("https".equalsIgnoreCase(scheme) && port != 443))) { 
			url.append(':'); 
			url.append(port); 
		} 

		url.append(webPath); 
		url.append('/');
		url.append(xpage);
		url.append("?documentId=" + doc.getUniversalID());
		url.append("&action=openDocument");

		return url.toString();
	}



	public static String buildHostAndWebPath(FacesContext context, Database db) throws NotesException{
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		final StringBuilder url = new StringBuilder(48); 
		String webPath  = db.getFilePath().replace("\\", "/");

		String scheme = request.getScheme(); 

		url.append(scheme); 
		url.append("://"); 
		url.append(request.getServerName()); 

		int port = request.getServerPort(); 
		if (port > 0 && 
				(("http".equalsIgnoreCase(scheme) && port != 80) || 
						("https".equalsIgnoreCase(scheme) && port != 443))) { 
			url.append(':'); 
			url.append(port); 
		} 

		url.append('/');
		url.append(webPath);
		return url.toString();
	}


	public static Map<String, String> getQueryMap(String query){  
		String[] params = query.split("&");  
		Map<String, String> map = new HashMap<String, String>();  
		for (String param : params){ 
			String[] arr = param.split("=");
			if(arr.length==2){
				String name = arr[0];  
				String value =arr[1];  
				map.put(name, value);
			}
		}  
		return map;  
	}


	public static String buildFileShortName(String fileName){
		String[] arr = fileName.split("\\.");
		String name = arr[0];
		String ext = arr[1];
		name = name.replaceAll("[0-9]*", "");
		return name + "." + ext;
	}


	public static Cookie[] getCookies(){
		return XSPUtils.getRequest().getCookies();
	}

	public static String getCookie(String name){
		String value = "";
		for(Cookie cookie : getCookies()){
			if(cookie.getName().equals(name)){
				value = cookie.getValue();
				break;
			}
		}
		return value;
	}

	public static HttpSession getHttpSession(){
		HttpServletRequest req = XSPUtils.getRequest();
		if(req==null){
			return null;
		}
		return XSPUtils.getRequest().getSession();
	}

	public static ExternalContext externalContext(){
		return XSPUtils.context().getExternalContext();
	}

	public static Properties loadProps(Document doc, String fieldName){
		Properties props=new Properties();
		InputStream in = null;
		Item item = null;
		try{
			
			if(doc==null) return props;
			
			//make sure the field is multi-value, 
			//and uses new lines between each value.
			item = doc.getFirstItem(fieldName);
			if(item==null){
				return props;
			}
			String data = item.getText().replace(';','\n');
			in = new ByteArrayInputStream(data.getBytes());
			props.load(in);
			item.recycle();

		}catch(Exception e){
			//replace with your own logger.
			logger.log(Level.SEVERE,null,e);

		}finally{
			IOUtils.closeQuietly(in);
		}
		return props;
	}
	

	public static void logout(){    
		HttpSession httpSession = XSPUtils.getHttpSession();

		if(httpSession==null){
			return;
		}

		String sessionId = XSPUtils.getHttpSession().getId();
		String url = XSPUtils.externalContext().getRequestContextPath() + "?logout&redirectto=" + externalContext().getRequestContextPath();
		XSPUtils.getRequest().getSession(false).invalidate();

		//wipe out the cookies
		for(Cookie cookie : getCookies()){
			cookie.setValue(StringCache.EMPTY);
			cookie.setPath("/");
			cookie.setMaxAge(0);
			XSPUtils.getResponse().addCookie(cookie);
		}

		try {
			NotesContext notesContext = NotesContext.getCurrent();
			notesContext.getModule().removeSession(sessionId);
			XSPUtils.externalContext().redirect(url);
		} catch (IOException e) {
			logger.log(Level.SEVERE,null,e);
		}
	}
	
	public static void logout(String url){    
		HttpSession httpSession = XSPUtils.getHttpSession();

		if(httpSession==null){
			return;
		}

		String sessionId = XSPUtils.getHttpSession().getId();
		XSPUtils.getRequest().getSession(false).invalidate();

		//wipe out the cookies
		for(Cookie cookie : getCookies()){
			cookie.setValue(StringCache.EMPTY);
			cookie.setPath("/");
			cookie.setMaxAge(0);
			XSPUtils.getResponse().addCookie(cookie);
		}

		try {
			NotesContext notesContext = NotesContext.getCurrent();
			notesContext.getModule().removeSession(sessionId);
			XSPUtils.externalContext().redirect(url);
		} catch (IOException e) {
			logger.log(Level.SEVERE,null,e);
		}
	}
	
	
	/*
	 * too many ways to get a handle on session...
	 * and not all of them work in the same context
	 */
	public static final Session session(){
		Session session = ContextInfo.getUserSession();
		if(session == null){
			session = NotesContext.getCurrent().getCurrentSession();
			
			if (session == null){
				session = DominoUtils.getCurrentSession();
				
				if(session== null){
					throw new RuntimeException("cannot get handle on domino session!");
				}
			}
			
		}
		return session;
	}
	
	
	public static final Database database(){
		Database db = ContextInfo.getUserDatabase();
		if(db==null){
			db = NotesContext.getCurrent().getCurrentDatabase();
			if(db == null){
				db = DominoUtils.getCurrentDatabase();
			}
		}
		return db;
	}
	
	
	
}

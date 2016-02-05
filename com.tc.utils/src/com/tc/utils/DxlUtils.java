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
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DxlExporter;
import lotus.domino.NoteCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class DxlUtils {

	private static final Logger logger = Logger.getLogger(DxlUtils.class.getName());


	private static final String FILE_DATA_START="<filedata>";
	private static final String FILE_DATA_END = "</filedata>";

	public static final String SSJS_LIB_START="<rawitemdata type='1'>";
	public static final String SSJS_LIB_END="</rawitemdata>";
	public static final int SSJS_HEADER_SIZE=50;

	private static Map<String,Object> simpleCache = new ConcurrentHashMap<String,Object>();


	public static Properties loadProperties(Database db, String properties){
		Properties props = (Properties) simpleCache.get(properties);
		if(props!=null){
			return props;
		}
		props = new Properties();
		byte[] byteMe = findFileResource(db, properties);

		if(byteMe == null){
			return props;
		}

		InputStream in = new ByteArrayInputStream(byteMe);
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE,null, e);
		}
		simpleCache.put(properties, props); //cache it until http restart.
		return props;
	}

	public static byte[] findFileResource(Database db, String resourceName){
		Document docLib=null;
		byte[] byteMe = null;
		NoteCollection nc=null;
		try {

			if(db==null || resourceName==null) {
				return null;
			}

			nc = db.createNoteCollection(false);
			nc.setSelectDocuments(false);
			nc.setSelectScriptLibraries(false);
			nc.setSelectMiscFormatElements(true);

			nc.buildCollection();
			docLib= null;

			//now lets find the lib that matches the name
			String noteId = nc.getFirstNoteID();
			while(!"".equals(noteId)){
				docLib = db.getDocumentByID(noteId);
				if(docLib!=null && resourceName.equals(docLib.getItemValueString("$Title"))){
					byteMe = extractData(docLib,FILE_DATA_START,FILE_DATA_END);
					break;
				}

				noteId = nc.getNextNoteID(noteId);
				if(docLib!=null){
					docLib.recycle();
				}
			}

		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}finally{
			try {
				nc.recycle();
			} catch (NotesException e) {
				logger.log(Level.SEVERE,null, e);
			}
		}
		return byteMe;
	}



	public static byte[] findSSJS(Database db, String resourceName){
		Document docLib=null;
		byte[] byteMe = null;
		NoteCollection nc=null;
		try {

			if(db==null || resourceName==null) {
				return null;
			}

			nc = db.createNoteCollection(false);
			nc.setSelectDocuments(false);
			nc.selectAllCodeElements(true);
			nc.buildCollection();

			
			docLib= null;

			//now lets find the lib that matches the name
			String noteId = nc.getFirstNoteID();
			while(!"".equals(noteId)){
				docLib = db.getDocumentByID(noteId);
				if(docLib!=null && resourceName.equals(docLib.getItemValueString("$Title"))){
					byteMe = extractData(docLib,SSJS_LIB_START,SSJS_LIB_END);

					//now lets drop the first 50 bytes, and the last byte (seems to have an illegal character in there).
					byteMe = Arrays.copyOfRange(byteMe, 50, byteMe.length - 1);

					break;
				}

				noteId = nc.getNextNoteID(noteId);
				if(docLib!=null){
					docLib.recycle();
				}
			}

		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}finally{
			try {
				nc.recycle();
			} catch (NotesException e) {
				logger.log(Level.SEVERE,null, e);
			}
		}
		return byteMe;
	}


	public static byte[] extractData(Document docLib, String start, String end){
		byte[] byteMe = null;
		try {
			logger.log(Level.FINE,"retrieving docLib " + docLib.getItemValueString("$Title"));
			Session s = docLib.getParentDatabase().getParent();
			DxlExporter exporter = s.createDxlExporter();
			String dxl = exporter.exportDxl(docLib);
			dxl = dxl.replaceAll("\n", "");
			String base64 = StrUtils.middle(dxl,start,end);
			byteMe = Base64.decode(base64);
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		} catch (Exception e) {
			logger.log(Level.SEVERE,null, e);
		}
		return byteMe;
	}

}

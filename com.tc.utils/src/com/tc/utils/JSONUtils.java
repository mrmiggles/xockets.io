package com.tc.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;


public class JSONUtils {
	public static final ObjectMapper mapper = createObjectMapper();
	private static final Logger logger = Logger.getLogger(JSONUtils.class.getName());


	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		mapper.setDateFormat(df);
		return mapper;
	}
	
	
	public static void bind(Object o, String json){
		try{
			mapper.readerForUpdating(o).readValue(json);
		}catch(Exception e){
			logger.log(Level.SEVERE, null, e);
		}
	}
	
	public static File write(Object o){
		File file=null;
		try {
			file = File.createTempFile("msg", ".json");
			mapper.writeValue(file, o);
			
		} catch (IOException e) {
			logger.log(Level.SEVERE,null, e);
		}
		return file;
	}
	
	public static <T> T toObject(String json, Class<T> clz){
		T t = null;
		
		try {
			t = mapper.readValue(json, clz);
		} catch (JsonParseException e) {
			logger.log(Level.SEVERE,null,e);
			logger.log(Level.SEVERE,json);
		} catch (JsonMappingException e) {
			logger.log(Level.SEVERE,null,e);
			logger.log(Level.SEVERE,json);
		} catch (IOException e) {
			logger.log(Level.SEVERE,null,e);
			logger.log(Level.SEVERE,json);
		}
		
		
		return t;
	}
	
	public static String toJson(Object o){
		String json = null;
		try {
			json= mapper.writeValueAsString(o);
		} catch (JsonGenerationException e) {
			logger.log(Level.SEVERE,null,e);
		} catch (JsonMappingException e) {
			logger.log(Level.SEVERE,null,e);
		} catch (IOException e) {
			logger.log(Level.SEVERE,null,e);
		}
		return json;
	}

	public static Map<String,Object> toMap(String json) throws JsonParseException, JsonMappingException, IOException{
		HashMap<String, Object> map = mapper.readValue(json, (new TypeReference<HashMap<String,Object>>(){}));
		return map;
	}

	public static List<Map<String,Object>> toList(String json) throws JsonParseException, JsonMappingException, IOException{
		List<Map<String,Object>> list = mapper.readValue(json, (new TypeReference<List<HashMap<String,Object>>>(){}));;
		return list;
	}

	
	
	public static <T> List<T> toList(String json, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, cls);
		List<T> list = mapper.readValue(json,type);
		return list;
	}

	public static void main(String[] args){
		String json="[{\"billId\":\"billid3\",\"amount\":\"13\"}]";
		List<Map<String,Object>> list = null;
		try {
			list = JSONUtils.toList(json);

			for(Map<String,Object> map : list){
				for(String key : map.keySet()){
					System.out.println(key + "=" + map.get(key));
				}
			}

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(list);
	}

}

package com.tc.websocket.scripts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Set;

import lotus.domino.Database;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.tc.utils.DxlUtils;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.IConfig;

public class ScriptAggregator {
	
	private StringBuilder sb = new StringBuilder();
	private Set<String> dependencies = new LinkedHashSet<String>();
	private Database db;
	private static final String IMPORT_PREFIX = "///use ";
	private IConfig cfg = Config.getInstance();
	
	public ScriptAggregator(Database db){
		this.db = db;
	}
	
	public ScriptAggregator(){}
	
	public String build(String script) throws JsonParseException, JsonMappingException, IOException{
		
		//if no dependencies just return itself.
		if(!script.contains(IMPORT_PREFIX)){
			return script;
		}
		
		//add the initial script.
		sb.append(script);
		
		//build the set of dependencies.
		this.resolveDependencies(script);
		
		//with all the script files build one big script.
		for(String path : this.dependencies){
			sb.append(this.resolveScript(path));
			sb.append("\n\n");
		}
		
		return sb.toString();
	}
	
	private String resolveElement(String path){
		String resource = path.substring(path.lastIndexOf(StringCache.FORWARD_SLASH) + 1,path.length());
		return resource;
	}
	
	public String resolveScript(String source) throws UnsupportedEncodingException{
		
		if(source.contains(StringCache.DOT_NSF)){
			source = this.resolveElement(source);
		}
		
		String script = null;
		if(source.endsWith(".ssjs") || source.endsWith(".js")){
			script = new String(DxlUtils.findSSJS(db, source),cfg.getCharSet());
		}else{
			script = new String(DxlUtils.findFileResource(db, source),cfg.getCharSet());
		}
		return script;
	}
	
	public void resolveDependencies(String script) throws JsonParseException, JsonMappingException, IOException{
		String[] parsed = script.split("\n");
		for(String str : parsed){
			if(str.contains(IMPORT_PREFIX)){
				str = StrUtils.rightBack(str, IMPORT_PREFIX);
				String[] references = str.split(StringCache.COMMA);
				for(String path : references){
					path = path.trim();
					if(!dependencies.contains(path)){
						dependencies.add(path);
						String newScript = this.resolveScript(path);
						
						//recurse to pull in all the script files.
						this.resolveDependencies(newScript);
					}
				}
			}
		}
	}
	

}

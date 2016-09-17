package com.tc.websocket.scripts;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lotus.domino.Database;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.tc.utils.DxlUtils;
import com.tc.utils.JSONUtils;
import com.tc.utils.StringCache;

public class ScriptAggregator {
	
	private StringBuilder sb = new StringBuilder();
	private Set<String> dependencies = new LinkedHashSet<String>();
	private Database db;
	private static final String IMPORT_PREFIX = "///{\"import\":[";
	public ScriptAggregator(Database db){
		this.db = db;
	}
	
	public ScriptAggregator(){}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException{
		String fullScript = new ScriptAggregator().build("/blah");
		System.out.println(fullScript);
	}
	
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
	
	public String resolveScript(String source){
		
		if(source.contains(StringCache.DOT_NSF)){
			source = this.resolveElement(source);
		}
		
		String script = null;
		if(source.endsWith(".ssjs") || source.endsWith(".js")){
			script = new String(DxlUtils.findSSJS(db, source));
		}else{
			script = new String(DxlUtils.findFileResource(db, source));
		}
		return script;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void resolveDependencies(String script) throws JsonParseException, JsonMappingException, IOException{
		
		
		String[] parsed = script.split("\n");
		
		
		for(String str : parsed){
			if(str.indexOf("///{")!=-1){
				str = str.replace("///", "");
				Map map = JSONUtils.toMap(str);
				for(String path : (List<String>)map.get("import")){
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

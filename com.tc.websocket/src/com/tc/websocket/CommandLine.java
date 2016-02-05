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



package com.tc.websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.ColUtils;
import com.tc.utils.StrUtils;
import com.tc.websocket.embeded.clients.IScriptClient;
import com.tc.websocket.embeded.clients.IScriptClientRegistry;
import com.tc.websocket.embeded.clients.RhinoClient;
import com.tc.websocket.embeded.clients.Script;
import com.tc.websocket.queue.PurgeDocuments;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

public class CommandLine implements CommandProvider {
	
	private static final Logger logger = Logger.getLogger(CommandLine.class.getName());

	public void _websocket(final CommandInterpreter out) {
		try{
			final String command=out.nextArgument();
			StartAndStop startStop = StartAndStop.getInstance();


			IGuicer guicer = Guicer.getInstance(Activator.BUNDLE);
			IDominoWebSocketServer server = null;

			//check to see if guice has been initialized
			if(guicer!=null){
				server = Guicer.getInstance(Activator.BUNDLE).createObject(IDominoWebSocketServer.class);
			}
			
			if("start".equalsIgnoreCase(command) || "run".equalsIgnoreCase(command)){
				startStop.start();

			}else if("stop".equalsIgnoreCase(command) || "quit".equalsIgnoreCase(command)){
				startStop.stop();

			}else if("count".equalsIgnoreCase(command)){
				out.println(server.getWebSocketCount());

			}else if("count-all".equals(command)){
				out.println(server.getUsers().size());
			}
			
			else if("show-all-users".equalsIgnoreCase(command)){
				this.printUsers(server.getUsers(), out);

			}else if("show-users".equalsIgnoreCase(command)){
				this.printUsers(server.getUsersOnThisServer(), out);
				
			}else if ("gc".equalsIgnoreCase(command)){
				System.out.println("requesting garbage collection");
				System.gc();
				
			}else if("reload-scripts".equalsIgnoreCase(command)){
				this.reloadScripts(out);
				
			}else if("register-script".startsWith(command)){
				this.registerScript(out);
				
			}else if("show-scripts".startsWith(command)){
				this.showScripts(out);
				
			}else if("remove-script".startsWith(command)){
				this.removeScript(out);
				
			}else if("purge".equals(command)){
				this.purge(out);
				
			}else if("queue-count".equals(command)){
				this.showQueueCounts(server, out);
			}
			
			else{
				out.println(getHelp());
			}
		}catch(Exception e){
			logger.log(Level.SEVERE, null, e);
		}

	}
	
	
	
	private void showQueueCounts(IDominoWebSocketServer server, CommandInterpreter out){
			out.println("show queue counts no longer supported.");
	}
	
	private void showScripts(CommandInterpreter out){
		for(IScriptClient client : RhinoClient.getAllClients()){
			for(Script script : client.getScripts()){
				out.println("uri=" + client.getUser().getUri() + ", event=" + script.getEvent() + ", source=" + script.getSource());
			}
		}

	}
	

	private void removeScript(CommandInterpreter out){
		String path = out.nextArgument();
		for(IScriptClient client : RhinoClient.getAllClients()){
			client.removeScriptByPath(path);
		}
		
	}
	
	private void registerScript(CommandInterpreter out){
		
		String host = out.nextArgument();
		String uri = out.nextArgument();
		String event = out.nextArgument();
		String scriptPath = out.nextArgument();
		
		
		if(StrUtils.isEmpty(host)){
			out.println("host is empty");
		}
		
		
		if(StrUtils.isEmpty(uri)){
			out.println("uri is empty.");
			return;
		}
		
		if(StrUtils.isEmpty(event)){
			out.println("event is missing (i.e. onMessage, onOpen, onClose)");
			return;
		}
		
		if(StrUtils.isEmpty(scriptPath)){
			out.println("invalid path to script.");
			return;
		}
		
		
		//register the script
		IScriptClientRegistry reg = Guicer.getInstance(Activator.BUNDLE).createObject(IScriptClientRegistry.class);
		reg.registerScriptClient(host, uri, event, scriptPath);
		
	}

	private void reloadScripts(CommandInterpreter out){
		for(IScriptClient client : RhinoClient.getAllClients()){
			client.reloadScripts();
		}
		out.println("scripts have been reloaded / recompiled");
	}
	
	
	private void purge(CommandInterpreter out){
		PurgeDocuments purge = new PurgeDocuments();
		purge.run();
		out.println("purge complete.");
	}
	

	private void printUsers(Collection<IUser> col, CommandInterpreter out){

		List<IUser> list = new ArrayList<IUser>();
		list.addAll(col);
		ColUtils.sort(list, "getUserId", false);

		for(IUser user : list){
			out.println("userId=" + user.getUserId() + ", server=" + user.getHost() + ", connected=" + user.isOpen() + ", isGoingOffline=" + user.isGoingOffline());
		}

	}

	@Override
	public String getHelp() {
		return "---WebSocket Service Commands---\n"+
				"websocket start - starts the websocket server (push is automatically started on HTTP startup)\n" +
				"websocket stop - stops the websocket server\n" +
				"";
	}

}

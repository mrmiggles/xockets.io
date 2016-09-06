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

import java.text.NumberFormat;
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
import com.tc.websocket.runners.PurgeDocuments;
import com.tc.websocket.scripts.Script;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.structures.UriScriptMap;


// TODO: Auto-generated Javadoc
/**
 * The Class CommandLine.
 */
public class CommandLine implements CommandProvider {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CommandLine.class.getName());
	
	/**
	 * Xockets.
	 *
	 * @param out the out
	 */
	public void _xockets(final CommandInterpreter out){
		this._websocket(out);
	}
	
	/**
	 * Websocket.
	 *
	 * @param out the out
	 */
	public void _websocket(final CommandInterpreter out) {
		try{
			final String command=out.nextArgument();
			final StartAndStop startStop = StartAndStop.getInstance();


			final IGuicer guicer = Guicer.getInstance(Activator.bundle);
			IDominoWebSocketServer server = null;

			//check to see if guice has been initialized
			if(guicer!=null){
				server = Guicer.getInstance(Activator.bundle).createObject(IDominoWebSocketServer.class);
			}
			
			if("start".equalsIgnoreCase(command) || "run".equalsIgnoreCase(command)){
				startStop.start();

			}else if("stop".equalsIgnoreCase(command) || "quit".equalsIgnoreCase(command)){
				startStop.stop();

			}else if("count".equalsIgnoreCase(command)){
				out.println(server.getWebSocketCount());

			}else if("count-all".equals(command)){
				out.println(server.getUsers().size());
				
			}else if("show-engines".equals(command)){
				Script.printEngines();
			}
			
			else if("show-all-users".equalsIgnoreCase(command)){
				this.printUsers(server.getUsers(), out);

			}else if("show-users".equalsIgnoreCase(command)){
				this.printUsers(server.getUsersOnThisServer(), out);
				
			}else if("show-listeners".equalsIgnoreCase(command)){
				this.showListeners();
			}
			
			else if ("gc".equalsIgnoreCase(command)){
				System.out.println("requesting garbage collection");
				System.gc();
				
			}else if("show-scripts".equalsIgnoreCase(command)){
				this.showScripts(server, out);
				
			}else if("reload-scripts".equalsIgnoreCase(command)){
				this.reloadScripts(out, server);
				
			}else if("register-listener".startsWith(command)){
				this.registerListener(server, out);
				
			}else if("register-observer".startsWith(command)){
				this.registerObserver(server, out);
				
			}else if("register-intervaled".startsWith(command)){
				this.registerIntervaled(server, out);
				
			}
			
			else if("show-scripts".startsWith(command)){
				this.showScripts(server, out);
				
			}else if("remove-script".startsWith(command)){
				this.removeScript(server, out);
				
			}else if("purge".equals(command)){
				this.purge(out);
				
			}else if("queue-count".equals(command)){
				this.showQueueCounts(server, out);
				
			}else if ("memory".equals(command)){
				this.showAvailableMemory(out);
			}
			
			else{
				out.println(getHelp());
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, null, e);
		}

	}
	
	
	/**
	 * To mb.
	 *
	 * @param value the value
	 * @return the long
	 */
	private long toMb(long value){
		return ((value / 1000) / 1000);
	}
	
	/**
	 * Show listeners.
	 */
	private void showListeners(){
		new UriScriptMap().print();
	}

	/**
	 * Show available memory.
	 *
	 * @param out the out
	 */
	private void showAvailableMemory(CommandInterpreter out){
		long allocatedMemory  = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		long freeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
		
	
		out.println("Free Memory: " + NumberFormat.getInstance().format(toMb(freeMemory)) + "MByte");
		out.println("Allocated Memory: " + NumberFormat.getInstance().format(toMb(allocatedMemory))  + "MByte");
		
	}
	
	/**
	 * Show queue counts.
	 *
	 * @param server the server
	 * @param out the out
	 */
	private void showQueueCounts(IDominoWebSocketServer server, CommandInterpreter out){
			out.println("show queue counts no longer supported.");
	}
	
	/**
	 * Show scripts.
	 *
	 * @param server the server
	 * @param out the out
	 */
	private void showScripts(IDominoWebSocketServer server, CommandInterpreter out){
		boolean b = false;
		for(Script script : server.getEventObservers()){
			out.println("event observer source=" + script.getSource() + ", event=" + script.getFunction());
			b = true;
		}
		
		if(!b){
			out.println("No registered scripts found.");
		}

	}
	

	/**
	 * Removes the script.
	 *
	 * @param server the server
	 * @param out the out
	 */
	private void removeScript(IDominoWebSocketServer server, CommandInterpreter out){
		throw new IllegalArgumentException("No longer supported.");
	}
	
	/**
	 * Register listener.
	 *
	 * @param server the server
	 * @param out the out
	 */
	private void registerListener(IDominoWebSocketServer server, CommandInterpreter out){
		
		String uri = out.nextArgument();
		String scriptPath = out.nextArgument();

		
		if(StrUtils.isEmpty(scriptPath)){
			out.println("invalid path to script.");
			return;
		}
		
		
		Script script = Script.newScript(scriptPath);
		script.setFunction(Const.ON_MESSAGE);
		script.setSource(scriptPath);
		script.setUri(uri);
		Guicer.getInstance(Activator.bundle).inject(script);
		script.recompile(true);
		server.addUriListener(script);
		out.println(script.toString() + " added to server's URI listeners.");	
	}
	
	/**
	 * Register observer.
	 *
	 * @param server the server
	 * @param out the out
	 */
	private void registerObserver(IDominoWebSocketServer server, CommandInterpreter out){
		
		String func = out.nextArgument();
		String scriptPath = out.nextArgument();

		
		if(StrUtils.isEmpty(func)){
			out.println("func/event is missing (i.e. onMessage, onOpen, onClose)");
			return;
		}
		
		if(StrUtils.isEmpty(scriptPath)){
			out.println("invalid path to script.");
			return;
		}
		
		
		Script script = Script.newScript(scriptPath);
		script.setFunction(func);
		script.setSource(scriptPath);
		Guicer.getInstance(Activator.bundle).inject(script);
		script.recompile(true);
		server.addEventObserver(script);
		
		
		out.println(script.toString() + " added to the server's observers.");
		
	}
	
	
	/**
	 * Register intervaled.
	 *
	 * @param server the server
	 * @param out the out
	 */
	private void registerIntervaled(IDominoWebSocketServer server, CommandInterpreter out){
		
		int interval = Integer.parseInt(out.nextArgument());
		String scriptPath = out.nextArgument();

		
		if(interval<=0) throw new IllegalArgumentException("Interval must be greater than zero.");
		
		if(StrUtils.isEmpty(scriptPath)){
			out.println("invalid path to script.");
			return;
		}
		
		
		Script script = Script.newScript(scriptPath);
		script.setFunction(Const.ON_INTERVAL);
		script.setSource(scriptPath);
		script.setInterval(interval);
		Guicer.getInstance(Activator.bundle).inject(script);
		script.recompile(true);
		server.addIntervaled(script);
		
		
		out.println(script.toString() + " added to the server's intervaled scripts.");
		
	}

	/**
	 * Reload scripts.
	 *
	 * @param out the out
	 * @param server the server
	 */
	private void reloadScripts(CommandInterpreter out, IDominoWebSocketServer server){
		out.println("reloading all scripts...");
		server.reloadScripts();
		out.println("scripts have been reloaded / recompiled");
	}
	
	
	/**
	 * Purge.
	 *
	 * @param out the out
	 */
	private void purge(CommandInterpreter out){
		PurgeDocuments purge = new PurgeDocuments();
		purge.run();
		out.println("purge complete.");
	}
	

	/**
	 * Prints the users.
	 *
	 * @param col the col
	 * @param out the out
	 */
	private void printUsers(Collection<IUser> col, CommandInterpreter out){

		List<IUser> list = new ArrayList<IUser>();
		list.addAll(col);
		ColUtils.sort(list, "getUserId", false);

		for(IUser user : list){
			out.println("userId=" + user.getUserId() + ", server=" + user.getHost() + " , count=" + user.count() + ", connected=" + user.isOpen() + ", isGoingOffline=" + user.isGoingOffline());
		}

	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		return "---WebSocket Service Commands---\n"+
				"websocket start - starts the websocket server (push is automatically started on HTTP startup)\n" +
				"websocket stop - stops the websocket server\n" +
				"";
	}

}

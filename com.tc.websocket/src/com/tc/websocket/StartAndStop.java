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

package com.tc.websocket;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Constants;

import com.google.inject.Module;
import com.tc.di.guicer.Guicer;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.websocket.guice.DominoWebSocketModule;
import com.tc.websocket.guice.NettyModule;
import com.tc.websocket.guice.RunnablesModule;
import com.tc.websocket.runners.StampAll;
import com.tc.websocket.runners.StampAllUsers;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.xpage.profiler.ProfilerModule;

public class StartAndStop {

	private static StartAndStop manager = new StartAndStop();


	public static StartAndStop getInstance(){
		return manager;
	}


	public void start(){
		if(ServerInfo.getInstance().isOnServer()){

			if(Config.getInstance().isValid()){
				@SuppressWarnings("rawtypes")
				Dictionary dict = Activator.bundle.getHeaders();
				Activator.VERSION= String.valueOf(dict.get(Constants.BUNDLE_VERSION));

				//create the guicer object.
				Guicer.createGuicer(Activator.bundle, this.getModules());

				TaskRunner.getInstance().start();
				
				
				
				//cleanup all the RhinoClient users
				StampAll stamp = new StampAll();
				stamp.setField(Const.FIELD_FORM);
				stamp.setValue(Const.FIELD_VALUE_DELETE);
				stamp.run();
				
				
				
				//just in case tell http osgi websocket stop was not called.
				//stamp everyone as offlline.
				//make sure the users that were on the server prior to restart are marked as offline.
				StampAllUsers stampAll = new StampAllUsers();
				stampAll.setStatus(Const.STATUS_OFFLINE);
				stampAll.run();
				
				
				

				//get an instance of the server which will start it up...
				Guicer.getInstance(Activator.bundle).createObject(IDominoWebSocketServer.class);


			}else{
				System.out.println("***There are some problems with your websocket server settings***");
				System.out.println(Config.getInstance().getError());
			}
		}

	}



	public void stop(){
		
		if(ServerInfo.getInstance().isOnServer()){
			IDominoWebSocketServer server = (IDominoWebSocketServer) Guicer.getInstance(Activator.bundle).createObject(IDominoWebSocketServer.class);

			//stop the websocket server.
			server.stop();

			//now lets stop the scheduled exec
			TaskRunner.getInstance().stop();

			server.setOn(false);
			
			DominoWebSocketModule.nullifyServer();//null it out.
			
			//remove the guicer object for this bundle.
			Guicer.removeGuicer(Activator.bundle);
			
			System.out.println("websocket server stopped.");

		}
	}


	public List<Module> getModules(){
		List<Module> modules = new ArrayList<Module>();
		modules.add(new RunnablesModule());
		modules.add(new NettyModule());
		modules.add(new DominoWebSocketModule());
		
		if(Config.getInstance().isProfiled()){
			modules.add(new ProfilerModule());
		}
		
		return modules;
	}

}

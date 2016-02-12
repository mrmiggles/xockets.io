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


package com.tc.websocket.embeded.clients;

import java.util.UUID;

import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.jsf.AbstractWebSocketBean;
import com.tc.websocket.runners.TaskRunner;


public class RhinoRegistry implements IScriptClientRegistry {
	
	
	@Override
	public void registerScriptClient(String host, String uri, String event, String script, String runAsUser, String runAsPassword){
		IScriptBuilder rhinoBuilder = new ScriptBuilder();
		String sessionId = Const.RHINO_PREFIX + UUID.randomUUID().toString().trim();
		String userId = Const.RHINO_PREFIX + script;
		rhinoBuilder.setSessionId(sessionId);
		rhinoBuilder.setUserId(userId);
		rhinoBuilder.addScript(event, script);
		rhinoBuilder.setRunAsCreds(runAsUser, runAsPassword);
		rhinoBuilder.setWebsocketUrl(AbstractWebSocketBean.buildWebSocketUrl(host, sessionId, uri));
		TaskRunner.getInstance().add(rhinoBuilder);
	}
	
	
	@Override
	public void registerScriptClient(String host, String uri, String event, String script){
		this.registerScriptClient(host, uri, event, script, StringCache.EMPTY, StringCache.EMPTY);
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptClientRegistry#registerScriptClient(com.tc.websocket.embeded.clients.ScriptConfig)
	 */
	@Override
	public void registerScriptClient(ScriptBuilder proxy){
		TaskRunner.getInstance().add(proxy);
	}

}

package com.tc.websocket.jsf;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.google.inject.Inject;
import com.tc.di.guicer.Guicer;
import com.tc.utils.StringCache;
import com.tc.utils.XSPUtils;
import com.tc.websocket.Activator;
import com.tc.websocket.Const;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

public class XocketsPhaseListener implements PhaseListener{

	private static final long serialVersionUID = -2279969943683011954L;
	private static final Logger LOG = Logger.getLogger(XocketsPhaseListener.class.getName());
	
	@Inject
	IDominoWebSocketServer server;
	
	public XocketsPhaseListener(){
		Guicer.getInstance(Activator.bundle).inject(this);
	}

	@Override
	public void afterPhase(PhaseEvent event){}

	@Override
	public void beforePhase(PhaseEvent event) {
		IUser user = server.resolveUser(XSPUtils.getSessionId());
		try {
			if(user!=null && !StringCache.ANONYMOUS.equals(XSPUtils.userName()) && user.isAnonymous()){
				LOG.log(Level.SEVERE, "user was anonymous is now " + XSPUtils.userName());
				IWebSocketBean userMgr = (IWebSocketBean) XSPUtils.getBean(Const.WEBSOCKET_BEAN);
				userMgr.registerCurrentUser();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}

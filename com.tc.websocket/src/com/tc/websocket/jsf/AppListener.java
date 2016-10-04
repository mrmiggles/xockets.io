package com.tc.websocket.jsf;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.events.ApplicationListener;

public class AppListener implements ApplicationListener  {

	@Override
	public void applicationCreated(ApplicationEx appEx) {
		// do nothing
	}

	@Override
	public void applicationDestroyed(ApplicationEx appEx) {
		Data.insta().remove(appEx.getApplicationId());
	}

}

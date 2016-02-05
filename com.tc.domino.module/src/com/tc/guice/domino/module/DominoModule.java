package com.tc.guice.domino.module;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.ibm.domino.osgi.core.context.ContextInfo;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;


public class DominoModule extends AbstractModule {
	
	private static final Logger logger = Logger.getLogger(DominoModule.class.getName());
	public static final String GUICE_SERVER_SESSION="ServerInfo";
	
	

	@Override
	protected void configure() {

	} 
	
	
	@Provides
	public Session provideSession(){
		Session session = null;
		if(session==null){
			try {

				try{
					session = ExtLibUtil.getCurrentSession();
				}catch(Exception n){
					logger.log(Level.FINE,"Unable to resolve session with ExtLibUtils.");

				}

				if(session==null){
					try{
						session = DominoUtils.getCurrentSession();
					}catch(Exception n){
						logger.log(Level.FINE,"Unable to resolve session with DominoUtils.");
					}
				}

				if(session==null){
					try{
						session = ContextInfo.getUserSession();
					}catch(Exception n){
						logger.log(Level.FINE,"Unable to resolve session with ContextInfo.");
					}
				}
			}
			catch (Exception e) {
				logger.log(Level.SEVERE,null, e);
			}
		}
		return session;
	}
	   
	

	@Provides
	@Named(GUICE_SERVER_SESSION)
	public Session provideServerSession(){
		return SessionFactory.openSession(); //caller is responsible for recycle
	}
	
	
	@Provides
	public Database provideCurrentDatabase(Session session){
		return ContextInfo.getUserDatabase();
	}

}

package com.tc.websocket.scripts;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import lotus.domino.Session;
import org.apache.commons.io.FileUtils;
import bsh.Interpreter;
import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.BundleUtils;
import com.tc.websocket.Const;

public class BSHScript extends Script {

	private static final Logger logger = Logger.getLogger(BSHScript.class.getName());

	private File sourceFile;
	
	
	@Inject
	private IGuicer guicer;

	@Override
	public void run() {
		Session session = this.openSession();
		try{
			
			//new interpreter for each invocation to keep it thread safe.
			Interpreter interpreter = new Interpreter();
			interpreter.source(this.sourceFile.getPath());
			
			interpreter.set(Const.FUNCTION, this.getFunction());
			interpreter.set(Const.RHINO_SESSION, session);
			interpreter.set(Const.RHINO_BUNDLE_UTIL, new BundleUtils());
			interpreter.set(Const.RHINO_WEB_SOCKET_CLIENT,guicer.inject(new SimpleClient(this)));
			
			
			String func = this.getFunction();

			if(this.getFunction().equalsIgnoreCase(Const.ON_MESSAGE)){
				interpreter.set("msg", this.getArgs()[0]);
				func = this.getFunction() + "(msg)";

			}else if (this.getFunction().equalsIgnoreCase(Const.ON_OPEN)){
				interpreter.set("user", this.getArgs()[0]);
				func = this.getFunction() + "(user)";

			}else if (this.getFunction().equalsIgnoreCase(Const.ON_CLOSE)){
				interpreter.set("user", this.getArgs()[0]);
				func = this.getFunction() + "(user)";

			}else if (this.getFunction().equalsIgnoreCase(Const.ON_ERROR)){
				interpreter.set("ex", this.getArgs()[0]);
				func = this.getFunction() + "(ex)";

			}
			
			interpreter.eval(func);
		
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}finally{
			this.closeSession(session);
		}
	}

	
	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}



	@Override
	public synchronized Script copy(Object ...args){
		BSHScript copy  = new BSHScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setSourceFile(this.getSourceFile());
		copy.setCreds(user, password);
		return copy;
	}

	@Override
	public boolean recompile(boolean reload) {

		boolean b = true;
		try{
			if(reload){
				this.setScript(this.extractFile());

				if(this.sourceFile!=null){
					this.sourceFile.delete(); //cleanup prior file.
				}
				
				this.sourceFile = File.createTempFile("tmp", ".bsh");
				FileUtils.writeByteArrayToFile(this.sourceFile, this.getScript().getBytes());

			}
		}catch(Exception e){
			logger.log(Level.SEVERE, null, e);
		}
		return b;
	}

}

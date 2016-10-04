/*
 * 
 */
package com.tc.websocket.scripts;

import java.io.File;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Session;

import org.apache.commons.io.FileUtils;

import bsh.Interpreter;

import com.tc.websocket.Const;


// TODO: Auto-generated Javadoc
/**
 * The Class BSHScript.
 */
public class BSHScript extends Script {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(BSHScript.class.getName());

	/** The source file. */
	private File sourceFile;
	
	
	


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		if(!this.shouldRun()){return;}
		
		Session session = this.openSession();
		try{
			
			
			//new interpreter for each invocation to keep it thread safe.
			Interpreter interpreter = new Interpreter();
			interpreter.source(this.sourceFile.getPath());
			
			
			for( Entry<String,Object> entry: this.getCommonVars(session).entrySet()){
				interpreter.set(entry.getKey(), entry.getValue());
			}
			

			
			
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

			}else if (this.getFunction().equalsIgnoreCase(Const.ON_INTERVAL)){
				func = this.getFunction() + "()";

			}
			
			interpreter.eval(func);
			
			this.setLastRun(new Date());
		
		}catch(Exception e){
			errorCount.incrementAndGet();
			LOG.log(Level.SEVERE,null, e);
		}finally{
			this.closeSession(session);
		}
	}

	
	/**
	 * Gets the source file.
	 *
	 * @return the source file
	 */
	public File getSourceFile() {
		return sourceFile;
	}

	/**
	 * Sets the source file.
	 *
	 * @param sourceFile the new source file
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#copy(java.lang.Object[])
	 */
	@Override
	public synchronized Script copy(Object ...args){
		BSHScript copy  = new BSHScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setSourceFile(this.getSourceFile());
		copy.setLastRun(this.getLastRun());
		copy.setCreds(user, password);
		return copy;
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#recompile(boolean)
	 */
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
			LOG.log(Level.SEVERE, null, e);
		}
		return b;
	}

}

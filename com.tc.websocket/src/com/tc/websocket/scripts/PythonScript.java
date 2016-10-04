/*
 * 
 */
package com.tc.websocket.scripts;

import java.util.Date;
import java.util.Map.Entry;

import lotus.domino.Session;

import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;


// TODO: Auto-generated Javadoc
/**
 * The Class PythonScript.
 */
public class PythonScript extends Script{

	/** The guicer. */
	@Inject
	IGuicer guicer;
	
	/** The compiled. */
	private PyCode compiled;


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		if(!this.shouldRun()){return;}
		
		
		Session session = null;
		PythonInterpreter python = null;

		try{
			
			if(this.isCallingItself()) return;
			


			
			session = this.openSession();
			python = new PythonInterpreter();


			//setup the global vars.
			for( Entry<String,Object> entry: this.getCommonVars(session).entrySet()){
				python.set(entry.getKey(), Py.java2py(entry.getValue()));
			}
			
			
			//execute the compiled code.
			python.exec(this.getCompiled());

			//get handle on the function
			PyFunction func = (PyFunction) python.get(this.getFunction());
			
			//convert all the args to pyobjects (no args for intervaled script)
			if(!this.isIntervaled()){
				PyObject[] pyArgs = new PyObject[args.length];
				for(int i = 0; i< args.length; i++){
					guicer.inject(args[i]); //add dependency
					pyArgs[i] = Py.java2py(args[i]); // wrap in a PyObject
				}
				//call the function, pass the PyObject[] args.
				func.__call__(pyArgs);
			}else{
				//call the empty function
				func.__call__();
			}
			

			
			
			
			this.setLastRun(new Date());
			
			
		}catch(Exception e){
			errorCount.incrementAndGet();
			e.printStackTrace();
		}finally{
			if(python!=null) python.cleanup();
			this.closeSession(session);
		}
	}
	


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#copy(java.lang.Object[])
	 */
	@Override
	public Script copy(Object ...args) {
		PythonScript copy = new PythonScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setCreds(user, password);
		copy.setLastRun(this.getLastRun());
		copy.setCompiled(this.getCompiled());
		return copy;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#recompile(boolean)
	 */
	@Override
	public boolean recompile(boolean reload) {
		
		if(reload){
			this.setScript(this.extractFile());
		}
		
		PythonInterpreter python = new PythonInterpreter();
		this.setCompiled(python.compile(this.getScript()));
		python.cleanup();
		
		return true;
	}



	/**
	 * Gets the compiled.
	 *
	 * @return the compiled
	 */
	public PyCode getCompiled() {
		return compiled;
	}



	/**
	 * Sets the compiled.
	 *
	 * @param compiled the new compiled
	 */
	public void setCompiled(PyCode compiled) {
		this.compiled = compiled;
	}
	
	

}

package com.tc.websocket.scripts;

import lotus.domino.Session;

import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.BundleUtils;
import com.tc.websocket.Const;

public class PythonScript extends Script{

	@Inject
	IGuicer guicer;
	
	private PyCode compiled;

	@Override
	public void run() {

		Session session = null;
		PythonInterpreter python = null;

		try{
			
			if(this.isCallingItself()) return;
			
			session = this.openSession();
			python = new PythonInterpreter();


			//setup the global vars.
			python.set(Const.FUNCTION, Py.java2py(this.getFunction()));
			python.set(Const.RHINO_SESSION, Py.java2py(session));
			python.set(Const.RHINO_BUNDLE_UTIL, Py.java2py(new BundleUtils()));
			python.set(Const.RHINO_WEB_SOCKET_CLIENT, Py.java2py(guicer.inject(new SimpleClient(this))));
			
			
			//execute the compiled code.
			python.exec(this.getCompiled());

			//get handle on the function
			PyFunction func = (PyFunction) python.get(this.getFunction());
			
			//convert all the args to pyobjects
			PyObject[] pyArgs = new PyObject[args.length];
			for(int i = 0; i< args.length; i++){
				guicer.inject(args[i]); //add dependency
				pyArgs[i] = Py.java2py(args[i]); // wrap in a PyObject
			}
			
			//call the function, pass the PyObject[] args.
			func.__call__(pyArgs);
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			python.cleanup();
			this.closeSession(session);
		}
	}
	


	@Override
	public Script copy(Object ...args) {
		PythonScript copy = new PythonScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setCreds(user, password);
		copy.setCompiled(this.getCompiled());
		return copy;
	}

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



	public PyCode getCompiled() {
		return compiled;
	}



	public void setCompiled(PyCode compiled) {
		this.compiled = compiled;
	}
	
	

}

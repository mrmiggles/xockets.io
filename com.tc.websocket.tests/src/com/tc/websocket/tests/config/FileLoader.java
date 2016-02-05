package com.tc.websocket.tests.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;



public class FileLoader {
	private File[] files;

	public FileLoader(){
		this.files=new File(TestConfig.getInstance().getSampleDataDir()).listFiles();
	}


	public File selectFile(int index){
		return files[index];
	}



	public String getData(File file){
		String data = null;
		InputStream in = null;
		try{
			in = new FileInputStream(file);
			data = IOUtils.toString(in);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(in);
		}
		return data;
	}


	public File[] getFiles() {
		return files;
	}





}

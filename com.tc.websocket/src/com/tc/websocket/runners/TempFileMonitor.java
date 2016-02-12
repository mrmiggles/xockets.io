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

package com.tc.websocket.runners;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.tc.utils.DateUtils;


public class TempFileMonitor implements Runnable {
	
	private static final Logger logger = Logger.getLogger(TempFileMonitor.class.getName());

	@Override
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}

		try{
			File temp = File.createTempFile("temp", "temp"); 
			String absolutePath = temp.getAbsolutePath();
			String tempFilePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));

			System.out.println("cleaning out directory " + tempFilePath);

			File tempdir = new File(tempFilePath);
			File[] files = tempdir.listFiles();

			temp.delete();//cleanup

			for(File file: files){
				String name = file.getName();
				if(file.exists() && name.startsWith("eo") && name.endsWith("tm")){
					//calculate the age
					Date lastmod = new Date(file.lastModified());
					long minutes = DateUtils.getTimeDiffMin(lastmod, new Date());
					if(minutes >= 5){
						FileUtils.deleteQuietly(file);
					}
				}
			}
		}catch(Exception e){
			logger.log(Level.SEVERE,null ,e);
		}
	}

}

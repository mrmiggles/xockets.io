/*
 * © Copyright Tek Counsel LLC 2013
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


package com.tc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class CompressionUtils {
	
	private static final Logger logger = Logger.getLogger(CompressionUtils.class.getName());
	
    public static byte[] compress(byte[] content) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] byteMe = null;
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteOut);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
            byteMe = byteOut.toByteArray();
        }finally{
        	byteOut.close();
        }
        return byteMe;
    }

    public static byte[] decompress(byte[] data) {

        byte[] tmpByte = new byte[1024];
        byte[] decomp = null;

        try {
            int read;

            GZIPInputStream zipin = new GZIPInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
            while ((read = zipin.read(tmpByte, 0, 1024)) > 0) {
                bout.write(tmpByte, 0, read);
            }
            decomp = bout.toByteArray();
            bout.close();
            zipin.close();
        } catch (Exception e) {
        	logger.log(Level.SEVERE,null,e);
        }

        return decomp;

    }
}

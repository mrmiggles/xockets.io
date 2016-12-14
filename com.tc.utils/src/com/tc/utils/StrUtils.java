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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;



public final class StrUtils {

	private static final StrUtils strUtils = new StrUtils();

	
	
    private StrUtils() {
    	//should not be instantiated.
    }
    
    public static StrUtils insta(){
    	return strUtils;
    }
    
    public static final String EMPTY_STRING=StringCache.EMPTY;
    

    public static String right(String source, String searchFor) {
        int index = source.indexOf(searchFor) + searchFor.length();

        if (index < 0) {
            return StringCache.EMPTY;
        }
        return source.substring(index);
    }

    public static String remChars(String source, char[] chars) {

        char[] charSrc = source.toCharArray();
        StringBuilder sb = new StringBuilder(charSrc.length);

        for (int i = 0; i < charSrc.length; i++) {
            char c = charSrc[i];
            if (!hasChar(chars, c)) {
                sb.append(c);
            }
        }
        return sb.toString();

    }
    
    
    public static String removeHtml(String source){
    	String scrubbed = source.replaceAll("<[^>]*>", StringCache.EMPTY).replaceAll("&nbsp;", StringCache.EMPTY);
    	return StringEscapeUtils.unescapeHtml4(scrubbed);
    }
    
    private static final char[] COMPACT_CHARS = {'\r','\n','\r','\t'};
    public static String compact(String source){
    	return  StrUtils.remRepeats(StrUtils.replaceChars(source, COMPACT_CHARS, ' '), ' ');
    }
    

    public static String remRepeats(String source, char r){
    	StringBuilder sb = new StringBuilder();
    	
    	char prior = '\0';
    	for(char c : source.toCharArray()){
    		if(c == r && prior ==r){
    			//do nothing
    		}else{
    			sb.append(c);
    		}
    		prior = c;
    	}
    	return sb.toString();
    }

    public static String whiteList(String source, char[] allowedChars) {
        char[] charSrc = source.toCharArray();
        StringBuffer sb = new StringBuffer(charSrc.length);

        for (int i = 0; i < charSrc.length; i++) {
            char c = charSrc[i];
            if (hasChar(allowedChars, c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String replaceChar(String source, char target, char replace) {
        char[] charSrc = source.toCharArray();
        StringBuffer sb = new StringBuffer(charSrc.length);

        for (int i = 0; i < charSrc.length; i++) {
            char c = charSrc[i];
            if (c == target) {
                sb.append(replace);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String replaceChars(String source, char[] chars, char replace) {

        char[] charSrc = source.toCharArray();
        StringBuffer sb = new StringBuffer(charSrc.length);

        for (int i = 0; i < charSrc.length; i++) {
            char c = charSrc[i];
            if (!hasChar(chars, c)) {
                sb.append(c);
            } else {
                sb.append(replace);
            }
        }
        return sb.toString();

    }

    public static boolean hasChar(char[] chars, char c) {
        for (int x = 0; x < chars.length; x++) {
            if (c == chars[x]) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAnyChar(char[] charArray, String target) {
        char[] chars = target.toCharArray();
        for (int x = 0; x < chars.length; x++) {
            char c = chars[x];
            if (hasChar(charArray, c)) {
                return true;
            }
        }
        return false;
    }

    
    public static String rightBack(String source, String searchFor) {
        int index = source.lastIndexOf(searchFor) + searchFor.length();

        if (index < 0) {
            return StringCache.EMPTY;
        }
        return source.substring(index);
    }


    public static String left(String source, String searchFor) {
        int index = source.indexOf(searchFor);

        if (index <= 0) {
            return StringCache.EMPTY;
        }
        return source.substring(0, index);
    }


    public static String leftBack(String source, String searchFor) {
        int index = source.lastIndexOf(searchFor);
        if (index <= 0) {
            return StringCache.EMPTY;
        }
        return source.substring(0, index);
    }

    public static String middle(String source, String start, String end) {
        String one = StrUtils.right(source, start);
        return StrUtils.leftBack(one, end);
    }

    public static String middle(String source, int startIndex, int length) {
        return source.substring(startIndex, source.length() - length);
    }


    public static String replace(String source, String searchFor, String replaceWith) {
    	return source.replace(searchFor, replaceWith);
    }

    public static String replace(String source, String[] searchFor, String replaceWith) {
        for(String str : searchFor){
            source = StrUtils.replace(source, str, replaceWith);
        }
        return source;
    }


    public static String[] explode(String source) {
        return StrUtils.explode(source, " ");
    }

   
    public static String[] explode(String s, String delimiter) {
        return s.split(delimiter);
    }


    public static String implode(Object[] elements, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elements.length - 1; i++) {
        	builder.append(elements[i]);
        	builder.append(delimiter);
        }
        builder.append((String) elements[elements.length - 1]);
        return builder.toString();
    }

    public static String implode(List<String> list, String delimeter) {
        String[] arr = new String[list.size()];
        list.toArray(arr);
        return StrUtils.implode(arr, delimeter);
    }

    public static String implode(Object[] elements) {
        return implode(elements, ", ");
    }

    public static String word(String source, String delimiter, int wordNo) {
        String[] split = explode(source, delimiter);

        if (split.length > wordNo - 1) {
            return split[wordNo - 1];
        }
        return StringCache.EMPTY;
    }
    
    public static String remove(String source, char searchFor) {
        String s = String.valueOf(searchFor);
        return StrUtils.remove(source, s);
    }


    public static String remove(String source, String searchFor) {
        return StrUtils.replace(source, searchFor, StringCache.EMPTY);
    }


    public static String remove(String source, String searchFor[]) {
        return StrUtils.replace(source, searchFor, StringCache.EMPTY);
    }

    public static String getStackTrace(Throwable t) throws IOException {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        sw.close();
        return sw.toString();
    }


    public static boolean isEmpty(String s) {
    	return s ==null || s.length() == 0;
    }
    
    public static boolean areEmpty(String ...s){
    	boolean b = true;
    	for(String str : s){
    		if(!isEmpty(str)){
    			b = false;
    			break;
    		}
    	}
    	return b;
    }
    
    public static boolean hasEmpty(String ...s){
    	boolean b = false;
    	for(String str : s){
    		if(isEmpty(str)){
    			b = true;
    			break;
    		}
    	}
    	return b;
    }


    public static boolean isUpperCase(char c) {
        return Character.isUpperCase(c);
    }

    public static boolean containsNumber(String string) {

        char[] c = string.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (isNumber(c[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRepeatedChars(String str) {

        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            int next = i + 1;

            if (next < str.length()) {
                char currentChar = chars[i];
                char nextChar = chars[next];
                if (currentChar == nextChar) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int repatedCharacterCount(String str) {

        char[] chars = str.toCharArray();

        int cntr = 0;

        for (int i = 0; i < chars.length; i++) {

            int next = i + 1;

            if (next < str.length()) {
                char currentChar = chars[i];
                char nextChar = chars[next];
                if (currentChar == nextChar) {
                    cntr++;
                }
            }
        }

        return cntr;
    }

    public static boolean isDate(String dtString) {
        String pattern = "\\d\\d/\\d\\d/\\d\\d\\d\\d";
        return dtString.matches(pattern);
    }

    public static final String ALL_UPPERCASE_LETTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static boolean isCapitalLetter(char letter) {
        String alpha = ALL_UPPERCASE_LETTERS;
        char[] c = alpha.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (letter == c[i]) {
                return true;
            }
        }
        return false;
    }

    public static final String ALL_LETTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static boolean isLetter(char letter) {
        return ALL_LETTERS.indexOf(letter) > -1;
    }


    public static String stripOutNonNumeric(String value) {
        char[] c = value.toCharArray();
        StringBuffer sb = new StringBuffer(c.length);

        for (int i = 0; i < c.length; i++) {
            if (StrUtils.isNumber(c[i])) {
                sb.append(c[i]);
            }
        }

        return sb.toString();
    }

    public static String stripOutNonAlpha(String value) {
        char[] c = value.toCharArray();
        StringBuffer sb = new StringBuffer(c.length);

        for (int i = 0; i < c.length; i++) {
            if (StrUtils.isLetter(c[i])) {
                sb.append(c[i]);
            }
        }

        return sb.toString();
    }

    public static boolean instr(String source, String check) {
        return source.indexOf(check) != -1;
    }

    public static boolean instr(String source, char check) {
        return source.indexOf(check) != -1;
    }
    
    private static String NUMBERS="0123456789";
    public static boolean isNumber(char nbr) {
        return NUMBERS.indexOf(nbr) > -1;
    }


    public static boolean isNumber(String number) {
    	boolean b = true;
    	char[] chars = number.toCharArray();
    	for(char c : chars){
    		if(!StrUtils.isNumber(c)){
    			b = false;
    			break;
    		}
    	}
    	return b;
    }

    public static String removeLeadingChar(String source, char c) {
        char[] car = source.toCharArray();

        int pos = 0;

        for (int i = 0; i < car.length; i++) {
            if (car[i] == c) {
                pos++;
            } else {
                break;
            }
        }

        return source.substring(pos, source.length());
    }
    
    

    /* remove leading whitespace */
    public static String ltrim(String source) {
        return source.replaceAll("^\\s+", StringCache.EMPTY);
    }

    /* remove trailing whitespace */
    public static String rtrim(String source) {
        return source.replaceAll("\\s+$", StringCache.EMPTY);
    }
    
    
    
	public static Map<String,String> parseCookie(String data){
		String[] arr = data.split("\r\n");
		Map<String,String> cookies = new HashMap<String,String>();
		for(String str : arr){
			if(str.toLowerCase().contains("cookie")){
				str = str.substring(str.indexOf(":") + 1, str.length());
				String[] pairs =str.split(";");
				for(String nv : pairs){
					String[] nameValue = nv.trim().split("=");
					cookies.put(nameValue[0], nameValue[1]);
				}
			}
		}
		return cookies;
	}


}

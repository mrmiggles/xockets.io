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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@SuppressWarnings("unchecked")
public final class ColUtils {

    public static final int SORT_ASC = 1;
    public static final int SORT_DESC = 2;
    private static final ReflectionUtils reflect=new ReflectionUtils();

    @SuppressWarnings("rawtypes")
	public static List<Object> toArrayList(Collection col, String getter, boolean sort, boolean unique) {
        List list = new ArrayList(col.size());
        Iterator iter = col.iterator();

        while (iter.hasNext()) {
            Object o = iter.next();
            Object obj = reflect.invokeMethod(getter, o);
            if (unique) {
                if (list.indexOf(obj) == -1) {
                    list.add(obj);
                }
            } else {
                list.add(obj);
            }

        }
        if (sort) {
            Collections.sort(list);
        }
        return list;
    }


	public static List<String> toArrayList(String[] array) {
        List<String> list = new ArrayList<String>(array.length);
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }


	public static List<Integer> toArrayList(int[] array) {
        List<Integer> list = new ArrayList<Integer>(array.length);
        for (int i = 0; i < list.size(); i++) {
            Integer integer = new Integer(array[i]);
            list.add(integer);
        }
        return list;
    }


	public static List<Long> toArrayList(long[] array) {
        List<Long> list = new ArrayList<Long>(array.length);
        for (int i = 0; i < list.size(); i++) {
            Long lng = new Long(array[i]);
            list.add(lng);
        }
        return list;
    }


	public static List<Double> toArrayList(double[] array) {
        List<Double> list = new ArrayList<Double>(array.length);
        for (int i = 0; i < list.size(); i++) {
            Double dbl = new Double(array[i]);
            list.add(dbl);
        }
        return list;
    }

	public static <T> List<T> getExclusionList(Collection<T> col, String getter, String[] filter) {
        List<T> newList = new ArrayList<T>(col.size());
        List<?> arrFilter = toArrayList(filter);
        
        for(T t : col){
            Object data = reflect.invokeMethod(getter, t);
            if (arrFilter.contains(data) == false) {
                newList.add(t);
            }
        }
        
        return newList;
    }

	public static <T> List<T> getInclusionList(Collection<T> col, String getter, String[] filter) {
        List<T> newList = new ArrayList<T>(col.size());
        List<String> arrFilter = toArrayList(filter);

        for(T t : col){
            Object data = reflect.invokeMethod(getter, t);
            if (arrFilter.contains(data)) {
                newList.add(t);
            }
        }
        return newList;
    }

	public static <T>List<T> getFilteredList(Collection<T> col, String getter, Object value) {
        List<T> newList = new ArrayList<T>(col.size());
        for(T t : col){
            Object data = reflect.invokeMethod(getter, t);
            if (data.equals(value)) {
                newList.add(t);
            }
        }
        return newList;
    }

	public static <T> void stampCollection(Collection<T> col, String setter, Object value) {
        for(T t : col){
        	reflect.invokeSetter(setter, t, value);
        }
    }

	public static void main(String[] args) {
		List<NameValue<Integer>> list = new ArrayList<NameValue<Integer>>();
		
		for(int i=0;i<1000;i++){
			NameValue<Integer> nv = new NameValue<Integer>();
			if(i >= 230){
				nv.setValue(999);
			}else{
				nv.setValue(new Integer(i));
			}
			nv.setName("name(" + i + ") ");
			
			list.add(nv);
		}
		
		
		
	//	ColUtils.stampCollection(list, "setName", "HI");
		int start = ColUtils.findIndex(list, "getValue", new Integer(999), true);
		System.out.println("start = " + start);
		NameValue<?> nv = ColUtils.findObject(list, "getValue", new Integer(999), true);
		System.out.println(nv.toString());
    }
	

	public static <T> int findIndex(List<T> col, String getter, Object value, boolean sort){
		BeanCompare c = new SearchCompare();
		c.setCompareAttribute(getter);

		if (col == null || col.size() == 0) {
			System.out.println("col.size is zero or null");
			return -1;
		}
		
		if(sort){
			Collections.sort(col, c);
		}

		return Collections.binarySearch(col, value, c);
	}

	public static <T> T findObject(List<T> col, String getter, Object value, boolean sort) {
		BeanCompare c = new SearchCompare();
		T t = null;

		if (col == null || col.size() == 0) {
			System.out.println("col.size is zero or null");
			return null;
		}

		c.setCompareAttribute(getter);
		
		if(sort){
			Collections.sort(col, c);
		}

		int i = Collections.binarySearch(col, value, c);
		if (i >= 0) {
			t = col.get(i);
		}
		return t;
	}
    
    
    public static <T> void sort(List<T> list, String getter, boolean desc){
        BeanCompare c = new SearchCompare();

        if (list == null || list.size() == 0) {
        	return;
        }
        

        c.sortDescending(desc);
        c.setCompareAttribute(getter);
        Collections.sort(list,c);
    }
    
    
    public static <T> Vector<T> toVector(List<T> list){
    	Vector<T> vec = new Vector<T>(list.size());
    	for(T t : list){
    		vec.add(t);
    	}
    	return vec;
    }

    public static Map<String,Object> toMap(Object ...args){
    	Map<String,Object> map = new HashMap<String,Object>();
    	
    		int cntr =0;
    		String name = null;
    		for(Object o : args){
    			if(cntr % 2 ==0){
    				name = (String)o;
    			}else{
    				map.put(name, o);
    			}
    			cntr++;
    		}
    	return map;
    }
    
    
    
}

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


package com.tc.xpage.profiler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.ibm.commons.util.profiler.Profiler;
import com.ibm.commons.util.profiler.ProfilerAggregator;
import com.ibm.commons.util.profiler.ProfilerType;

public class ProfileInterceptor implements MethodInterceptor {
	public static Map<String,ProfilerType> map = new ConcurrentHashMap<String,ProfilerType>();
	private static final Object lock = new Object();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object value = null;
		if(Profiler.isEnabled()){
			if(!Profiler.isStarted()){
				synchronized(lock){
					if(!Profiler.isStarted()){
						Profiler.startProfiler();
					}
				}
			}

			String methodname = invocation.getMethod().getName();
			ProfilerType mytype =getProfilerType(invocation.getThis());

			ProfilerAggregator agg=Profiler.startProfileBlock(mytype,methodname);
			long startProfiler = Profiler.getCurrentTime();
			value = invocation.proceed();
			Profiler.endProfileBlock(agg, startProfiler);
		}else{
			value = invocation.proceed();
		}

		return value;
	}



	private static ProfilerType getProfilerType(Object o){
		ProfilerType profilerType = map.get(o.getClass().getName());
		String cls = getTypeName(o);

		if(profilerType==null){
			profilerType = new ProfilerType(cls);
			map.put(cls,profilerType);
		}
		return profilerType;
	}

	private static String getTypeName(Object o){
		String cls = o.getClass().getName();
		int indexOf = cls.indexOf('$');
		return cls.substring(0, indexOf);
	}

}






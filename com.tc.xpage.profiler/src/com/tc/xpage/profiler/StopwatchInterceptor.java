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

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.tc.utils.StopWatch;

public class StopwatchInterceptor implements MethodInterceptor {


	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		StopWatch watch = new StopWatch();
		Object value = null;
		String methodname = invocation.getMethod().getName();
		com.tc.xpage.profiler.Stopwatch stopwatch = invocation.getMethod().getAnnotation(com.tc.xpage.profiler.Stopwatch.class);
		String clz = this.className(invocation.getThis().getClass().getName());
		value = invocation.proceed();
		long time = watch.elapsedTime(TimeUnit.MILLISECONDS);
		if(time > stopwatch.time()){
			System.out.println(clz + "." + methodname + " took " + time + " milliseconds");
		}
		return value;
	}

	private String className(String clz){
		return clz.substring(0, clz.indexOf("$$"));
	}

}






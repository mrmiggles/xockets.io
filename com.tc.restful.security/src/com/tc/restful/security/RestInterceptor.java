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


package com.tc.restful.security;

import java.lang.reflect.Method;
import java.util.Vector;

import javax.ws.rs.core.Response;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.tc.utils.XSPUtils;

public class RestInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		boolean isAuth = this.isAuthorized(rolesAllowed.value());

		if(!isAuth){
			return Response.status(403).type("text/plain").entity("You are not authorized to perform this operation.").build();
		}
		
		return invocation.proceed();
	}


	//check to see if the current user belongs to any of the roles
	//associated with the database context
	@SuppressWarnings("unchecked")
	private boolean isAuthorized(String[] rolesAllowed) {
		boolean b = false;

		try{
			Session s= XSPUtils.session();
			Database db = XSPUtils.database();

			if(s!=null && db!=null){

				Vector<String> roles = db.queryAccessRoles(s.getEffectiveUserName());

				for(String role : rolesAllowed){
					if(roles.contains(role)){
						b = true;
						break;
					}
				}
			}
		}catch(NotesException n){
			n.printStackTrace();
		}
		return b;
	}

}






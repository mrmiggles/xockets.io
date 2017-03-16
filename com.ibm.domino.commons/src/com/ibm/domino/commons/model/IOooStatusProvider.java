/*
 * � Copyright IBM Corp. 2013
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

package com.ibm.domino.commons.model;

import lotus.domino.Database;

/**
 * Interface for getting and setting Out of Office status. 
 */
public interface IOooStatusProvider {
    
    public OooStatus get(Database database) throws ModelException;
    
    public void put(Database database, OooStatus oooStatus) throws ModelException;

    /**
     * Frees any resources associated with this provider.
     * 
     * <p>The provider assumes this method is called only once at the end of the
     * container's lifecycle.  The container is responsible for making sure that
     * is the case.  Also, after calling this method, the container should not call 
     * other provider methods (e.g. get or put). 
     */
    public void destroy();
}

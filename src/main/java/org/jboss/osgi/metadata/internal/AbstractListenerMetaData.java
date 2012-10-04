/*
 * #%L
 * JBossOSGi Resolver Metadata
 * %%
 * Copyright (C) 2010 - 2012 JBoss by Red Hat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.jboss.osgi.metadata.internal;

import java.io.Serializable;

import org.jboss.osgi.metadata.ListenerMetaData;


/**
 * Simple listener meta data. Referencing ref bean as a reference listener.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
class AbstractListenerMetaData implements ListenerMetaData, Serializable {
    private static final long serialVersionUID = 1l;

    private String ref;
    private String bindMethod;
    private String unbindMethod;

    public String getRef() {
        return ref;
    }

    public String getBindMethod() {
        return bindMethod;
    }

    public String getUnbindMethod() {
        return unbindMethod;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setBindMethod(String bindMethod) {
        this.bindMethod = bindMethod;
    }

    public void setUnbindMethod(String unbindMethod) {
        this.unbindMethod = unbindMethod;
    }

}

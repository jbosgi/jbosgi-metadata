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

import static org.jboss.osgi.metadata.MetadataLogger.LOGGER;

import java.util.ArrayList;
import java.util.List;

import org.jboss.osgi.metadata.ParameterizedAttribute;


/**
 * Create parameterized attribute list from string attribute.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 */
abstract class ParameterizedAttributeListValueCreator extends ListValueCreator<ParameterizedAttribute> {

    public List<ParameterizedAttribute> useString(String attribute) {
        List<ParameterizedAttribute> list = new ArrayList<ParameterizedAttribute>();
        parseAttribute(attribute, list, LOGGER.isTraceEnabled());
        return list;
    }

    /**
     * Use appropriate JavaCC parsing util.
     * 
     * @param attribute string value to parse
     * @param list data holder list
     * @param trace log trace
     */
    protected abstract void parseAttribute(String attribute, List<ParameterizedAttribute> list, boolean trace);

}

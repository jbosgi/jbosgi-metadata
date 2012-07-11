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

import static org.jboss.osgi.metadata.internal.MetadataLogger.LOGGER;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE;
import static org.osgi.framework.Constants.BUNDLE_VERSION_ATTRIBUTE;
import static org.osgi.framework.Constants.RESOLUTION_DIRECTIVE;
import static org.osgi.framework.Constants.RESOLUTION_MANDATORY;
import static org.osgi.framework.Constants.VERSION_ATTRIBUTE;
import static org.osgi.framework.Constants.VISIBILITY_DIRECTIVE;
import static org.osgi.framework.Constants.VISIBILITY_PRIVATE;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.osgi.metadata.Parameter;
import org.jboss.osgi.metadata.VersionRange;

/**
 * OSGi parameter values. Util for transforming parameter info to actual useful values.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 */
public class OSGiParameters {

    protected Map<String, Parameter> parameters;
    protected Map<String, Object> cachedAttributes;

    public OSGiParameters(Map<String, Parameter> parameters) {
        this.parameters = Collections.unmodifiableMap(parameters);
        this.cachedAttributes = new ConcurrentHashMap<String, Object>();
    }

    protected Map<String, Parameter> getParameters() {
        return parameters;
    }

    public VersionRange getVersion() {
        return get(VERSION_ATTRIBUTE, ValueCreatorUtil.VERSION_RANGE_VC);
    }

    public String getBundleSymbolicName() {
        return get(BUNDLE_SYMBOLICNAME_ATTRIBUTE, ValueCreatorUtil.STRING_VC);
    }

    public VersionRange getBundleVersion() {
        return get(BUNDLE_VERSION_ATTRIBUTE, ValueCreatorUtil.VERSION_RANGE_VC);
    }

    public String getVisibility() {
        return get(VISIBILITY_DIRECTIVE, ValueCreatorUtil.STRING_VC, VISIBILITY_PRIVATE);
    }

    public String getResolution() {
        return get(RESOLUTION_DIRECTIVE, ValueCreatorUtil.STRING_VC, RESOLUTION_MANDATORY);
    }

    protected <T> T get(String key, ValueCreator<T> creator) {
        return get(key, creator, null);
    }

    @SuppressWarnings({ "unchecked" })
    protected <T> T get(String key, ValueCreator<T> creator, T defaultValue) {
        T value = (T) cachedAttributes.get(key);
        if (value == null) {
            Parameter parameter = parameters.get(key);
            if (parameter != null) {
                Object paramValue = parameter.getValue();
                if (parameter.isCollection()) {
                    if (creator instanceof CollectionValueCreator) {
                        CollectionValueCreator<T> cvc = (CollectionValueCreator<T>) creator;
                        value = cvc.createValue((Collection<String>) paramValue);
                    } else {
                        LOGGER.warnCannotCreateValueForParameter(creator, parameter);
                    }
                } else {
                    value = creator.createValue(paramValue.toString());
                }
            } else if (defaultValue != null) {
                value = defaultValue;
            }
            if (value != null)
                cachedAttributes.put(key, value);
        }
        return value;
    }
}

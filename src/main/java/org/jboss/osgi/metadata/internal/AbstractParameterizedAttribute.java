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

import static org.jboss.osgi.metadata.internal.MetadataMessages.MESSAGES;

import java.util.Collections;
import java.util.Map;

import org.jboss.osgi.metadata.Parameter;
import org.jboss.osgi.metadata.ParameterizedAttribute;

/**
 * Parameter attribute impl.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 */
public class AbstractParameterizedAttribute extends AbstractAttributeAware implements ParameterizedAttribute {

    protected Map<String, Parameter> attributes;
    protected Map<String, Parameter> directives;

    public AbstractParameterizedAttribute(String attribute, Map<String, Parameter> attributes, Map<String, Parameter> directives) {
        super(attribute);
        if (attributes == null)
            attributes = Collections.emptyMap();
        this.attributes = attributes;
        if (directives == null)
            directives = Collections.emptyMap();
        this.directives = directives;
    }

    public Map<String, Parameter> getAttributes() {
        return attributes;
    }

    public Parameter getAttribute(String name) {
        return attributes.get(name);
    }

    public <T> T getAttributeValue(String name, Class<T> type) {
        return getAttributeValue(name, null, type);
    }

    public <T> T getAttributeValue(String name, T defaultValue, Class<T> type) {
        Parameter parameter = getAttribute(name);
        if (parameter == null)
            return defaultValue;
        if (parameter.isCollection())
            throw MESSAGES.illegalArgumentDuplicateAttribute(name);
        Object value = parameter.getValue();
        if (value == null)
            return defaultValue;
        return type.cast(value);
    }

    public Map<String, Parameter> getDirectives() {
        return directives;
    }

    public Parameter getDirective(String name) {
        return directives.get(name);
    }

    public <T> T getDirectiveValue(String name, Class<T> type) {
        return getDirectiveValue(name, null, type);
    }

    public <T> T getDirectiveValue(String name, T defaultValue, Class<T> type) {
        Parameter parameter = getDirective(name);
        if (parameter == null)
            return defaultValue;
        if (parameter.isCollection())
            throw MESSAGES.illegalArgumentDuplicateDirective(name);
        Object value = parameter.getValue();
        if (value == null)
            return defaultValue;
        return type.cast(value);
    }

    @Override
    public String toString() {
        return attribute + "[attr=" + attributes + ",dirs=" + directives + "]";
    }
}

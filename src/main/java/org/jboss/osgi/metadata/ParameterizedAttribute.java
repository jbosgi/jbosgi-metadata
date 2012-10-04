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
package org.jboss.osgi.metadata;

import java.util.Map;


/**
 * Attribute with parameters.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 */
public interface ParameterizedAttribute extends AttributeAware {
    /**
     * Get the attributes.
     * 
     * @return the attributes or an empty map
     */
    Map<String, Parameter> getAttributes();

    /**
     * Get an attribute by name.
     * 
     * @param name attributes's name
     * @return the attribute
     */
    Parameter getAttribute(String name);

    /**
     * Get an attribute value
     * 
     * @param <T> the expected type
     * @param name the name of the attribute
     * @param type the expected type
     * @return the attribute value
     */
    <T> T getAttributeValue(String name, Class<T> type);

    /**
     * Get a directive value
     * 
     * @param <T> the expected type
     * @param name the name of the directive
     * @param defaultValue the default value when no attribute is specified
     * @param type the expected type
     * @return the attribute value
     */
    <T> T getAttributeValue(String name, T defaultValue, Class<T> type);

    /**
     * Get the declerations
     * 
     * @return the directives or an empty map
     */
    Map<String, Parameter> getDirectives();

    /**
     * Get a directive by name.
     * 
     * @param name directive's name
     * @return the directive
     */
    Parameter getDirective(String name);

    /**
     * Get a directive value
     * 
     * @param <T> the expected type
     * @param name the name of the directive
     * @param type the expected type
     * @return the directive value
     */
    <T> T getDirectiveValue(String name, Class<T> type);

    /**
     * Get a directive value
     * 
     * @param <T> the expected type
     * @param name the name of the directive
     * @param defaultValue the default value when no directive is specified
     * @param type the expected type
     * @return the directive value
     */
    <T> T getDirectiveValue(String name, T defaultValue, Class<T> type);
}

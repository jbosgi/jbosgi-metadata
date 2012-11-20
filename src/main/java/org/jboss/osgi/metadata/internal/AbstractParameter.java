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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.jboss.osgi.metadata.Parameter;

/**
 * Parameter impl. It uses [Hash]Set to hold the values. So duplicate values (by hash) will be ignored.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class AbstractParameter implements Parameter {

    private final Collection<String> values;
    private final ValueCreator<?> vc;

    public AbstractParameter(ValueCreator<?> vc) {
        this.values = new HashSet<String>();
        this.vc = vc;
    }

    public AbstractParameter(ValueCreator<?> vc, String parameter) {
        this.values = new LinkedHashSet<String>();
        this.vc = vc;
        addValue(parameter);
    }

    public void addValue(String value) {
        values.add(value);
    }

    public Object getValue() {
        if (values.isEmpty())
            return null;
        String result = values.toString();
        result = result.substring(1, result.length() - 1);
        return vc.createValue(result);
    }

    public boolean isCollection() {
        return values.size() > 1;
    }

    @Override
    public String toString() {
        return "[" + getValue() + "]";
    }
}

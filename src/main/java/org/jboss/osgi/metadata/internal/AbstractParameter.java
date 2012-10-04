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
import java.util.LinkedHashSet;

import org.jboss.osgi.metadata.Parameter;


/**
 * Parameter impl. It uses [Hash]Set to hold the values. So duplicate values (by hash) will be ignored.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
class AbstractParameter implements Parameter {
    protected Collection<String> values;

    public AbstractParameter() {
        values = new LinkedHashSet<String>();
    }

    public AbstractParameter(String parameter) {
        values = new LinkedHashSet<String>();
        addValue(parameter);
    }

    public void addValue(String value) {
        values.add(value);
    }

    public Object getValue() {
        if (values.isEmpty())
            return null;
        else if (values.size() == 1)
            return values.iterator().next();
        else
            return values;
    }

    public boolean isCollection() {
        return values.size() > 1;
    }

    @Override
    public String toString() {
        return "[" + getValue() + "]";
    }
}

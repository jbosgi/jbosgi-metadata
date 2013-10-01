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

import java.util.Collections;
import java.util.List;

/**
 * Create value list from string attribute.
 * 
 * @param <T> expected component type
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
abstract class ListValueCreator<T> extends AbstractValueCreator<List<T>> {

    protected ListValueCreator() {
        super();
    }

    protected ListValueCreator(boolean trim) {
        super(trim);
    }

    @Override
    public List<T> createValue(String attribute) {
        return attribute != null ? super.createValue(attribute) : Collections.<T>emptyList();
    }
}

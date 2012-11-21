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

import java.util.ArrayList;
import java.util.List;

import org.jboss.osgi.metadata.spi.ElementParser;

/**
 * Split string into list of strings.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
class StringListValueCreator extends ListValueCreator<String> {

    public StringListValueCreator() {
        super();
    }

    public StringListValueCreator(boolean trim) {
        super(trim);
    }

    public List<String> useString(String attribute) {
        List<String> parts = ElementParser.parseDelimitedString(attribute, ',', false);
        List<String> result = new ArrayList<String>(parts.size());
        StringValueCreator valueCreator = new StringValueCreator();
        for (String part : parts) {
            result.add(valueCreator.createValue(part));
        }
        return result;
    }
}

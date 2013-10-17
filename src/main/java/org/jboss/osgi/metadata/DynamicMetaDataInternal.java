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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;

import org.jboss.osgi.metadata.spi.AbstractOSGiMetaData;
import org.jboss.osgi.metadata.spi.NotNullException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * OSGi meta data that can constructed dynamically.
 *
 * This is needed for deployments that are not backed by a valid OSGi Manifest.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jun-2010
 */
class DynamicMetaDataInternal extends AbstractOSGiMetaData implements Externalizable {
    private Map<Name, String> attributes = new LinkedHashMap<Name, String>();

    DynamicMetaDataInternal(String symbolicName, Version version) {
        NotNullException.assertValue(symbolicName, "symbolicName");
        NotNullException.assertValue(version, "version");
        addMainAttribute(Constants.BUNDLE_MANIFESTVERSION, "2");
        addMainAttribute(Constants.BUNDLE_SYMBOLICNAME, symbolicName);
        addMainAttribute(Constants.BUNDLE_VERSION, version.toString());
    }

    void addMainAttribute(String key, String value) {
        attributes.put(new Name(key), value);
    }

    @Override
    public Map<Name, String> getMainAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public String getMainAttribute(String key) {
        return getMainAttributes().get(new Name(key));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        attributes = (Map<Name, String>) in.readObject();
    }
}

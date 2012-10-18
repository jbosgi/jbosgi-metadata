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

import static org.jboss.osgi.metadata.MetadataMessages.MESSAGES;

import java.io.Serializable;
import java.util.Map;

import org.jboss.osgi.metadata.PackageAttribute;
import org.jboss.osgi.metadata.Parameter;
import org.osgi.framework.Constants;
import org.osgi.framework.VersionRange;

/**
 * Package attribute impl.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 */
public class AbstractPackageAttribute extends AbstractParameterizedAttribute implements PackageAttribute, Serializable {
    private static final long serialVersionUID = 1l;

    protected VersionRange versionRange;

    public AbstractPackageAttribute(String packageName, Map<String, Parameter> attributes, Map<String, Parameter> directives) {
        super(packageName, attributes, directives);
    }

    @Override
    public String getPackageName() {
        return getAttribute();
    }

    public VersionRange getVersion() {
        if (versionRange == null) {
            Parameter parameter = getAttribute(Constants.VERSION_ATTRIBUTE);
            if (parameter != null) {
                if (parameter.isCollection())
                    throw MESSAGES.illegalArgumentDuplicateVersionParameter();
                Object value = parameter.getValue();
                if (value != null) {
                    versionRange = new VersionRange(value.toString());
                }
            }
        }
        return versionRange;
    }
}

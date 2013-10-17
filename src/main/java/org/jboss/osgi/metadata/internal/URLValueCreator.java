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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Create URL from string.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 */
class URLValueCreator extends AbstractValueCreator<URL> {

    static final Logger LOGGER = Logger.getLogger(URLValueCreator.class.getName());

    public URLValueCreator() {
        super();
    }

    public URLValueCreator(boolean trim) {
        super(trim);
    }

    public URL useString(String attribute) {
        try {
            return new URL(attribute);
        } catch (MalformedURLException ex) {
            LOGGER.warning("Cannot create URL from: " + attribute);
            return null;
        }
    }
}

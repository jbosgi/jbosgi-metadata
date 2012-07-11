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

import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Logger;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;
import org.jboss.osgi.metadata.Parameter;

/**
 * Logging Id ranges: 10600-10699
 *
 * https://docs.jboss.org/author/display/JBOSGI/JBossOSGi+Logging
 *
 * @author Thomas.Diesler@jboss.com
 */
@MessageLogger(projectCode = "JBOSGI")
public interface MetadataLogger extends BasicLogger {

    MetadataLogger LOGGER = Logger.getMessageLogger(MetadataLogger.class, "org.jboss.osgi.metadata");

    @LogMessage(level = WARN)
    @Message(id = 10600, value = "Cannot create value from %s for parameter: %s")
    void warnCannotCreateValueForParameter(ValueCreator<?> creator, Parameter parameter);

    @LogMessage(level = WARN)
    @Message(id = 10601, value = "Cannot create URL from: %s")
    void warnCannotCreateURL(String attribute);

}

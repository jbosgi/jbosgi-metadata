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

import java.util.Dictionary;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.osgi.framework.BundleException;

/**
 * Logging Id ranges: 10700-10799
 *
 * https://docs.jboss.org/author/display/JBOSGI/JBossOSGi+Logging
 *
 * @author Thomas.Diesler@jboss.com
 */
@MessageBundle(projectCode = "JBOSGI")
public interface MetadataMessages {

    MetadataMessages MESSAGES = Messages.getBundle(MetadataMessages.class);

    @Message(id = 10700, value = "%s is null")
    IllegalArgumentException illegalArgumentNull(String name);

    @Message(id = 10701, value = "Duplicate version parameter")
    IllegalArgumentException illegalArgumentDuplicateVersionParameter();

    @Message(id = 10702, value = "Duplicate attribute: %s")
    IllegalArgumentException illegalArgumentDuplicateAttribute(String name);

    @Message(id = 10703, value = "Duplicate directive: %s")
    IllegalArgumentException illegalArgumentDuplicateDirective(String name);

    @Message(id = 10704, value = "Duplicates with varying case for key [%s] : %s")
    IllegalArgumentException illegalArgumentDuplicatesForKey(String name, Dictionary<?,?> delegate);

    @Message(id = 10705, value = "No paths for clause: %s")
    IllegalArgumentException illegalArgumentNoPathsForClause(String clause);

    @Message(id = 10706, value = "Path [%s] should appear before attributes and directives in clause: %s")
    IllegalArgumentException illegalArgumentPathShouldAppearBefore(String path, String clause);

    @Message(id = 10707, value = "Invalid delimited string [%s] for delimiter: %s")
    IllegalArgumentException illegalArgumentInvalidDelimitedString(String value, char delim);

    @Message(id = 10708, value = "Cannot determine Bundle-ManifestVersion")
    BundleException bundleCannotObtainBundleManifestVersion();

    @Message(id = 10709, value = "Unsupported Bundle-ManifestVersion: %d")
    BundleException bundleUnsupportedBundleManifestVersion(int version);

    @Message(id = 10710, value = "Invalid Bundle-ManifestVersion for: %s")
    BundleException bundleInvalidBundleManifestVersion(String symbolicName);

    @Message(id = 10711, value = "Cannot obtain Bundle-SymbolicName")
    BundleException bundleCannotObtainBundleSymbolicName();

    @Message(id = 10712, value = "Invalid OSGi metadata")
    BundleException bundleInvalidMetadata(@Cause Throwable cause);

    @Message(id = 10713, value = "Cannot create manifest")
    IllegalStateException illegalStateCannotCreateManifest(@Cause Throwable cause);

    @Message(id = 10714, value = "Cannot provide manifest input stream")
    IllegalStateException illegalStateCannotProvideManifestInputStream(@Cause Throwable cause);

    @Message(id = 10715, value = "Cannot append to already existing manifest")
    IllegalStateException illegalStateCannotAppendToExistingManifest();
}

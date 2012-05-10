/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.osgi.metadata.internal;

import java.util.Dictionary;

import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;
import org.jboss.logging.Messages;
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
    IllegalArgumentException illegalArgumentInvalidDelimitedString(String value, String delim);

    @Message(id = 10708, value = "Cannot determine Bundle-ManifestVersion")
    BundleException bundleCannotObtainBundleManifestVersion();

    @Message(id = 10709, value = "Unsupported Bundle-ManifestVersion: %d")
    BundleException bundleUnsupportedBundleManifestVersion(int version);

    @Message(id = 10710, value = "Invalid Bundle-ManifestVersion for: %s")
    BundleException bundleInvalidBundleManifestVersion(String symbolicName);

    @Message(id = 10711, value = "Cannot obtain Bundle-SymbolicName")
    BundleException bundleCannotObtainBundleSymbolicName();
}

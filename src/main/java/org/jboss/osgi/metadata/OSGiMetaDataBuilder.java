/*
 * #%L
 * JBossOSGi Resolver Metadata
 * %%
 * Copyright (C) 2010 - 2012 JBoss by Red Hat
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.osgi.metadata;

import static org.jboss.osgi.metadata.internal.MetadataMessages.MESSAGES;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * A builder for {@link OSGiMetaData}.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jun-2010
 */
public class OSGiMetaDataBuilder {
    private DynamicOSGiMetaData metadata;
    private List<String> importPackages = new ArrayList<String>();
    private List<String> exportPackages = new ArrayList<String>();
    private List<String> requiredBundles = new ArrayList<String>();
    private List<String> dynamicImportPackages = new ArrayList<String>();

    public static OSGiMetaDataBuilder createBuilder(String symbolicName) {
        if (symbolicName == null)
            throw MESSAGES.illegalArgumentNull("symbolicName");
        return new OSGiMetaDataBuilder(symbolicName, Version.emptyVersion);
    }

    public static OSGiMetaDataBuilder createBuilder(String symbolicName, Version version) {
        if (symbolicName == null)
            throw MESSAGES.illegalArgumentNull("symbolicName");
        if (version == null)
            throw MESSAGES.illegalArgumentNull("version");
        return new OSGiMetaDataBuilder(symbolicName, version);
    }

    public static OSGiMetaDataBuilder createBuilder(Dictionary<String, String> headers) {
        if (headers == null)
            throw MESSAGES.illegalArgumentNull("headers");
        String symbolicName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
        Version version = Version.parseVersion(headers.get(Constants.BUNDLE_VERSION));
        OSGiMetaDataBuilder builder = new OSGiMetaDataBuilder(symbolicName, version);
        Enumeration<String> keys = headers.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = headers.get(key);
            builder.addMainAttribute(key, value);
        }
        return builder;
    }

    public static OSGiMetaData load(Properties props) {
        if (props == null)
            throw MESSAGES.illegalArgumentNull("props");
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        for (Object key : props.keySet()) {
            Attributes.Name name = new Attributes.Name((String) key);
            mainAttributes.put(name, props.get(key));
        }
        return load(manifest);
    }

    public static OSGiMetaData load(Manifest manifest) {
        if (manifest == null)
            throw MESSAGES.illegalArgumentNull("manifest");
        return new OSGiManifestMetaData(manifest);
    }

    /**
     * Validate a given OSGi metadata.
     *
     * @param metadata The given metadata
     * @return True if the metadata is valid
     */
    public static boolean isValidMetadata(OSGiMetaData metadata) {
        if (metadata == null)
            return false;
        try {
            validateMetadata(metadata);
            return true;
        } catch (BundleException e) {
            return false;
        }
    }

    /**
     * Validate a given OSGi metadata.
     *
     * @param metadata The given metadata
     * @throws BundleException if the given metadata is not a valid
     */
    public static void validateMetadata(OSGiMetaData metadata) throws BundleException {
        if (metadata == null)
            throw MESSAGES.illegalArgumentNull("metadata");

        // A bundle manifest must express the version of the OSGi manifest header
        // syntax in the Bundle-ManifestVersion header. Bundles exploiting this version
        // of the Framework specification (or later) must specify this header.
        // The Framework version 1.3 (or later) bundle manifest version must be ’2’.
        // Bundle manifests written to previous specifications’ manifest syntax are
        // taken to have a bundle manifest version of '1', although there is no way to
        // express this in such manifests.
        int manifestVersion = getBundleManifestVersion(metadata);
        if (manifestVersion < 0)
            throw MESSAGES.bundleCannotObtainBundleManifestVersion();
        if (manifestVersion > 2)
            throw MESSAGES.bundleUnsupportedBundleManifestVersion(manifestVersion);

        // R3 Framework
        String symbolicName = metadata.getBundleSymbolicName();
        if (manifestVersion == 1 && symbolicName != null)
            throw MESSAGES.bundleInvalidBundleManifestVersion(symbolicName);

        // R4 Framework
        if (manifestVersion == 2 && symbolicName == null)
            throw MESSAGES.bundleCannotObtainBundleSymbolicName();

    }

    private static int getBundleManifestVersion(OSGiMetaData metaData) {

        // At least one of these manifest headers must be there
        // Note, in R3 and R4 there is no common mandatory header
        String bundleName = metaData.getBundleName();
        String bundleSymbolicName = metaData.getBundleSymbolicName();
        Version bundleVersion = metaData.getBundleVersion();

        if (bundleName == null && bundleSymbolicName == null && bundleVersion.equals(Version.emptyVersion))
            return -1;

        Integer manifestVersion = metaData.getBundleManifestVersion();
        return manifestVersion != null ? manifestVersion : 1;
    }

    private OSGiMetaDataBuilder(String symbolicName, Version version) {
        metadata = new DynamicOSGiMetaData(symbolicName, version);
    }

    public OSGiMetaDataBuilder setBundleManifestVersion(int version) {
        metadata.addMainAttribute(Constants.BUNDLE_MANIFESTVERSION, "" + version);
        return this;
    }

    public OSGiMetaDataBuilder setBundleActivator(String value) {
        metadata.addMainAttribute(Constants.BUNDLE_ACTIVATOR, value);
        return this;
    }

    public OSGiMetaDataBuilder addImportPackages(Class<?>... packages) {
        for (Class<?> aux : packages) {
            addImportPackages(aux.getPackage().getName());
        }
        return this;
    }

    public OSGiMetaDataBuilder addImportPackages(String... packages) {
        for (String aux : packages) {
            importPackages.add(aux);
        }
        return this;
    }

    public OSGiMetaDataBuilder addExportPackages(Class<?>... packages) {
        for (Class<?> aux : packages) {
            addExportPackages(aux.getPackage().getName());
        }
        return this;
    }

    public OSGiMetaDataBuilder addExportPackages(String... packages) {
        for (String aux : packages) {
            exportPackages.add(aux);
        }
        return this;
    }

    public OSGiMetaDataBuilder addDynamicImportPackages(Class<?>... packages) {
        for (Class<?> aux : packages) {
            addDynamicImportPackages(aux.getPackage().getName());
        }
        return this;
    }

    public OSGiMetaDataBuilder addDynamicImportPackages(String... packages) {
        for (String aux : packages) {
            dynamicImportPackages.add(aux);
        }
        return this;
    }

    public OSGiMetaDataBuilder addRequiredBundles(String... required) {
        for (String aux : required) {
            requiredBundles.add(aux);
        }
        return this;
    }

    public OSGiMetaDataBuilder addMainAttribute(String key, String value) {
        metadata.addMainAttribute(key, value);
        return this;
    }

    public OSGiMetaData getAndValidateMetaData() throws BundleException {
        OSGiMetaData metadata = getMetaDataInternal();
        validateMetadata(metadata);
        return metadata;
    }

    private OSGiMetaData getMetaDataInternal() {
        // Export-Package
        if (exportPackages.size() > 0) {
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < exportPackages.size(); i++) {
                value.append(i > 0 ? "," : "");
                value.append(exportPackages.get(i));
            }
            metadata.addMainAttribute(Constants.EXPORT_PACKAGE, value.toString());
        }

        // Import-Package
        if (importPackages.size() > 0) {
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < importPackages.size(); i++) {
                value.append(i > 0 ? "," : "");
                value.append(importPackages.get(i));
            }
            metadata.addMainAttribute(Constants.IMPORT_PACKAGE, value.toString());
        }

        // Require-Bundle
        if (requiredBundles.size() > 0) {
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < requiredBundles.size(); i++) {
                value.append(i > 0 ? "," : "");
                value.append(requiredBundles.get(i));
            }
            metadata.addMainAttribute(Constants.REQUIRE_BUNDLE, value.toString());
        }

        // DynamicImport-Package
        if (dynamicImportPackages.size() > 0) {
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < dynamicImportPackages.size(); i++) {
                value.append(i > 0 ? "," : "");
                value.append(dynamicImportPackages.get(i));
            }
            metadata.addMainAttribute(Constants.DYNAMICIMPORT_PACKAGE, value.toString());
        }
        return metadata;
    }

    public OSGiMetaData getOSGiMetaData() {
        return getMetaDataInternal();
    }
}

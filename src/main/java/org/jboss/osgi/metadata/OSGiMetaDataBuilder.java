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

import static org.jboss.osgi.metadata.MetadataLogger.LOGGER;
import static org.jboss.osgi.metadata.MetadataMessages.MESSAGES;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public final class OSGiMetaDataBuilder {
    private DynamicMetaDataInternal metadata;
    private Map<String, String> importPackages = new LinkedHashMap<String, String>();
    private Map<String, String> exportPackages = new LinkedHashMap<String, String>();
    private Map<String, String> requiredBundles = new LinkedHashMap<String, String>();
    private Map<String, String> dynamicImportPackages = new LinkedHashMap<String, String>();
    private final List<String> providedCapabilities = new ArrayList<String>();
    private final List<String> requiredCapabilities = new ArrayList<String>();

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
        return new ManifestMetaDataInternal(manifest);
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
        try {
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
        } catch (RuntimeException ex) {
            throw MESSAGES.bundleInvalidMetadata(ex);
        }
    }

    private static int getBundleManifestVersion(OSGiMetaData metaData) {

        // At least one of these manifest headers must be there
        // Note, in R3 and R4 there is no common mandatory header
        String bundleName = metaData.getBundleName();
        String bundleSymbolicName = metaData.getBundleSymbolicName();
        Version bundleVersion = null;
		try {
			bundleVersion = metaData.getBundleVersion();
		} catch (IllegalArgumentException ex) {
			// ignore
		}

        if (bundleName == null && bundleSymbolicName == null && bundleVersion == null)
            return -1;

        Integer manifestVersion = metaData.getBundleManifestVersion();
        return manifestVersion != null ? manifestVersion : 1;
    }

    private OSGiMetaDataBuilder(String symbolicName, Version version) {
        metadata = new DynamicMetaDataInternal(symbolicName, version);
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
            addEntry(importPackages, aux);
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
            addEntry(exportPackages, aux);
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
            addEntry(dynamicImportPackages, aux);
        }
        return this;
    }

    public OSGiMetaDataBuilder addRequiredBundles(String... required) {
        for (String aux : required) {
            addEntry(requiredBundles, aux);
        }
        return this;
    }

    public OSGiMetaDataBuilder addProvidedCapabilities(String... capabilities) {
        for (String entry : capabilities) {
            providedCapabilities.add(entry);
        }
        return this;
    }

    public OSGiMetaDataBuilder addRequiredCapabilities(String... capabilities) {
        for (String entry : capabilities) {
            requiredCapabilities.add(entry);
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

    // Strip attributes/directives to avoid duplicates
    private void addEntry(Map<String, String> target, String entry) {
        String key = entry;
        int index = entry.indexOf(";");
        if (index > 0) {
            key = entry.substring(0, index);
        }
        if (target.get(key) == null) {
            target.put(key, entry);
        } else {
            LOGGER.warnIgnoreDuplicateEntry(entry);
        }
    }

    private OSGiMetaData getMetaDataInternal() {
        addManifestHeader(Constants.EXPORT_PACKAGE, exportPackages);
        addManifestHeader(Constants.IMPORT_PACKAGE, importPackages);
        addManifestHeader(Constants.REQUIRE_BUNDLE, requiredBundles);
        addManifestHeader(Constants.DYNAMICIMPORT_PACKAGE, dynamicImportPackages);
        addManifestHeader(Constants.PROVIDE_CAPABILITY, providedCapabilities);
        addManifestHeader(Constants.REQUIRE_CAPABILITY, requiredCapabilities);
        return metadata;
    }

    private void addManifestHeader(String header, Map<String, String> source) {
        if (source.size() > 0) {
            int i = 0;
            StringBuffer buffer = new StringBuffer();
            for (String entry : source.values()) {
                buffer.append(i++ > 0 ? "," : "");
                buffer.append(entry);
            }
            metadata.addMainAttribute(header, buffer.toString());
        }
    }

    private void addManifestHeader(String header, List<String> source) {
        if (source.size() > 0) {
            int i = 0;
            StringBuffer buffer = new StringBuffer();
            for (String entry : source) {
                buffer.append(i++ > 0 ? "," : "");
                buffer.append(entry);
            }
            metadata.addMainAttribute(header, buffer.toString());
        }
    }
    
    public OSGiMetaData getOSGiMetaData() {
        return getMetaDataInternal();
    }
}

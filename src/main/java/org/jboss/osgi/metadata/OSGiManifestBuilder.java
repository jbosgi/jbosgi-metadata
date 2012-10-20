/*
 * #%L
 * JBossOSGi SPI
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

import static org.jboss.osgi.metadata.MetadataLogger.LOGGER;
import static org.jboss.osgi.metadata.MetadataMessages.MESSAGES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

/**
 * A simple OSGi manifest builder.
 *
 * @author thomas.diesler@jboss.com
 * @since 08-Mar-2010
 */
public final class OSGiManifestBuilder implements Asset {

    private final ManifestBuilder delegate = ManifestBuilder.newInstance();
    private final Map<String, String> importPackages = new LinkedHashMap<String, String>();
    private final Map<String, String> exportPackages = new LinkedHashMap<String, String>();
    private final Map<String, String> dynamicImportPackages = new LinkedHashMap<String, String>();
    private final Map<String, String> requiredBundles = new LinkedHashMap<String, String>();
    private final Map<String, String> requiredEnvironments = new LinkedHashMap<String, String>();
    private final Map<String, String> providedCapabilities = new LinkedHashMap<String, String>();
    private final Map<String, String> requiredCapabilities = new LinkedHashMap<String, String>();
    private Manifest manifest;

    public static OSGiManifestBuilder newInstance() {
        return new OSGiManifestBuilder();
    }

    private OSGiManifestBuilder() {
    }

    public OSGiManifestBuilder addBundleManifestVersion(int version) {
        delegate.append(Constants.BUNDLE_MANIFESTVERSION + ": " + version);
        return this;
    }

    public OSGiManifestBuilder addBundleSymbolicName(String symbolicName) {
        delegate.append(Constants.BUNDLE_SYMBOLICNAME + ": " + symbolicName);
        return this;
    }

    public OSGiManifestBuilder addBundleName(String name) {
        delegate.append(Constants.BUNDLE_NAME + ": " + name);
        return this;
    }

    public OSGiManifestBuilder addBundleVersion(Version version) {
        delegate.append(Constants.BUNDLE_VERSION + ": " + version);
        return this;
    }

    public OSGiManifestBuilder addBundleVersion(String version) {
        return addBundleVersion(Version.parseVersion(version));
    }

    public OSGiManifestBuilder addBundleActivator(Class<?> bundleActivator) {
        return addBundleActivator(bundleActivator.getName());
    }

    public OSGiManifestBuilder addBundleActivator(String bundleActivator) {
        delegate.append(Constants.BUNDLE_ACTIVATOR + ": " + bundleActivator);
        return this;
    }

    public OSGiManifestBuilder addBundleActivationPolicy(String activationPolicy) {
        delegate.append(Constants.BUNDLE_ACTIVATIONPOLICY + ": " + activationPolicy);
        return this;
    }

    public OSGiManifestBuilder addBundleClasspath(String classpath) {
        delegate.append(Constants.BUNDLE_CLASSPATH + ": " + classpath);
        return this;
    }

    public OSGiManifestBuilder addFragmentHost(String fragmentHost) {
        delegate.append(Constants.FRAGMENT_HOST + ": " + fragmentHost);
        return this;
    }

    public OSGiManifestBuilder addFragmentHost(String fragmentHost, VersionRange version) {
        String bundleSpec = fragmentHost;
        if (version != null) {
            bundleSpec += ";bundle-version=\"" + version + "\"";
        }
        delegate.append(Constants.FRAGMENT_HOST + ": " + bundleSpec);
        return this;
    }

    public OSGiManifestBuilder addManifestHeader(String key, String value) {
        delegate.addManifestHeader(key, value);
        return this;
    }

    public OSGiManifestBuilder addRequireBundle(String requiredBundle) {
        return addRequireBundle(requiredBundle, null);
    }

    public OSGiManifestBuilder addRequireBundle(String requiredBundle, VersionRange version) {
        String entry = requiredBundle;
        if (version != null) {
            entry += ";bundle-version=\"" + version + "\"";
        }
        addEntry(requiredBundles, entry);
        return this;
    }

    public OSGiManifestBuilder addRequireExecutionEnvironment(String... environments) {
        for (String entry : environments) {
            addEntry(requiredEnvironments, entry);
        }
        return this;
    }

    public OSGiManifestBuilder addImportPackages(Class<?>... packages) {
        for (Class<?> aux : packages) {
            addImportPackage(aux.getPackage(), null);
        }
        return this;
    }

    public OSGiManifestBuilder addImportPackages(Package... packages) {
        for (Package aux : packages) {
            addImportPackage(aux, null);
        }
        return this;
    }

    public OSGiManifestBuilder addImportPackages(String... packages) {
        for (String aux : packages) {
            addImportPackage(aux);
        }
        return this;
    }

    public OSGiManifestBuilder addImportPackage(Class<?> imported, VersionRange version) {
        return addImportPackage(imported.getPackage(), version);
    }

    public OSGiManifestBuilder addImportPackage(Package imported, VersionRange version) {
        return addImportPackage(imported.getName(), version);
    }

    public OSGiManifestBuilder addImportPackage(String packageName, VersionRange version) {
        String packageSpec = packageName;
        if (version != null) {
            packageSpec += ";version=\"" + version + "\"";
        }
        addImportPackage(packageSpec);
        return this;
    }

    public OSGiManifestBuilder addImportPackage(String packageSpec) {
        addEntry(importPackages, packageSpec);
        return this;
    }

    public OSGiManifestBuilder addDynamicImportPackages(Class<?>... imported) {
        for (Class<?> clazz : imported) {
            addDynamicImportPackages(clazz.getPackage());
        }
        return this;
    }
    
    public OSGiManifestBuilder addDynamicImportPackages(Package... imported) {
        for (Package aux : imported) {
            addDynamicImportPackage(aux.getName());
        }
        return this;
    }
    
    public OSGiManifestBuilder addDynamicImportPackages(String... imported) {
        for (String entry : imported) {
            addDynamicImportPackage(entry);
        }
        return this;
    }

    public OSGiManifestBuilder addDynamicImportPackage(String imported) {
        addEntry(dynamicImportPackages, imported);
        return this;
    }

    public OSGiManifestBuilder addExportPackages(Class<?>... packages) {
        for (Class<?> aux : packages) {
            addExportPackage(aux.getPackage(), null);
        }
        return this;
    }

    public OSGiManifestBuilder addExportPackages(Package... packages) {
        for (Package aux : packages) {
            addExportPackage(aux, null);
        }
        return this;
    }

    public OSGiManifestBuilder addExportPackages(String... packages) {
        for (String aux : packages) {
            addExportPackage(aux);
        }
        return this;
    }

    public OSGiManifestBuilder addExportPackage(Class<?> exported, Version version) {
        return addExportPackage(exported.getPackage(), version);
    }

    public OSGiManifestBuilder addExportPackage(Package exported, Version version) {
        return addExportPackage(exported.getName(), version);
    }

    public OSGiManifestBuilder addExportPackage(String packageName, Version version) {
        String packageSpec = packageName;
        if (version != null) {
            packageSpec += ";version=" + version;
        }
        addExportPackage(packageSpec);
        return this;
    }

    public OSGiManifestBuilder addExportPackage(String packageSpec) {
        addEntry(exportPackages, packageSpec);
        return this;
    }

    public OSGiManifestBuilder addProvidedCapabilities(String... capabilities) {
        for (String entry : capabilities) {
            addEntry(providedCapabilities, entry);
        }
        return this;
    }

    public OSGiManifestBuilder addRequiredCapabilities(String... capabilities) {
        for (String entry : capabilities) {
            addEntry(requiredCapabilities, entry);
        }
        return this;
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

    @SuppressWarnings("deprecation")
    public Manifest getManifest() {
        if (manifest == null) {
            addManifestHeader(Constants.REQUIRE_BUNDLE, requiredBundles);
            addManifestHeader(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, requiredEnvironments);
            addManifestHeader(Constants.EXPORT_PACKAGE, exportPackages);
            addManifestHeader(Constants.IMPORT_PACKAGE, importPackages);
            addManifestHeader(Constants.DYNAMICIMPORT_PACKAGE, dynamicImportPackages);
            addManifestHeader(Constants.PROVIDE_CAPABILITY, providedCapabilities);
            addManifestHeader(Constants.REQUIRE_CAPABILITY, requiredCapabilities);
            Manifest aux = delegate.getManifest();
            try {
                validateBundleManifest(aux);
            } catch (BundleException ex) {
                throw new IllegalStateException(ex);
            }
            manifest = aux;
        }
        return manifest;
    }

    private void addManifestHeader(String header, Map<String, String> source) {
        if (source.size() > 0) {
            int i = 0;
            StringBuffer buffer = new StringBuffer();
            for (String entry : source.values()) {
                buffer.append(i++ > 0 ? "," : "");
                buffer.append(entry);
            }
            delegate.append(header + ": " + buffer);
        }
    }

    /**
     * Validate a given bundle manifest.
     *
     * @param manifest The given manifest
     * @return True if the manifest is valid
     */
    public static boolean isValidBundleManifest(Manifest manifest) {
        if (manifest == null)
            return false;

        try {
            validateBundleManifest(manifest);
            return true;
        } catch (BundleException e) {
            return false;
        }
    }

    /**
     * Validate a given manifest.
     *
     * @param manifest The given manifest
     * @throws BundleException if the given manifest is not a valid OSGi manifest
     */
    public static void validateBundleManifest(Manifest manifest) throws BundleException {
        if (manifest == null)
            MESSAGES.illegalArgumentNull("manifest");

        // A bundle manifest must express the version of the OSGi manifest header
        // syntax in the Bundle-ManifestVersion header. Bundles exploiting this version
        // of the Framework specification (or later) must specify this header.
        // The Framework version 1.3 (or later) bundle manifest version must be ’2’.
        // Bundle manifests written to previous specifications’ manifest syntax are
        // taken to have a bundle manifest version of '1', although there is no way to
        // express this in such manifests.
        int manifestVersion = getBundleManifestVersion(manifest);
        if (manifestVersion < 0)
            throw MESSAGES.bundleCannotObtainBundleManifestVersion();
        if (manifestVersion > 2)
            throw MESSAGES.bundleUnsupportedBundleManifestVersion(manifestVersion);

        // R3 Framework
        String symbolicName = getManifestHeaderInternal(manifest, Constants.BUNDLE_SYMBOLICNAME);
        if (manifestVersion == 1 && symbolicName != null)
            throw MESSAGES.bundleInvalidBundleManifestVersion(symbolicName);

        // R4 Framework
        if (manifestVersion == 2 && symbolicName == null)
            throw MESSAGES.bundleCannotObtainBundleSymbolicName();

    }

    /**
     * Get the bundle manifest version.
     *
     * @param manifest The given manifest
     * @return The value of the Bundle-ManifestVersion header, or -1 for a non OSGi manifest
     */
    public static int getBundleManifestVersion(Manifest manifest) {
        if (manifest == null)
            throw MESSAGES.illegalArgumentNull("manifest");

        String manifestVersion = getManifestHeaderInternal(manifest, Constants.BUNDLE_MANIFESTVERSION);
        if (manifestVersion != null) {
            return Integer.parseInt(manifestVersion);
        }

        // At least one of these manifest headers must be there
        // Note, in R3 and R4 there is no common mandatory header
        String bundleName = getManifestHeaderInternal(manifest, Constants.BUNDLE_NAME);
        String bundleSymbolicName = getManifestHeaderInternal(manifest, Constants.BUNDLE_SYMBOLICNAME);
        String bundleVersion = getManifestHeaderInternal(manifest, Constants.BUNDLE_VERSION);

        if (bundleName == null && bundleSymbolicName == null && bundleVersion == null)
            return -1;

        return 1;
    }

    @Override
    public InputStream openStream() {
        Manifest manifest = getManifest();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            manifest.write(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException ex) {
            throw MESSAGES.illegalStateCannotProvideManifestInputStream(ex);
        }
    }

    private static String getManifestHeaderInternal(Manifest manifest, String key) {
        Attributes attribs = manifest.getMainAttributes();
        String value = attribs.getValue(key);
        return value != null ? value.trim() : null;
    }
}

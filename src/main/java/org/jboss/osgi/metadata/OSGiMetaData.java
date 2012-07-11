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

import java.net.URL;
import java.util.Dictionary;
import java.util.List;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * OSGi specific meta data.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author thomas.diesler@jboss.com
 */
public interface OSGiMetaData {

    /** The fallback {@link Constants#BUNDLE_SYMBOLICNAME} for a v4.1 bundle */
    final String ANONYMOUS_BUNDLE_SYMBOLIC_NAME = "anonymous-bundle-symbolic-name";

    /**
     * Get the headers in raw unlocalized format.
     *
     * @return the headers
     */
    Dictionary<String, String> getHeaders();

    /**
     * Extension point to read custom manifest headers.
     *
     * @param key the header key
     * @return value or null of no such header
     */
    String getHeader(String key);

    /**
     * Get bundle activation policy.
     *
     * @return bundle activation policy
     */
    ActivationPolicyMetaData getBundleActivationPolicy();

    /**
     * Get bundle activator class name.
     *
     * @return bundle activator classname or null if no such attribute
     */
    String getBundleActivator();

    /**
     * Get the bundle category
     *
     * @return list of category names
     */
    List<String> getBundleCategory();

    /**
     * Get the bundle classpath
     *
     * @return list of JAR file path names or directories inside bundle
     */
    List<String> getBundleClassPath();

    /**
     * Get the description
     *
     * @return a description
     */
    String getBundleDescription();

    /**
     * Get the localization's location
     *
     * @return location in the bundle for localization files
     */
    String getBundleLocalization();

    /**
     * Get the bundle manifest version
     *
     * @return bundle's manifest version
     */
    int getBundleManifestVersion();

    /**
     * Get the name
     *
     * @return readable name
     */
    String getBundleName();

    /**
     * Get native code libs
     *
     * @return native libs contained in the bundle
     */
    List<ParameterizedAttribute> getBundleNativeCode();

    /**
     * Get required exectuion envs
     *
     * @return list of execution envs that must be present on the Service Platform
     */
    List<String> getRequiredExecutionEnvironment();

    /**
     * Get bundle symbolic name.
     *
     * @return bundle's symbolic name
     */
    String getBundleSymbolicName();

    /**
     * Get the bundle parameters
     *
     * @return the bundle parameters
     */
    ParameterizedAttribute getBundleParameters();

    /**
     * Get the update url.
     *
     * @return URL of an update bundle location
     */
    URL getBundleUpdateLocation();

    /**
     * Get bundle's version.
     *
     * Note, R3 does not define a specific syntax for Bundle-Version.
     *
     * @return version of this bundle
     */
    Version getBundleVersion();

    /**
     * Get dynamic imports.
     *
     * @return package names that should be dynamically imported when needed
     */
    List<PackageAttribute> getDynamicImports();

    /**
     * Get the export packages.
     *
     * @return exported packages
     */
    List<PackageAttribute> getExportPackages();

    /**
     * Get the fragment host.
     *
     * @return host bundle for this fragment
     */
    ParameterizedAttribute getFragmentHost();

    /**
     * Get the import packages.
     *
     * @return imported packages.
     */
    List<PackageAttribute> getImportPackages();

    /**
     * Get the required exports
     *
     * @return required bundles
     */
    List<ParameterizedAttribute> getRequireBundles();

    /**
     * Get the provided capabilities
     *
     * @return provided capabilities
     */
    List<ParameterizedAttribute> getProvidedCapabilities();

    /**
     * Get the required capabilities
     *
     * @return required capabilities
     */
    List<ParameterizedAttribute> getRequiredCapabilities();

    /**
     * Whether the bundle is a singleton
     *
     * @return true when it is a singleton
     */
    boolean isSingleton();

    /**
     * Whether the bundle is a fragment
     *
     * @return true when it is a fragment
     */
    boolean isFragment();

    /**
     * Validate the this metadata
     */
    OSGiMetaData validate() throws BundleException;
}

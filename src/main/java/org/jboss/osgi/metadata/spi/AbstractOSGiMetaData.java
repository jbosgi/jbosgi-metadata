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
package org.jboss.osgi.metadata.spi;

import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.ACTIVATION_POLICY_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.INTEGER_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.NATIVE_CODE_ATTRIB_LIST_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.PACKAGE_LIST_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.PARAM_ATTRIB_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.QNAME_ATTRIB_LIST_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.STRING_LIST_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.STRING_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.URL_VC;
import static org.jboss.osgi.metadata.internal.ValueCreatorUtil.VERSION_VC;
import static org.osgi.framework.Constants.BUNDLE_ACTIVATIONPOLICY;
import static org.osgi.framework.Constants.BUNDLE_ACTIVATOR;
import static org.osgi.framework.Constants.BUNDLE_CATEGORY;
import static org.osgi.framework.Constants.BUNDLE_CLASSPATH;
import static org.osgi.framework.Constants.BUNDLE_DESCRIPTION;
import static org.osgi.framework.Constants.BUNDLE_LOCALIZATION;
import static org.osgi.framework.Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_NAME;
import static org.osgi.framework.Constants.BUNDLE_NATIVECODE;
import static org.osgi.framework.Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_UPDATELOCATION;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.DYNAMICIMPORT_PACKAGE;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.FRAGMENT_HOST;
import static org.osgi.framework.Constants.IMPORT_PACKAGE;
import static org.osgi.framework.Constants.PROVIDE_CAPABILITY;
import static org.osgi.framework.Constants.REQUIRE_BUNDLE;
import static org.osgi.framework.Constants.REQUIRE_CAPABILITY;

import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes.Name;

import org.jboss.osgi.metadata.ActivationPolicyMetaData;
import org.jboss.osgi.metadata.CaseInsensitiveDictionary;
import org.jboss.osgi.metadata.OSGiMetaData;
import org.jboss.osgi.metadata.OSGiMetaDataBuilder;
import org.jboss.osgi.metadata.PackageAttribute;
import org.jboss.osgi.metadata.ParameterizedAttribute;
import org.jboss.osgi.metadata.internal.ValueCreator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * Abstract OSGi meta data.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 * @author David Bosschaert
 */
public abstract class AbstractOSGiMetaData implements OSGiMetaData {

    // The cached attributes
    protected transient Map<String, Object> cachedAttributes = new ConcurrentHashMap<String, Object>();
    // The parameters on the Bundle-SymbolicName
    protected transient ParameterizedAttribute parameters;

    protected abstract Map<Name, String> getMainAttributes();

    protected abstract String getMainAttribute(String key);

    @Override
    @SuppressWarnings({ "unchecked" })
    public Dictionary<String, String> getHeaders() {
        Map<Name, String> attributes = getMainAttributes();
        Dictionary<String, String> result = new Hashtable<String, String>();
        for (Entry<Name, String> entry : attributes.entrySet())
            result.put(entry.getKey().toString(), entry.getValue());

        return new CaseInsensitiveDictionary(result);
    }

    @Override
    public String getHeader(String key) {
        return get(key, STRING_VC);
    }

    @Override
    public ActivationPolicyMetaData getBundleActivationPolicy() {
        return get(BUNDLE_ACTIVATIONPOLICY, ACTIVATION_POLICY_VC);
    }

    @Override
    public String getBundleActivator() {
        return get(BUNDLE_ACTIVATOR, STRING_VC);
    }

    @Override
    public List<String> getBundleCategory() {
        return get(BUNDLE_CATEGORY, STRING_LIST_VC);
    }

    @Override
    public List<String> getBundleClassPath() {
        return get(BUNDLE_CLASSPATH, STRING_LIST_VC);
    }

    @Override
    public String getBundleDescription() {
        return get(BUNDLE_DESCRIPTION, STRING_VC);
    }

    @Override
    public String getBundleLocalization() {
        return get(BUNDLE_LOCALIZATION, STRING_VC, BUNDLE_LOCALIZATION_DEFAULT_BASENAME);
    }

    @Override
    public int getBundleManifestVersion() {
        return get(BUNDLE_MANIFESTVERSION, INTEGER_VC, 1);
    }

    @Override
    public String getBundleName() {
        return get(BUNDLE_NAME, STRING_VC);
    }

    @Override
    public List<ParameterizedAttribute> getBundleNativeCode() {
        return get(BUNDLE_NATIVECODE, NATIVE_CODE_ATTRIB_LIST_VC);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<String> getRequiredExecutionEnvironment() {
        return get(BUNDLE_REQUIREDEXECUTIONENVIRONMENT, STRING_LIST_VC);
    }

    @Override
    public String getBundleSymbolicName() {
        ParameterizedAttribute parameters = parseSymbolicName();
        return (parameters != null ? parameters.getAttribute() : null);
    }

    @Override
    public Version getBundleVersion() {
        try {
            return get(BUNDLE_VERSION, VERSION_VC, Version.emptyVersion);
        } catch (IllegalArgumentException ex) {
            int manifestVersion = getBundleManifestVersion();
            if (manifestVersion < 2) {
                // Install expected to succeed on invalid Bundle-Version for R3 bundle
                // https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1503
                return Version.emptyVersion;
            }
            throw ex;
        }
    }

    @Override
    public ParameterizedAttribute getBundleParameters() {
        return parseSymbolicName();
    }

    @Override
    public URL getBundleUpdateLocation() {
        return get(BUNDLE_UPDATELOCATION, URL_VC);
    }

    @Override
    public List<PackageAttribute> getDynamicImports() {
        return get(DYNAMICIMPORT_PACKAGE, PACKAGE_LIST_VC);
    }

    @Override
    public List<PackageAttribute> getExportPackages() {
        return get(EXPORT_PACKAGE, PACKAGE_LIST_VC);
    }

    @Override
    public ParameterizedAttribute getFragmentHost() {
        return get(FRAGMENT_HOST, PARAM_ATTRIB_VC);
    }

    @Override
    public List<PackageAttribute> getImportPackages() {
        return get(IMPORT_PACKAGE, PACKAGE_LIST_VC);
    }

    @Override
    public List<ParameterizedAttribute> getRequireBundles() {
        return get(REQUIRE_BUNDLE, QNAME_ATTRIB_LIST_VC);
    }

    @Override
    public List<ParameterizedAttribute> getProvidedCapabilities() {
        return get(PROVIDE_CAPABILITY, QNAME_ATTRIB_LIST_VC);
    }

    @Override
    public List<ParameterizedAttribute> getRequiredCapabilities() {
        return get(REQUIRE_CAPABILITY, QNAME_ATTRIB_LIST_VC);
    }

    @Override
    public boolean isSingleton() {
        parseSymbolicName();
        if (parameters == null)
            return false;
        return "true".equals(parameters.getDirectiveValue(Constants.SINGLETON_DIRECTIVE, String.class));
    }

    @Override
    public boolean isFragment() {
        return getFragmentHost() != null;
    }

    public String getFragmentAttachment() {
        parseSymbolicName();
        if (parameters == null)
            return null;
        return parameters.getDirectiveValue(Constants.FRAGMENT_ATTACHMENT_DIRECTIVE, String.class);
    }

    protected ParameterizedAttribute parseSymbolicName() {
        if (parameters == null) {
            List<ParameterizedAttribute> parsed = get(BUNDLE_SYMBOLICNAME, QNAME_ATTRIB_LIST_VC);
            if (parsed == null || parsed.size() != 1)
                return null;
            parameters = parsed.get(0);
        }
        return parameters;
    }

    protected <T> T get(String key, ValueCreator<T> creator) {
        return get(key, creator, null);
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(String key, ValueCreator<T> creator, T defaultValue) {
        synchronized (cachedAttributes) {
            T value = (T) cachedAttributes.get(key);
            if (value == null) {
                String attribute = getMainAttribute(key);
                value = creator.createValue(attribute);
                if (attribute == null && defaultValue != null) {
                    value = defaultValue;
                }
                if (value != null)
                    cachedAttributes.put(key, value);
            }
            return value;
        }
    }

    public Map<String, Object> getCachedAttributes() {
        synchronized (cachedAttributes) {
            return Collections.unmodifiableMap(cachedAttributes);
        }
    }

    @Override
    public OSGiMetaData validate() throws BundleException {
        OSGiMetaDataBuilder.validateMetadata(this);
        return this;
    }

    @Override
    public String toString() {
        String name = getMainAttribute(Constants.BUNDLE_SYMBOLICNAME);
        name = name != null ? name : getMainAttribute(Constants.BUNDLE_NAME);
        Version version = Version.parseVersion(getMainAttribute(Constants.BUNDLE_VERSION));
        return "[" + name + ":" + version + "]";
    }
}

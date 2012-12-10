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


/**
 * ValueCreator holder.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author Thomas.Diesler@jboss.com
 */
public class ValueCreatorUtil {
    public static StringValueCreator STRING_VC = new StringValueCreator();
    public static IntegerValueCreator INTEGER_VC = new IntegerValueCreator();
    public static LongValueCreator LONG_VC = new LongValueCreator();
    public static DoubleValueCreator DOUBLE_VC = new DoubleValueCreator();
    public static BooleanValueCreator BOOLEAN_VC = new BooleanValueCreator();
    public static VersionValueCreator VERSION_VC = new VersionValueCreator();
    public static VersionRangeValueCreator VERSION_RANGE_VC = new VersionRangeValueCreator();
    public static URLValueCreator URL_VC = new URLValueCreator();
    public static StringListValueCreator STRING_LIST_VC = new StringListValueCreator();
    public static VersionListValueCreator VERSION_LIST_VC = new VersionListValueCreator();
    public static LongListValueCreator LONG_LIST_VC = new LongListValueCreator();
    public static DoubleListValueCreator DOUBLE_LIST_VC = new DoubleListValueCreator();
    public static ParameterizedAttributeValueCreator PARAM_ATTRIB_VC = new ParameterizedAttributeValueCreator();
    public static ParameterizedAttributeListValueCreator QNAME_ATTRIB_LIST_VC = new QNameAttributeListValueCreator();
    public static ParameterizedAttributeListValueCreator NATIVE_CODE_ATTRIB_LIST_VC = new NativeCodeAttributeListValueCreator();
    public static PackageAttributeListValueCreator PACKAGE_LIST_VC = new PackageAttributeListValueCreator();
    public static ActivationPolicyMDValueCreator ACTIVATION_POLICY_VC = new ActivationPolicyMDValueCreator();

    public static ValueCreator<?> forType(String type) {
        ValueCreator<?> result = STRING_VC;
        if ("Version".equals(type)) {
            result = VERSION_VC;
        } else if ("Long".equals(type)) {
            result = LONG_VC;
        } else if ("Double".equals(type)) {
            result = DOUBLE_VC;
        } else if ("List<Version>".equals(type)) {
            result = VERSION_LIST_VC;
        } else if ("List<Long>".equals(type)) {
            result = LONG_LIST_VC;
        } else if ("List<Double>".equals(type)) {
            result = DOUBLE_LIST_VC;
        } else if ("List<String>".equals(type)) {
            result = STRING_LIST_VC;
        }
        return result;
    }
}

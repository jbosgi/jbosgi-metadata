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

import java.util.ArrayList;
import java.util.List;

import org.jboss.osgi.metadata.ActivationPolicyMetaData;


/**
 * Activation policy value creator.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
class ActivationPolicyMDValueCreator extends AbstractValueCreator<ActivationPolicyMetaData> {
    private static final String INCLUDE = "include:=";
    private static final String EXCLUDE = "exclude:=";

    public ActivationPolicyMDValueCreator() {
        super(true);
    }

    protected ActivationPolicyMetaData useString(String attibute) {
        AbstractActivationPolicyMetaData aap = new AbstractActivationPolicyMetaData();
        String[] split = attibute.split(";");
        aap.setType(split[0]);
        if (split.length > 1)
            readDirective(aap, split[1].trim());
        if (split.length > 2)
            readDirective(aap, split[2].trim());
        return aap;
    }

    /**
     * Read the directive.
     * 
     * @param aap the activation policy
     * @param directive the directive
     */
    protected void readDirective(AbstractActivationPolicyMetaData aap, String directive) {
        if (directive.startsWith(INCLUDE)) {
            List<String> list = getPackageList(INCLUDE, directive);
            aap.setIncludes(list);
        } else if (directive.startsWith(EXCLUDE)) {
            List<String> list = getPackageList(EXCLUDE, directive);
            aap.setExcludes(list);
        }
    }

    private List<String> getPackageList(String prefix, String directive) {
        directive = directive.substring(prefix.length());
        if (directive.startsWith("\"") && directive.endsWith("\""))
            directive = directive.substring(1, directive.length() - 1);
        if (directive.startsWith("'") && directive.endsWith("'"))
            directive = directive.substring(1, directive.length() - 1);

        List<String> result = new ArrayList<String>();
        for (String packname : directive.split(","))
            result.add(packname.trim());

        return result;
    }
}

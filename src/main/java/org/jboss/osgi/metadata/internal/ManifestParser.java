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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.osgi.metadata.PackageAttribute;
import org.jboss.osgi.metadata.Parameter;
import org.jboss.osgi.metadata.ParameterizedAttribute;
import org.jboss.osgi.metadata.spi.ElementParser;
import org.jboss.osgi.metadata.spi.NotNullException;

/**
 * ManifestParser.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author Thomas.Diesler@jboss.com
 */
public class ManifestParser {
    /**
     * Parse packages
     *
     * @param header the header
     * @param list the list of packages to create
     */
    public static void parsePackages(String header, List<PackageAttribute> list) {
        parse(header, list, true, false);
    }

    /**
     * Parse parameters
     *
     * @param header the header
     * @param list the list of parameters to create
     */
    public static void parseParameters(String header, List<ParameterizedAttribute> list) {
        parse(header, list, false, false);
    }

    /**
     * Parse paths
     *
     * @param header the header
     * @param list the list of paths to create
     */
    public static void parsePaths(String header, List<ParameterizedAttribute> list) {
        parse(header, list, false, false);
    }

    /**
     * Parse a header
     *
     * @param header the header
     * @param list the list to create
     * @param packages whether to create packages
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void parse(String header, List list, boolean packages, boolean allowDuplicateAttributes) {
        NotNullException.assertValue(header, "header");
        if (header.length() == 0)
            throw new IllegalArgumentException("Invalid null header");

        // Split the header into clauses using which are seperated by commas
        // Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2,
        // path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2
        List<String> clauses = ElementParser.parseDelimitedString(header, ',');

        // Now parse each clause
        for (String clause : clauses) {
            // Split the clause into paths, directives and attributes using the semi-colon
            // Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2
            List<String> pieces = ElementParser.parseDelimitedString(clause, ';');

            // Collect the paths they should be first
            List<String> paths = new ArrayList<String>();
            for (String piece : pieces) {
                if (piece.indexOf('=') >= 0)
                    break;
                paths.add(unquote(piece));
            }
            if (paths.isEmpty())
                throw new IllegalArgumentException("No paths for clause: " + clause);

            Map<String, Parameter> directives = null;
            Map<String, Parameter> attributes = null;

            for (int i = paths.size(); i < pieces.size(); ++i) {
                String piece = pieces.get(i);
                int seperator = piece.indexOf(":=");
                if (seperator >= 0) {
                    String name = piece.substring(0, seperator);
                    String value = piece.substring(seperator + 2);
                    if (directives == null)
                        directives = new LinkedHashMap<String, Parameter>();
                    name = unquote(name.trim());
                    if (directives.containsKey(name))
                        throw new IllegalArgumentException("Duplicate directive: " + name);
                    value = unquote(value.trim());
                    directives.put(name, new AbstractParameter(ValueCreatorUtil.STRING_VC, value));
                } else {
                    seperator = piece.indexOf("=");
                    if (seperator >= 0) {
                        String name = piece.substring(0, seperator);
                        String value = piece.substring(seperator + 1);
                        ValueCreator<?> vc = ValueCreatorUtil.STRING_VC;
                        if (attributes == null)
                            attributes = new LinkedHashMap<String, Parameter>();
                        name = unquote(name.trim());
                        seperator = name.indexOf(":");
                        if (seperator >= 0) {
                            String type = name.substring(seperator + 1).trim();
                            if (type.startsWith("List")) {
                                int index = type.indexOf('<');
                                String subtype = index > 0 ? type.substring(index + 1, type.indexOf('>')) : "String";
                                type = "List<" + subtype.trim() + ">";
                            }
                            vc = ValueCreatorUtil.forType(type);
                            name = name.substring(0, seperator).trim();
                        }
                        Parameter attribute = attributes.get(name);
                        if (attribute != null) {
                            if (!allowDuplicateAttributes) {
                                throw new IllegalArgumentException("Duplicate attribute: " + name);
                            }
                        } else {
                            attribute = new AbstractParameter(vc);
                            attributes.put(name, attribute);
                        }
                        value = unquote(value.trim());
                        attribute.addValue(value);
                    } else {
                        throw new IllegalArgumentException("Path [" + piece + "] should appear before attributes and directives in clause: " + clause);
                    }
                }
            }

            for (String path : paths) {
                ParameterizedAttribute metadata = null;
                if (packages)
                    metadata = new AbstractPackageAttribute(path, attributes, directives);
                else
                    metadata = new AbstractParameterizedAttribute(path, attributes, directives);

                list.add(metadata);
            }
        }
    }

    /**
     * Remove around quotes around a string
     *
     * @param string the string
     * @return the unquoted string
     */
    private static String unquote(String string) {
        if (string.length() < 2)
            return string;
        if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"')
            return string.substring(1, string.length() - 1);
        if (string.charAt(0) == '\'' && string.charAt(string.length() - 1) == '\'')
            return string.substring(1, string.length() - 1);
        return string;
    }

}

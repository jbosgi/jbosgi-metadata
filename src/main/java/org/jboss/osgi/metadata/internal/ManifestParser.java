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

// This class is based on some original classes from
// Apache Felix which is licensed as below

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.osgi.metadata.internal;

import static org.jboss.osgi.metadata.MetadataMessages.MESSAGES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.osgi.metadata.PackageAttribute;
import org.jboss.osgi.metadata.Parameter;
import org.jboss.osgi.metadata.ParameterizedAttribute;


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
        if (header == null || header.length() == 0)
            throw MESSAGES.illegalArgumentNull("header");

        // Split the header into clauses using which are seperated by commas
        // Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2,
        // path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2
        List<String> clauses = parseDelimitedString(header, ",");

        // Now parse each clause
        for (String clause : clauses) {
            // Split the clause into paths, directives and attributes using the semi-colon
            // Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1; attr2=attrval2
            List<String> pieces = parseDelimitedString(clause, ";");

            // Collect the paths they should be first
            List<String> paths = new ArrayList<String>();
            for (String piece : pieces) {
                if (piece.indexOf('=') >= 0)
                    break;
                paths.add(unquote(piece));
            }
            if (paths.isEmpty())
                throw MESSAGES.illegalArgumentNoPathsForClause(clause);

            Map<String, Parameter> directives = null;
            Map<String, Parameter> attributes = null;

            for (int i = paths.size(); i < pieces.size(); ++i) {
                String piece = pieces.get(i);
                int seperator = piece.indexOf(":=");
                if (seperator >= 0) {
                    String name = piece.substring(0, seperator);
                    String value = piece.substring(seperator + 2);
                    if (directives == null)
                        directives = new HashMap<String, Parameter>();
                    String unquoted = unquote(name);
                    if (directives.containsKey(unquoted))
                        throw MESSAGES.illegalArgumentDuplicateDirective(unquoted);
                    directives.put(unquoted, new AbstractParameter(unquote(value)));
                } else {
                    seperator = piece.indexOf("=");
                    if (seperator >= 0) {
                        String name = piece.substring(0, seperator);
                        String value = piece.substring(seperator + 1);
                        if (attributes == null)
                            attributes = new HashMap<String, Parameter>();
                        String unquoted = unquote(name);
                        Parameter attribute = attributes.get(unquoted);
                        if (attribute != null) {
                            if (!allowDuplicateAttributes) {
                                throw MESSAGES.illegalArgumentDuplicateAttribute(unquoted);
                            }
                        } else {
                            attribute = new AbstractParameter();
                            attributes.put(unquoted, attribute);
                        }
                        attribute.addValue(unquote(value));
                    } else {
                        throw MESSAGES.illegalArgumentPathShouldAppearBefore(piece, clause);
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

    /**
     * Parses delimited string and returns an array containing the tokens. This parser obeys quotes, so the delimiter character
     * will be ignored if it is inside of a quote. This method assumes that the quote character is not included in the set of
     * delimiter characters.
     *
     * @param value the delimited string to parse.
     * @param delim the characters delimiting the tokens.
     * @return an array of string tokens or null if there were no tokens.
     **/
    private static List<String> parseDelimitedString(String value, String delim) {
        if (value == null)
            value = "";

        List<String> list = new ArrayList<String>();

        int CHAR = 1;
        int DELIMITER = 2;
        int STARTQUOTE = 4;
        int ENDQUOTE = 8;

        StringBuilder sb = new StringBuilder();

        int expecting = (CHAR | DELIMITER | STARTQUOTE);

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            boolean isDelimiter = (delim.indexOf(c) >= 0);
            boolean isQuote = (c == '"') || (c == '\'');

            if (isDelimiter && ((expecting & DELIMITER) > 0)) {
                list.add(sb.toString().trim());
                sb.delete(0, sb.length());
                expecting = (CHAR | DELIMITER | STARTQUOTE);
            } else if (isQuote && ((expecting & STARTQUOTE) > 0)) {
                sb.append(c);
                expecting = CHAR | ENDQUOTE;
            } else if (isQuote && ((expecting & ENDQUOTE) > 0)) {
                sb.append(c);
                expecting = (CHAR | STARTQUOTE | DELIMITER);
            } else if ((expecting & CHAR) > 0) {
                sb.append(c);
            } else {
                throw MESSAGES.illegalArgumentInvalidDelimitedString(value, delim);
            }
        }

        if (sb.length() > 0)
            list.add(sb.toString().trim());

        return list;
    }

}

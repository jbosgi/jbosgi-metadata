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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * A simple manifest builder.
 *
 * @author thomas.diesler@jboss.com
 * @since 08-Mar-2010
 */
public final class ManifestBuilder implements Asset {

    static final Logger LOGGER = Logger.getLogger(ManifestBuilder.class.getName());

    private List<String> lines = new ArrayList<String>();
    private Manifest manifest;

    public static ManifestBuilder newInstance() {
        return new ManifestBuilder();
    }

    private ManifestBuilder() {
        append(Attributes.Name.MANIFEST_VERSION + ": 1.0");
    }

    public ManifestBuilder addManifestHeader(String key, String value) {
        append(key + ": " + value);
        return this;
    }

    public Manifest getManifest() {
        if (manifest == null) {
            // JBOSGI-780
            int maxLineLength = 512 - System.getProperty("line.separator").length();
            StringWriter out = new StringWriter();
            PrintWriter pw = new PrintWriter(out);
            for(String line : lines) {
                byte[] bytes = line.getBytes();
                while (bytes.length > maxLineLength) {
                    byte[] head = Arrays.copyOf(bytes, 256);
                    bytes = Arrays.copyOfRange(bytes, 256, bytes.length);
                    pw.println(new String(head));
                    pw.print(" ");
                }
                pw.println(new String(bytes));
            }

            String content = out.toString();
            if (LOGGER.isLoggable(Level.FINEST))
                LOGGER.finest(content);

            try {
                manifest = new Manifest(new ByteArrayInputStream(content.getBytes()));
            } catch (IOException ex) {
                throw new IllegalStateException("Cannot create manifest", ex);
            }
        }
        return manifest;
    }

    @Override
    public InputStream openStream() {
        Manifest manifest = getManifest();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            manifest.write(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot provide manifest input stream", ex);
        }
    }

    public void append(String line) {
        if (manifest != null)
            throw new IllegalStateException("Cannot append to already existing manifest");

        lines.add(line);
    }
}

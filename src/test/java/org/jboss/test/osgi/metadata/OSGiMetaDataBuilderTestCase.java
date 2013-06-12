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
package org.jboss.test.osgi.metadata;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.jboss.osgi.metadata.OSGiMetaData;
import org.jboss.osgi.metadata.OSGiMetaDataBuilder;
import org.jboss.osgi.metadata.PackageAttribute;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the {@link OSGiMetaDataBuilder}.
 *
 * @author thomas.diesler@jboss.com
 * @since 27-Jun-2013
 */
public class OSGiMetaDataBuilderTestCase {

    @Test
    public void testSplitLines() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream("jbosgi-xservice-01.properties");
        Properties props = new Properties();
        props.load(input);

        OSGiMetaData metadata = OSGiMetaDataBuilder.load(props);
        List<PackageAttribute> exportPackages = metadata.getExportPackages();
        Assert.assertEquals(15, exportPackages.size());
    }
}

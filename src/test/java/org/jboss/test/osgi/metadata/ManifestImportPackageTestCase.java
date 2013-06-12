/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.osgi.metadata;

import java.util.List;
import java.util.jar.Manifest;

import junit.framework.Assert;

import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.osgi.metadata.OSGiMetaData;
import org.jboss.osgi.metadata.OSGiMetaDataBuilder;
import org.jboss.osgi.metadata.PackageAttribute;
import org.jboss.osgi.metadata.Parameter;
import org.junit.Test;
import org.osgi.framework.VersionRange;

/**
 * Test the Manifest parser
 *
 * @author thomas.diesler@jboss.com
 * @since 04-Oct-2012
 */
public class ManifestImportPackageTestCase {

    @Test
    public void testPackageAttributes() {
        verifyImportPackageSpec("org.osgi.service.blueprint;version=\"[1.0.0,2.0.0)\"");
        verifyImportPackageSpec("org.osgi.service.blueprint; version='[1.0.0,2.0.0)'");
        verifyExportPackageSpec("org.osgi.service.blueprint;version=\"1.0.0\"");
        verifyExportPackageSpec("org.osgi.service.blueprint; version='1.0.0'");
        verifyExportPackageSpec("org.osgi.service.blueprint; version=1.0.0");
    }

    @Test
    public void testOtherPackageAttributes() {
        verifyOtherImportPackageSpec("org.osgi.service.blueprint;atts=xxx", "xxx");
        verifyOtherImportPackageSpec("org.osgi.service.blueprint;atts=\"xxx,yyy\"", "xxx,yyy");
        verifyOtherImportPackageSpec("org.osgi.service.blueprint;atts='xxx,yyy'", "xxx,yyy");
        verifyOtherImportPackageSpec("org.osgi.service.blueprint;atts=\"xxx'yyy\"", "xxx'yyy");
        verifyOtherImportPackageSpec("org.osgi.service.blueprint;atts='xxx\"yyy\"'", "xxx\"yyy\"");
    }

    private void verifyOtherImportPackageSpec(String packagespec, Object expected) {
        OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
        builder.addBundleManifestVersion(2);
        builder.addBundleSymbolicName("some.name");
        builder.addImportPackage(packagespec);
        Manifest manifest = builder.getManifest();

        OSGiMetaData metadata = OSGiMetaDataBuilder.load(manifest);
        List<PackageAttribute> packages = metadata.getImportPackages();
        Assert.assertEquals(1, packages.size());
        PackageAttribute pattr = packages.get(0);
        Assert.assertEquals("org.osgi.service.blueprint", pattr.getPackageName());
        Parameter param = pattr.getAttribute("atts");
        Assert.assertEquals(expected, param.getValue());
    }

    private void verifyImportPackageSpec(String packagespec) {
        OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
        builder.addBundleManifestVersion(2);
        builder.addBundleSymbolicName("some.name");
        builder.addImportPackage(packagespec);
        Manifest manifest = builder.getManifest();

        OSGiMetaData metadata = OSGiMetaDataBuilder.load(manifest);
        List<PackageAttribute> packages = metadata.getImportPackages();
        Assert.assertEquals(1, packages.size());
        PackageAttribute pattr = packages.get(0);
        Assert.assertEquals("org.osgi.service.blueprint", pattr.getPackageName());
        Assert.assertEquals(new VersionRange("[1,2)"), pattr.getVersion());
    }


    private void verifyExportPackageSpec(String packagespec) {
        OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
        builder.addBundleManifestVersion(2);
        builder.addBundleSymbolicName("some.name");
        builder.addExportPackage(packagespec);
        Manifest manifest = builder.getManifest();

        OSGiMetaData metadata = OSGiMetaDataBuilder.load(manifest);
        List<PackageAttribute> packages = metadata.getExportPackages();
        Assert.assertEquals(1, packages.size());
        PackageAttribute pattr = packages.get(0);
        Assert.assertEquals("org.osgi.service.blueprint", pattr.getPackageName());
        Assert.assertEquals(new VersionRange("1.0.0"), pattr.getVersion());
    }
}

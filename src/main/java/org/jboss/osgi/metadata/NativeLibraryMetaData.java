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


import static org.jboss.osgi.metadata.MetadataMessages.MESSAGES;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Meta data for native code libraries as defined by OSGi R4V42.
 *
 * 3.9 Loading Native Code Libraries http://www.osgi.org/Download/File?url=/download/r4v42/r4.core.pdf
 *
 * @author Thomas.Diesler@jboss.com
 * @since 21-Jan-2010
 */
public class NativeLibraryMetaData implements Serializable {
    /** The serialVersionUID */
    private static final long serialVersionUID = 7650641261993316609L;

    /** The nativeLibraries */
    private List<NativeLibrary> nativeLibraries;

    public List<NativeLibrary> getNativeLibraries() {
        return nativeLibraries;
    }

    public void setNativeLibraries(List<NativeLibrary> nativeLibraries) {
        this.nativeLibraries = nativeLibraries;
    }

    public void addNativeLibrary(NativeLibrary nativeLibrary) {
        if (nativeLibrary == null)
            throw MESSAGES.illegalArgumentNull("library");

        if (nativeLibraries == null)
            nativeLibraries = new CopyOnWriteArrayList<NativeLibrary>();

        nativeLibraries.add(nativeLibrary);
    }

    public void removeNativeLibrary(NativeLibrary nativeLibrary) {
        if (nativeLibrary == null)
            throw MESSAGES.illegalArgumentNull("library");

        if (nativeLibraries == null)
            return;

        nativeLibraries.remove(nativeLibrary);
    }
}

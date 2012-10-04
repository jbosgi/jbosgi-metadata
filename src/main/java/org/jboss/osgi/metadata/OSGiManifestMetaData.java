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

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.jboss.osgi.metadata.spi.AbstractOSGiMetaData;


/**
 * Abstract OSGi meta data.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jun-2010
 */
class OSGiManifestMetaData extends AbstractOSGiMetaData implements Externalizable {
    private Manifest manifest;

    public OSGiManifestMetaData() {
        // for serialization
    }

    OSGiManifestMetaData(Manifest manifest) {
        this.manifest = manifest;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<Name, String> getMainAttributes() {
        return (Map) manifest.getMainAttributes();
    }

    @Override
    public String getMainAttribute(String key) {
        String value = manifest.getMainAttributes().getValue(key);
        return value != null ? value.trim() : null;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        OutputStream os = new OutputWrapper(out);
        manifest.write(os);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        InputStream is = new InputWrapper(in);
        manifest = new Manifest(is);
    }

    private static class OutputWrapper extends OutputStream {
        private ObjectOutput out;

        public OutputWrapper(ObjectOutput out) {
            this.out = out;
        }

        public void write(int b) throws IOException {
            out.write(b);
        }

        public void flush() throws IOException {
            out.flush();
        }

        public void close() throws IOException {
            out.close();
        }
    }

    class InputWrapper extends InputStream {
        private ObjectInput in;

        public InputWrapper(ObjectInput in) {
            this.in = in;
        }

        public int read() throws IOException {
            return in.read();
        }

        public long skip(long n) throws IOException {
            return in.skip(n);
        }

        public int available() throws IOException {
            return in.available();
        }

        public void close() throws IOException {
            in.close();
        }
    }
}

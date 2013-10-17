/*
 * #%L
 * Gravia :: Runtime :: API
 * %%
 * Copyright (C) 2013 JBoss by Red Hat
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
package org.jboss.osgi.metadata.spi;

/**
 * NotNullException
 *
 * @author thomas.diesler@jboss.com
 * @since 27-Sep-2013
 */
public final class NotNullException {

    // hide ctor
    private NotNullException() {
    }

    /**
     * Throws an IllegalArgumentException when the given value is null.
     */
    public static void assertValue(Object value, String name) {
        if (value == null)
            throw new IllegalArgumentException("Null " + name);
    }
}

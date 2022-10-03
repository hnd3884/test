package com.sun.rowset.internal;

import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class XmlResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String s, final String s2) {
        final String substring = s2.substring(s2.lastIndexOf("/"));
        if (s2.startsWith("http://java.sun.com/xml/ns/jdbc")) {
            return new InputSource(this.getClass().getResourceAsStream(substring));
        }
        return null;
    }
}

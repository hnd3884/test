package com.microsoft.sqlserver.jdbc;

import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

final class SQLServerEntityResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        return new InputSource(new StringReader(""));
    }
}

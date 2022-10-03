package com.adventnet.iam.security;

import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class DummyEntityResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    }
}

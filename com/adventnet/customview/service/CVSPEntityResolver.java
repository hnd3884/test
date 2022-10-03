package com.adventnet.customview.service;

import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class CVSPEntityResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream("dtd/customview-service-providers.dtd");
        final BufferedInputStream bis = new BufferedInputStream(stream);
        final InputSource inputSource = new InputSource(bis);
        return inputSource;
    }
}

package com.sun.org.apache.xerces.internal.util;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.xml.sax.EntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

public class EntityResolverWrapper implements XMLEntityResolver
{
    protected EntityResolver fEntityResolver;
    
    public EntityResolverWrapper() {
    }
    
    public EntityResolverWrapper(final EntityResolver entityResolver) {
        this.setEntityResolver(entityResolver);
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.fEntityResolver = entityResolver;
    }
    
    public EntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    @Override
    public XMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
        final String pubId = resourceIdentifier.getPublicId();
        final String sysId = resourceIdentifier.getExpandedSystemId();
        if (pubId == null && sysId == null) {
            return null;
        }
        if (this.fEntityResolver != null && resourceIdentifier != null) {
            try {
                final InputSource inputSource = this.fEntityResolver.resolveEntity(pubId, sysId);
                if (inputSource != null) {
                    final String publicId = inputSource.getPublicId();
                    final String systemId = inputSource.getSystemId();
                    final String baseSystemId = resourceIdentifier.getBaseSystemId();
                    final InputStream byteStream = inputSource.getByteStream();
                    final Reader charStream = inputSource.getCharacterStream();
                    final String encoding = inputSource.getEncoding();
                    final XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, baseSystemId);
                    xmlInputSource.setByteStream(byteStream);
                    xmlInputSource.setCharacterStream(charStream);
                    xmlInputSource.setEncoding(encoding);
                    return xmlInputSource;
                }
            }
            catch (final SAXException e) {
                Exception ex = e.getException();
                if (ex == null) {
                    ex = e;
                }
                throw new XNIException(ex);
            }
        }
        return null;
    }
}

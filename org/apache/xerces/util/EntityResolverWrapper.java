package org.apache.xerces.util;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.xml.sax.EntityResolver;
import org.apache.xerces.xni.parser.XMLEntityResolver;

public class EntityResolverWrapper implements XMLEntityResolver
{
    protected EntityResolver fEntityResolver;
    
    public EntityResolverWrapper() {
    }
    
    public EntityResolverWrapper(final EntityResolver entityResolver) {
        this.setEntityResolver(entityResolver);
    }
    
    public void setEntityResolver(final EntityResolver fEntityResolver) {
        this.fEntityResolver = fEntityResolver;
    }
    
    public EntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier xmlResourceIdentifier) throws XNIException, IOException {
        final String publicId = xmlResourceIdentifier.getPublicId();
        final String expandedSystemId = xmlResourceIdentifier.getExpandedSystemId();
        if (publicId == null && expandedSystemId == null) {
            return null;
        }
        if (this.fEntityResolver != null && xmlResourceIdentifier != null) {
            try {
                final InputSource resolveEntity = this.fEntityResolver.resolveEntity(publicId, expandedSystemId);
                if (resolveEntity != null) {
                    final String publicId2 = resolveEntity.getPublicId();
                    final String systemId = resolveEntity.getSystemId();
                    final String baseSystemId = xmlResourceIdentifier.getBaseSystemId();
                    final InputStream byteStream = resolveEntity.getByteStream();
                    final Reader characterStream = resolveEntity.getCharacterStream();
                    final String encoding = resolveEntity.getEncoding();
                    final XMLInputSource xmlInputSource = new XMLInputSource(publicId2, systemId, baseSystemId);
                    xmlInputSource.setByteStream(byteStream);
                    xmlInputSource.setCharacterStream(characterStream);
                    xmlInputSource.setEncoding(encoding);
                    return xmlInputSource;
                }
            }
            catch (final SAXException ex) {
                Exception exception = ex.getException();
                if (exception == null) {
                    exception = ex;
                }
                throw new XNIException(exception);
            }
        }
        return null;
    }
}

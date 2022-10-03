package com.sun.org.apache.xerces.internal.util;

import java.io.Reader;
import java.io.InputStream;
import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import org.xml.sax.ext.EntityResolver2;
import com.sun.org.apache.xerces.internal.impl.ExternalSubsetResolver;

public class EntityResolver2Wrapper implements ExternalSubsetResolver
{
    protected EntityResolver2 fEntityResolver;
    
    public EntityResolver2Wrapper() {
    }
    
    public EntityResolver2Wrapper(final EntityResolver2 entityResolver) {
        this.setEntityResolver(entityResolver);
    }
    
    public void setEntityResolver(final EntityResolver2 entityResolver) {
        this.fEntityResolver = entityResolver;
    }
    
    public EntityResolver2 getEntityResolver() {
        return this.fEntityResolver;
    }
    
    @Override
    public XMLInputSource getExternalSubset(final XMLDTDDescription grammarDescription) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            final String name = grammarDescription.getRootName();
            final String baseURI = grammarDescription.getBaseSystemId();
            try {
                final InputSource inputSource = this.fEntityResolver.getExternalSubset(name, baseURI);
                return (inputSource != null) ? this.createXMLInputSource(inputSource, baseURI) : null;
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
    
    @Override
    public XMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            final String pubId = resourceIdentifier.getPublicId();
            final String sysId = resourceIdentifier.getLiteralSystemId();
            final String baseURI = resourceIdentifier.getBaseSystemId();
            String name = null;
            if (resourceIdentifier instanceof XMLDTDDescription) {
                name = "[dtd]";
            }
            else if (resourceIdentifier instanceof XMLEntityDescription) {
                name = ((XMLEntityDescription)resourceIdentifier).getEntityName();
            }
            if (pubId == null && sysId == null) {
                return null;
            }
            try {
                final InputSource inputSource = this.fEntityResolver.resolveEntity(name, pubId, baseURI, sysId);
                return (inputSource != null) ? this.createXMLInputSource(inputSource, baseURI) : null;
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
    
    private XMLInputSource createXMLInputSource(final InputSource source, final String baseURI) {
        final String publicId = source.getPublicId();
        final String systemId = source.getSystemId();
        final String baseSystemId = baseURI;
        final InputStream byteStream = source.getByteStream();
        final Reader charStream = source.getCharacterStream();
        final String encoding = source.getEncoding();
        final XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, baseSystemId);
        xmlInputSource.setByteStream(byteStream);
        xmlInputSource.setCharacterStream(charStream);
        xmlInputSource.setEncoding(encoding);
        return xmlInputSource;
    }
}

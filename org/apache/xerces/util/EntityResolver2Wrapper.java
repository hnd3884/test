package org.apache.xerces.util;

import java.io.Reader;
import java.io.InputStream;
import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.xni.XMLResourceIdentifier;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.grammars.XMLDTDDescription;
import org.xml.sax.ext.EntityResolver2;
import org.apache.xerces.impl.ExternalSubsetResolver;

public class EntityResolver2Wrapper implements ExternalSubsetResolver
{
    protected EntityResolver2 fEntityResolver;
    
    public EntityResolver2Wrapper() {
    }
    
    public EntityResolver2Wrapper(final EntityResolver2 entityResolver) {
        this.setEntityResolver(entityResolver);
    }
    
    public void setEntityResolver(final EntityResolver2 fEntityResolver) {
        this.fEntityResolver = fEntityResolver;
    }
    
    public EntityResolver2 getEntityResolver() {
        return this.fEntityResolver;
    }
    
    public XMLInputSource getExternalSubset(final XMLDTDDescription xmldtdDescription) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            final String rootName = xmldtdDescription.getRootName();
            final String baseSystemId = xmldtdDescription.getBaseSystemId();
            try {
                final InputSource externalSubset = this.fEntityResolver.getExternalSubset(rootName, baseSystemId);
                return (externalSubset != null) ? this.createXMLInputSource(externalSubset, baseSystemId) : null;
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
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier xmlResourceIdentifier) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            final String publicId = xmlResourceIdentifier.getPublicId();
            final String literalSystemId = xmlResourceIdentifier.getLiteralSystemId();
            final String baseSystemId = xmlResourceIdentifier.getBaseSystemId();
            String entityName = null;
            if (xmlResourceIdentifier instanceof XMLDTDDescription) {
                entityName = "[dtd]";
            }
            else if (xmlResourceIdentifier instanceof XMLEntityDescription) {
                entityName = ((XMLEntityDescription)xmlResourceIdentifier).getEntityName();
            }
            if (publicId == null && literalSystemId == null) {
                return null;
            }
            try {
                final InputSource resolveEntity = this.fEntityResolver.resolveEntity(entityName, publicId, baseSystemId, literalSystemId);
                return (resolveEntity != null) ? this.createXMLInputSource(resolveEntity, baseSystemId) : null;
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
    
    private XMLInputSource createXMLInputSource(final InputSource inputSource, final String s) {
        final String publicId = inputSource.getPublicId();
        final String systemId = inputSource.getSystemId();
        final InputStream byteStream = inputSource.getByteStream();
        final Reader characterStream = inputSource.getCharacterStream();
        final String encoding = inputSource.getEncoding();
        final XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, s);
        xmlInputSource.setByteStream(byteStream);
        xmlInputSource.setCharacterStream(characterStream);
        xmlInputSource.setEncoding(encoding);
        return xmlInputSource;
    }
}

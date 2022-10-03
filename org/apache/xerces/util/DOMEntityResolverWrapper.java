package org.apache.xerces.util;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;
import java.io.Reader;
import java.io.StringReader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.w3c.dom.ls.LSResourceResolver;
import org.apache.xerces.xni.parser.XMLEntityResolver;

public class DOMEntityResolverWrapper implements XMLEntityResolver
{
    private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
    private static final String XSD_TYPE = "http://www.w3.org/2001/XMLSchema";
    protected LSResourceResolver fEntityResolver;
    
    public DOMEntityResolverWrapper() {
    }
    
    public DOMEntityResolverWrapper(final LSResourceResolver entityResolver) {
        this.setEntityResolver(entityResolver);
    }
    
    public void setEntityResolver(final LSResourceResolver fEntityResolver) {
        this.fEntityResolver = fEntityResolver;
    }
    
    public LSResourceResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier xmlResourceIdentifier) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            final LSInput lsInput = (xmlResourceIdentifier == null) ? this.fEntityResolver.resolveResource(null, null, null, null, null) : this.fEntityResolver.resolveResource(this.getType(xmlResourceIdentifier), xmlResourceIdentifier.getNamespace(), xmlResourceIdentifier.getPublicId(), xmlResourceIdentifier.getLiteralSystemId(), xmlResourceIdentifier.getBaseSystemId());
            if (lsInput != null) {
                final String publicId = lsInput.getPublicId();
                final String systemId = lsInput.getSystemId();
                final String baseURI = lsInput.getBaseURI();
                final InputStream byteStream = lsInput.getByteStream();
                final Reader characterStream = lsInput.getCharacterStream();
                final String encoding = lsInput.getEncoding();
                final String stringData = lsInput.getStringData();
                final XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, baseURI);
                if (characterStream != null) {
                    xmlInputSource.setCharacterStream(characterStream);
                }
                else if (byteStream != null) {
                    xmlInputSource.setByteStream(byteStream);
                }
                else if (stringData != null && stringData.length() != 0) {
                    xmlInputSource.setCharacterStream(new StringReader(stringData));
                }
                xmlInputSource.setEncoding(encoding);
                return xmlInputSource;
            }
        }
        return null;
    }
    
    private String getType(final XMLResourceIdentifier xmlResourceIdentifier) {
        if (xmlResourceIdentifier instanceof XMLGrammarDescription && "http://www.w3.org/2001/XMLSchema".equals(((XMLGrammarDescription)xmlResourceIdentifier).getGrammarType())) {
            return "http://www.w3.org/2001/XMLSchema";
        }
        return "http://www.w3.org/TR/REC-xml";
    }
}

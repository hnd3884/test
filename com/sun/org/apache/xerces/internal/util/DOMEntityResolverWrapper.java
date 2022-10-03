package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;
import java.io.Reader;
import java.io.StringReader;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.w3c.dom.ls.LSResourceResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

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
    
    public void setEntityResolver(final LSResourceResolver entityResolver) {
        this.fEntityResolver = entityResolver;
    }
    
    public LSResourceResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    @Override
    public XMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            final LSInput inputSource = (resourceIdentifier == null) ? this.fEntityResolver.resolveResource(null, null, null, null, null) : this.fEntityResolver.resolveResource(this.getType(resourceIdentifier), resourceIdentifier.getNamespace(), resourceIdentifier.getPublicId(), resourceIdentifier.getLiteralSystemId(), resourceIdentifier.getBaseSystemId());
            if (inputSource != null) {
                final String publicId = inputSource.getPublicId();
                final String systemId = inputSource.getSystemId();
                final String baseSystemId = inputSource.getBaseURI();
                final InputStream byteStream = inputSource.getByteStream();
                final Reader charStream = inputSource.getCharacterStream();
                final String encoding = inputSource.getEncoding();
                final String data = inputSource.getStringData();
                final XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, baseSystemId);
                if (charStream != null) {
                    xmlInputSource.setCharacterStream(charStream);
                }
                else if (byteStream != null) {
                    xmlInputSource.setByteStream(byteStream);
                }
                else if (data != null && data.length() != 0) {
                    xmlInputSource.setCharacterStream(new StringReader(data));
                }
                xmlInputSource.setEncoding(encoding);
                return xmlInputSource;
            }
        }
        return null;
    }
    
    private String getType(final XMLResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier instanceof XMLGrammarDescription) {
            final XMLGrammarDescription desc = (XMLGrammarDescription)resourceIdentifier;
            if ("http://www.w3.org/2001/XMLSchema".equals(desc.getGrammarType())) {
                return "http://www.w3.org/2001/XMLSchema";
            }
        }
        return "http://www.w3.org/TR/REC-xml";
    }
}

package com.adventnet.iam.security;

import java.io.StringReader;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class DummyResourceResolver implements LSResourceResolver
{
    @Override
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
        return new LSInput() {
            @Override
            public void setSystemId(final String systemId) {
            }
            
            @Override
            public void setStringData(final String stringData) {
            }
            
            @Override
            public void setPublicId(final String publicId) {
            }
            
            @Override
            public void setEncoding(final String encoding) {
            }
            
            @Override
            public void setCharacterStream(final Reader characterStream) {
            }
            
            @Override
            public void setCertifiedText(final boolean certifiedText) {
            }
            
            @Override
            public void setByteStream(final InputStream byteStream) {
            }
            
            @Override
            public void setBaseURI(final String baseURI) {
            }
            
            @Override
            public String getSystemId() {
                return null;
            }
            
            @Override
            public String getStringData() {
                return null;
            }
            
            @Override
            public String getPublicId() {
                return null;
            }
            
            @Override
            public String getEncoding() {
                return null;
            }
            
            @Override
            public Reader getCharacterStream() {
                return new StringReader("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"/>");
            }
            
            @Override
            public boolean getCertifiedText() {
                return false;
            }
            
            @Override
            public InputStream getByteStream() {
                return null;
            }
            
            @Override
            public String getBaseURI() {
                return null;
            }
        };
    }
}

package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TypedXmlWriter;

public final class TXWSerializer implements XmlSerializer
{
    public final TypedXmlWriter txw;
    
    public TXWSerializer(final TypedXmlWriter txw) {
        this.txw = txw;
    }
    
    @Override
    public void startDocument() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void endDocument() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeXmlns(final String prefix, final String uri) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void endStartTag(final String uri, final String localName, final String prefix) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void endTag() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void text(final StringBuilder text) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void cdata(final StringBuilder text) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void comment(final StringBuilder comment) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }
}

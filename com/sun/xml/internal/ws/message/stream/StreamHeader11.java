package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.ws.message.Util;
import com.sun.istack.internal.FinalArrayList;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;

public class StreamHeader11 extends StreamHeader
{
    protected static final String SOAP_1_1_MUST_UNDERSTAND = "mustUnderstand";
    protected static final String SOAP_1_1_ROLE = "actor";
    
    public StreamHeader11(final XMLStreamReader reader, final XMLStreamBuffer mark) {
        super(reader, mark);
    }
    
    public StreamHeader11(final XMLStreamReader reader) throws XMLStreamException {
        super(reader);
    }
    
    @Override
    protected final FinalArrayList<Attribute> processHeaderAttributes(final XMLStreamReader reader) {
        FinalArrayList<Attribute> atts = null;
        this._role = "http://schemas.xmlsoap.org/soap/actor/next";
        for (int i = 0; i < reader.getAttributeCount(); ++i) {
            final String localName = reader.getAttributeLocalName(i);
            final String namespaceURI = reader.getAttributeNamespace(i);
            final String value = reader.getAttributeValue(i);
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceURI)) {
                if ("mustUnderstand".equals(localName)) {
                    this._isMustUnderstand = Util.parseBool(value);
                }
                else if ("actor".equals(localName) && value != null && value.length() > 0) {
                    this._role = value;
                }
            }
            if (atts == null) {
                atts = new FinalArrayList<Attribute>();
            }
            atts.add(new Attribute(namespaceURI, localName, value));
        }
        return atts;
    }
}

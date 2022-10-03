package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.ws.message.Util;
import com.sun.istack.internal.FinalArrayList;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;

public class StreamHeader12 extends StreamHeader
{
    protected static final String SOAP_1_2_MUST_UNDERSTAND = "mustUnderstand";
    protected static final String SOAP_1_2_ROLE = "role";
    protected static final String SOAP_1_2_RELAY = "relay";
    
    public StreamHeader12(final XMLStreamReader reader, final XMLStreamBuffer mark) {
        super(reader, mark);
    }
    
    public StreamHeader12(final XMLStreamReader reader) throws XMLStreamException {
        super(reader);
    }
    
    @Override
    protected final FinalArrayList<Attribute> processHeaderAttributes(final XMLStreamReader reader) {
        FinalArrayList<Attribute> atts = null;
        this._role = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
        for (int i = 0; i < reader.getAttributeCount(); ++i) {
            final String localName = reader.getAttributeLocalName(i);
            final String namespaceURI = reader.getAttributeNamespace(i);
            final String value = reader.getAttributeValue(i);
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceURI)) {
                if ("mustUnderstand".equals(localName)) {
                    this._isMustUnderstand = Util.parseBool(value);
                }
                else if ("role".equals(localName)) {
                    if (value != null && value.length() > 0) {
                        this._role = value;
                    }
                }
                else if ("relay".equals(localName)) {
                    this._isRelay = Util.parseBool(value);
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

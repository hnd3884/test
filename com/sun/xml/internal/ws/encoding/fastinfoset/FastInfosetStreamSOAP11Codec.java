package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.message.stream.StreamHeader11;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;

final class FastInfosetStreamSOAP11Codec extends FastInfosetStreamSOAPCodec
{
    FastInfosetStreamSOAP11Codec(final StreamSOAPCodec soapCodec, final boolean retainState) {
        super(soapCodec, SOAPVersion.SOAP_11, retainState, retainState ? "application/vnd.sun.stateful.fastinfoset" : "application/fastinfoset");
    }
    
    private FastInfosetStreamSOAP11Codec(final FastInfosetStreamSOAP11Codec that) {
        super(that);
    }
    
    @Override
    public Codec copy() {
        return new FastInfosetStreamSOAP11Codec(this);
    }
    
    @Override
    protected final StreamHeader createHeader(final XMLStreamReader reader, final XMLStreamBuffer mark) {
        return new StreamHeader11(reader, mark);
    }
    
    @Override
    protected ContentType getContentType(final String soapAction) {
        if (soapAction == null || soapAction.length() == 0) {
            return this._defaultContentType;
        }
        return new ContentTypeImpl(this._defaultContentType.getContentType(), soapAction);
    }
}

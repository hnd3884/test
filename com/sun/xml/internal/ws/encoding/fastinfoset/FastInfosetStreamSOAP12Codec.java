package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;

final class FastInfosetStreamSOAP12Codec extends FastInfosetStreamSOAPCodec
{
    FastInfosetStreamSOAP12Codec(final StreamSOAPCodec soapCodec, final boolean retainState) {
        super(soapCodec, SOAPVersion.SOAP_12, retainState, retainState ? "application/vnd.sun.stateful.soap+fastinfoset" : "application/soap+fastinfoset");
    }
    
    private FastInfosetStreamSOAP12Codec(final FastInfosetStreamSOAPCodec that) {
        super(that);
    }
    
    @Override
    public Codec copy() {
        return new FastInfosetStreamSOAP12Codec(this);
    }
    
    @Override
    protected final StreamHeader createHeader(final XMLStreamReader reader, final XMLStreamBuffer mark) {
        return new StreamHeader12(reader, mark);
    }
    
    @Override
    protected ContentType getContentType(final String soapAction) {
        if (soapAction == null) {
            return this._defaultContentType;
        }
        return new ContentTypeImpl(this._defaultContentType.getContentType() + ";action=\"" + soapAction + "\"");
    }
}

package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;

final class StAXExConnector extends StAXStreamConnector
{
    private final XMLStreamReaderEx in;
    
    public StAXExConnector(final XMLStreamReaderEx in, final XmlVisitor visitor) {
        super(in, visitor);
        this.in = in;
    }
    
    @Override
    protected void handleCharacters() throws XMLStreamException, SAXException {
        if (this.predictor.expectText()) {
            final CharSequence pcdata = this.in.getPCDATA();
            if (pcdata instanceof Base64Data) {
                final Base64Data bd = (Base64Data)pcdata;
                final com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data binary = new com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data();
                if (!bd.hasData()) {
                    binary.set(bd.getDataHandler());
                }
                else {
                    binary.set(bd.get(), bd.getDataLen(), bd.getMimeType());
                }
                this.visitor.text(binary);
                this.textReported = true;
            }
            else {
                this.buffer.append(pcdata);
            }
        }
    }
}

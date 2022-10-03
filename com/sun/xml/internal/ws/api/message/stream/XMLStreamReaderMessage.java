package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderMessage extends StreamBasedMessage
{
    public final XMLStreamReader msg;
    
    public XMLStreamReaderMessage(final Packet properties, final XMLStreamReader msg) {
        super(properties);
        this.msg = msg;
    }
    
    public XMLStreamReaderMessage(final Packet properties, final AttachmentSet attachments, final XMLStreamReader msg) {
        super(properties, attachments);
        this.msg = msg;
    }
}

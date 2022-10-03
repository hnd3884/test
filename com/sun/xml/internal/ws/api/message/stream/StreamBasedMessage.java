package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;

abstract class StreamBasedMessage
{
    public final Packet properties;
    public final AttachmentSet attachments;
    
    protected StreamBasedMessage(final Packet properties) {
        this.properties = properties;
        this.attachments = new AttachmentSetImpl();
    }
    
    protected StreamBasedMessage(final Packet properties, final AttachmentSet attachments) {
        this.properties = properties;
        this.attachments = attachments;
    }
}

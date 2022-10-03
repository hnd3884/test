package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.InputStream;

public class InputStreamMessage extends StreamBasedMessage
{
    public final String contentType;
    public final InputStream msg;
    
    public InputStreamMessage(final Packet properties, final String contentType, final InputStream msg) {
        super(properties);
        this.contentType = contentType;
        this.msg = msg;
    }
    
    public InputStreamMessage(final Packet properties, final AttachmentSet attachments, final String contentType, final InputStream msg) {
        super(properties, attachments);
        this.contentType = contentType;
        this.msg = msg;
    }
}

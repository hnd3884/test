package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;

public interface StreamSOAPCodec extends Codec
{
    @NotNull
    Message decode(@NotNull final XMLStreamReader p0);
    
    @NotNull
    Message decode(@NotNull final XMLStreamReader p0, @NotNull final AttachmentSet p1);
}

package com.oracle.webservices.internal.impl.internalspi.encoding;

import java.io.IOException;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.io.InputStream;

public interface StreamDecoder
{
    Message decode(final InputStream p0, final String p1, final AttachmentSet p2, final SOAPVersion p3) throws IOException;
}

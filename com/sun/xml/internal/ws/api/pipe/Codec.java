package com.sun.xml.internal.ws.api.pipe;

import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.message.Packet;

public interface Codec
{
    String getMimeType();
    
    ContentType getStaticContentType(final Packet p0);
    
    ContentType encode(final Packet p0, final OutputStream p1) throws IOException;
    
    ContentType encode(final Packet p0, final WritableByteChannel p1);
    
    Codec copy();
    
    void decode(final InputStream p0, final String p1, final Packet p2) throws IOException;
    
    void decode(final ReadableByteChannel p0, final String p1, final Packet p2);
}

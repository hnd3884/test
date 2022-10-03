package com.sun.xml.internal.ws.encoding;

import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.istack.internal.NotNull;
import java.io.InputStream;
import com.sun.xml.internal.ws.api.pipe.Codec;

public interface RootOnlyCodec extends Codec
{
    void decode(@NotNull final InputStream p0, @NotNull final String p1, @NotNull final Packet p2, @NotNull final AttachmentSet p3) throws IOException;
    
    void decode(@NotNull final ReadableByteChannel p0, @NotNull final String p1, @NotNull final Packet p2, @NotNull final AttachmentSet p3);
}

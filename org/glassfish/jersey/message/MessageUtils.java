package org.glassfish.jersey.message;

import org.glassfish.jersey.message.internal.ReaderWriter;
import java.nio.charset.Charset;
import javax.ws.rs.core.MediaType;

public final class MessageUtils
{
    public static Charset getCharset(final MediaType media) {
        return ReaderWriter.getCharset(media);
    }
    
    private MessageUtils() {
        throw new AssertionError((Object)"No instances allowed.");
    }
}

package org.apache.tomcat.websocket;

import java.util.List;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.websocket.Extension;

public interface Transformation
{
    void setNext(final Transformation p0);
    
    boolean validateRsvBits(final int p0);
    
    Extension getExtensionResponse();
    
    TransformationResult getMoreData(final byte p0, final boolean p1, final int p2, final ByteBuffer p3) throws IOException;
    
    boolean validateRsv(final int p0, final byte p1);
    
    List<MessagePart> sendMessagePart(final List<MessagePart> p0) throws IOException;
    
    void close();
}

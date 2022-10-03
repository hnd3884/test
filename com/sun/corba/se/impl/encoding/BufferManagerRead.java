package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import java.nio.ByteBuffer;

public interface BufferManagerRead
{
    void processFragment(final ByteBuffer p0, final FragmentMessage p1);
    
    ByteBufferWithInfo underflow(final ByteBufferWithInfo p0);
    
    void init(final Message p0);
    
    MarkAndResetHandler getMarkAndResetHandler();
    
    void cancelProcessing(final int p0);
    
    void close(final ByteBufferWithInfo p0);
}

package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

public interface Pipe
{
    Packet process(final Packet p0);
    
    void preDestroy();
    
    Pipe copy(final PipeCloner p0);
}

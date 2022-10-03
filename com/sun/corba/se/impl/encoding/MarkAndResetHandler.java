package com.sun.corba.se.impl.encoding;

interface MarkAndResetHandler
{
    void mark(final RestorableInputStream p0);
    
    void fragmentationOccured(final ByteBufferWithInfo p0);
    
    void reset();
}

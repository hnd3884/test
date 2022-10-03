package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

interface Data
{
    int size();
    
    byte[] read();
    
    long writeTo(final DataFile p0);
    
    Data createNext(final DataHead p0, final ByteBuffer p1);
}

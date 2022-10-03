package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.io.OutputStream;
import java.io.InputStream;

public interface SharedInputStream
{
    long getPosition();
    
    InputStream newStream(final long p0, final long p1);
    
    void writeTo(final long p0, final long p1, final OutputStream p2);
}

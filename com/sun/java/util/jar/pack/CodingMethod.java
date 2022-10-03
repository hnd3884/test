package com.sun.java.util.jar.pack;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

interface CodingMethod
{
    void readArrayFrom(final InputStream p0, final int[] p1, final int p2, final int p3) throws IOException;
    
    void writeArrayTo(final OutputStream p0, final int[] p1, final int p2, final int p3) throws IOException;
    
    byte[] getMetaCoding(final Coding p0);
}

package org.apache.axiom.ext.io;

import java.io.InputStream;

public interface ReadFromSupport
{
    long readFrom(final InputStream p0, final long p1) throws StreamCopyException;
}

package org.bouncycastle.util.io;

import java.io.IOException;

public class StreamOverflowException extends IOException
{
    public StreamOverflowException(final String s) {
        super(s);
    }
}

package com.maverick.ssh;

import java.io.IOException;

public class SshIOException extends IOException
{
    SshException b;
    
    public SshIOException(final SshException b) {
        this.b = b;
    }
    
    public SshException getRealException() {
        return this.b;
    }
}

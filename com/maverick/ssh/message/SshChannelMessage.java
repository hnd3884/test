package com.maverick.ssh.message;

import java.io.IOException;
import com.maverick.ssh.SshException;

public class SshChannelMessage extends SshMessage
{
    int f;
    
    public SshChannelMessage(final byte[] array) throws SshException {
        super(array);
        try {
            this.f = (int)this.readInt();
        }
        catch (final IOException ex) {
            throw new SshException(5, ex);
        }
    }
    
    int b() {
        return this.f;
    }
}

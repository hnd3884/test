package com.sshtools.net;

import java.io.IOException;
import com.maverick.ssh.SocketTimeoutSupport;
import com.maverick.ssh.SshTransport;
import java.net.Socket;

public class SocketTransport extends Socket implements SshTransport, SocketTimeoutSupport
{
    String m;
    
    public SocketTransport(final String m, final int n) throws IOException {
        super(m, n);
        this.m = m;
        try {
            Socket.class.getMethod("setSendBufferSize", Integer.TYPE).invoke(this, new Integer(65535));
        }
        catch (final Throwable t) {}
        try {
            Socket.class.getMethod("setReceiveBufferSize", Integer.TYPE).invoke(this, new Integer(65535));
        }
        catch (final Throwable t2) {}
    }
    
    public String getHost() {
        return this.m;
    }
    
    public SshTransport duplicate() throws IOException {
        return new SocketTransport(this.getHost(), this.getPort());
    }
}

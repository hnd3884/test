package sun.security.krb5.internal;

import java.io.IOException;

public abstract class NetClient implements AutoCloseable
{
    public static NetClient getInstance(final String s, final String s2, final int n, final int n2) throws IOException {
        if (s.equals("TCP")) {
            return new TCPClient(s2, n, n2);
        }
        return new UDPClient(s2, n, n2);
    }
    
    public abstract void send(final byte[] p0) throws IOException;
    
    public abstract byte[] receive() throws IOException;
    
    @Override
    public abstract void close() throws IOException;
}

package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface DatagramTransport
{
    int getReceiveLimit() throws IOException;
    
    int getSendLimit() throws IOException;
    
    int receive(final byte[] p0, final int p1, final int p2, final int p3) throws IOException;
    
    void send(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void close() throws IOException;
}

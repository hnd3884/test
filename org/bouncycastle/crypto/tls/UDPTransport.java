package org.bouncycastle.crypto.tls;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;

public class UDPTransport implements DatagramTransport
{
    protected static final int MIN_IP_OVERHEAD = 20;
    protected static final int MAX_IP_OVERHEAD = 84;
    protected static final int UDP_OVERHEAD = 8;
    protected final DatagramSocket socket;
    protected final int receiveLimit;
    protected final int sendLimit;
    
    public UDPTransport(final DatagramSocket socket, final int n) throws IOException {
        if (!socket.isBound() || !socket.isConnected()) {
            throw new IllegalArgumentException("'socket' must be bound and connected");
        }
        this.socket = socket;
        this.receiveLimit = n - 20 - 8;
        this.sendLimit = n - 84 - 8;
    }
    
    public int getReceiveLimit() {
        return this.receiveLimit;
    }
    
    public int getSendLimit() {
        return this.sendLimit;
    }
    
    public int receive(final byte[] array, final int n, final int n2, final int soTimeout) throws IOException {
        this.socket.setSoTimeout(soTimeout);
        final DatagramPacket datagramPacket = new DatagramPacket(array, n, n2);
        this.socket.receive(datagramPacket);
        return datagramPacket.getLength();
    }
    
    public void send(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 > this.getSendLimit()) {
            throw new TlsFatalAlert((short)80);
        }
        this.socket.send(new DatagramPacket(array, n, n2));
    }
    
    public void close() throws IOException {
        this.socket.close();
    }
}

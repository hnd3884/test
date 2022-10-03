package org.xbill.DNS;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.net.SocketAddress;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.DatagramChannel;

final class UDPClient extends Client
{
    public UDPClient(final long endTime) throws IOException {
        super(DatagramChannel.open(), endTime);
    }
    
    void bind(final SocketAddress addr) throws IOException {
        final DatagramChannel channel = (DatagramChannel)this.key.channel();
        channel.socket().bind(addr);
    }
    
    void connect(final SocketAddress addr) throws IOException {
        final DatagramChannel channel = (DatagramChannel)this.key.channel();
        channel.connect(addr);
    }
    
    void send(final byte[] data) throws IOException {
        final DatagramChannel channel = (DatagramChannel)this.key.channel();
        Client.verboseLog("UDP write", data);
        channel.write(ByteBuffer.wrap(data));
    }
    
    byte[] recv(final int max) throws IOException {
        final DatagramChannel channel = (DatagramChannel)this.key.channel();
        final byte[] temp = new byte[max];
        this.key.interestOps(1);
        try {
            while (!this.key.isReadable()) {
                Client.blockUntil(this.key, this.endTime);
            }
        }
        finally {
            if (this.key.isValid()) {
                this.key.interestOps(0);
            }
        }
        final long ret = channel.read(ByteBuffer.wrap(temp));
        if (ret <= 0L) {
            throw new EOFException();
        }
        final int len = (int)ret;
        final byte[] data = new byte[len];
        System.arraycopy(temp, 0, data, 0, len);
        Client.verboseLog("UDP read", data);
        return data;
    }
    
    static byte[] sendrecv(final SocketAddress local, final SocketAddress remote, final byte[] data, final int max, final long endTime) throws IOException {
        final UDPClient client = new UDPClient(endTime);
        try {
            if (local != null) {
                client.bind(local);
            }
            client.connect(remote);
            client.send(data);
            return client.recv(max);
        }
        finally {
            client.cleanup();
        }
    }
    
    static byte[] sendrecv(final SocketAddress addr, final byte[] data, final int max, final long endTime) throws IOException {
        return sendrecv(null, addr, data, max, endTime);
    }
}

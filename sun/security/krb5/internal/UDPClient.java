package sun.security.krb5.internal;

import java.net.PortUnreachableException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPClient extends NetClient
{
    InetAddress iaddr;
    int iport;
    int bufSize;
    DatagramSocket dgSocket;
    DatagramPacket dgPacketIn;
    
    UDPClient(final String s, final int iport, final int soTimeout) throws UnknownHostException, SocketException {
        this.bufSize = 65507;
        this.iaddr = InetAddress.getByName(s);
        this.iport = iport;
        (this.dgSocket = new DatagramSocket()).setSoTimeout(soTimeout);
        this.dgSocket.connect(this.iaddr, this.iport);
    }
    
    @Override
    public void send(final byte[] array) throws IOException {
        this.dgSocket.send(new DatagramPacket(array, array.length, this.iaddr, this.iport));
    }
    
    @Override
    public byte[] receive() throws IOException {
        final byte[] array = new byte[this.bufSize];
        this.dgPacketIn = new DatagramPacket(array, array.length);
        try {
            this.dgSocket.receive(this.dgPacketIn);
        }
        catch (final SocketException ex) {
            if (ex instanceof PortUnreachableException) {
                throw ex;
            }
            this.dgSocket.receive(this.dgPacketIn);
        }
        final byte[] array2 = new byte[this.dgPacketIn.getLength()];
        System.arraycopy(this.dgPacketIn.getData(), 0, array2, 0, this.dgPacketIn.getLength());
        return array2;
    }
    
    @Override
    public void close() {
        this.dgSocket.close();
    }
}

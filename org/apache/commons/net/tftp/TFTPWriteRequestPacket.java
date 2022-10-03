package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;

public final class TFTPWriteRequestPacket extends TFTPRequestPacket
{
    public TFTPWriteRequestPacket(final InetAddress destination, final int port, final String filename, final int mode) {
        super(destination, port, 2, filename, mode);
    }
    
    TFTPWriteRequestPacket(final DatagramPacket datagram) throws TFTPPacketException {
        super(2, datagram);
    }
    
    @Override
    public String toString() {
        return super.toString() + " WRQ " + this.getFilename() + " " + TFTP.getModeName(this.getMode());
    }
}

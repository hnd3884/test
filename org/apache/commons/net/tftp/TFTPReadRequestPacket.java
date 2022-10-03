package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;

public final class TFTPReadRequestPacket extends TFTPRequestPacket
{
    public TFTPReadRequestPacket(final InetAddress destination, final int port, final String filename, final int mode) {
        super(destination, port, 1, filename, mode);
    }
    
    TFTPReadRequestPacket(final DatagramPacket datagram) throws TFTPPacketException {
        super(1, datagram);
    }
    
    @Override
    public String toString() {
        return super.toString() + " RRQ " + this.getFilename() + " " + TFTP.getModeName(this.getMode());
    }
}

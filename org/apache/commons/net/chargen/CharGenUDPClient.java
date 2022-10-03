package org.apache.commons.net.chargen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import org.apache.commons.net.DatagramSocketClient;

public final class CharGenUDPClient extends DatagramSocketClient
{
    public static final int SYSTAT_PORT = 11;
    public static final int NETSTAT_PORT = 15;
    public static final int QUOTE_OF_DAY_PORT = 17;
    public static final int CHARGEN_PORT = 19;
    public static final int DEFAULT_PORT = 19;
    private final byte[] __receiveData;
    private final DatagramPacket __receivePacket;
    private final DatagramPacket __sendPacket;
    
    public CharGenUDPClient() {
        this.__receiveData = new byte[512];
        this.__receivePacket = new DatagramPacket(this.__receiveData, this.__receiveData.length);
        this.__sendPacket = new DatagramPacket(new byte[0], 0);
    }
    
    public void send(final InetAddress host, final int port) throws IOException {
        this.__sendPacket.setAddress(host);
        this.__sendPacket.setPort(port);
        this._socket_.send(this.__sendPacket);
    }
    
    public void send(final InetAddress host) throws IOException {
        this.send(host, 19);
    }
    
    public byte[] receive() throws IOException {
        this._socket_.receive(this.__receivePacket);
        final int length;
        final byte[] result = new byte[length = this.__receivePacket.getLength()];
        System.arraycopy(this.__receiveData, 0, result, 0, length);
        return result;
    }
}

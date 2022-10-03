package org.apache.commons.net.ntp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.DatagramSocketClient;

public final class NTPUDPClient extends DatagramSocketClient
{
    public static final int DEFAULT_PORT = 123;
    private int _version;
    
    public NTPUDPClient() {
        this._version = 3;
    }
    
    public TimeInfo getTime(final InetAddress host, final int port) throws IOException {
        if (!this.isOpen()) {
            this.open();
        }
        final NtpV3Packet message = new NtpV3Impl();
        message.setMode(3);
        message.setVersion(this._version);
        final DatagramPacket sendPacket = message.getDatagramPacket();
        sendPacket.setAddress(host);
        sendPacket.setPort(port);
        final NtpV3Packet recMessage = new NtpV3Impl();
        final DatagramPacket receivePacket = recMessage.getDatagramPacket();
        final TimeStamp now = TimeStamp.getCurrentTime();
        message.setTransmitTime(now);
        this._socket_.send(sendPacket);
        this._socket_.receive(receivePacket);
        final long returnTime = System.currentTimeMillis();
        final TimeInfo info = new TimeInfo(recMessage, returnTime, false);
        return info;
    }
    
    public TimeInfo getTime(final InetAddress host) throws IOException {
        return this.getTime(host, 123);
    }
    
    public int getVersion() {
        return this._version;
    }
    
    public void setVersion(final int version) {
        this._version = version;
    }
}

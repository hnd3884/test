package org.apache.commons.net.time;

import java.util.Date;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.commons.net.DatagramSocketClient;

public final class TimeUDPClient extends DatagramSocketClient
{
    public static final int DEFAULT_PORT = 37;
    public static final long SECONDS_1900_TO_1970 = 2208988800L;
    private final byte[] __dummyData;
    private final byte[] __timeData;
    
    public TimeUDPClient() {
        this.__dummyData = new byte[1];
        this.__timeData = new byte[4];
    }
    
    public long getTime(final InetAddress host, final int port) throws IOException {
        final DatagramPacket sendPacket = new DatagramPacket(this.__dummyData, this.__dummyData.length, host, port);
        final DatagramPacket receivePacket = new DatagramPacket(this.__timeData, this.__timeData.length);
        this._socket_.send(sendPacket);
        this._socket_.receive(receivePacket);
        long time = 0L;
        time |= ((long)((this.__timeData[0] & 0xFF) << 24) & 0xFFFFFFFFL);
        time |= ((long)((this.__timeData[1] & 0xFF) << 16) & 0xFFFFFFFFL);
        time |= ((long)((this.__timeData[2] & 0xFF) << 8) & 0xFFFFFFFFL);
        time |= ((long)(this.__timeData[3] & 0xFF) & 0xFFFFFFFFL);
        return time;
    }
    
    public long getTime(final InetAddress host) throws IOException {
        return this.getTime(host, 37);
    }
    
    public Date getDate(final InetAddress host, final int port) throws IOException {
        return new Date((this.getTime(host, port) - 2208988800L) * 1000L);
    }
    
    public Date getDate(final InetAddress host) throws IOException {
        return new Date((this.getTime(host, 37) - 2208988800L) * 1000L);
    }
}

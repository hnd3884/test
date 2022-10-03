package com.zoho.net.handshake;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.Serializable;

public class HandShakePacket implements Serializable
{
    private static final long serialVersionUID = 4777879186453183038L;
    private String serverHostName;
    private String serverHome;
    private long handShakeServerID;
    private int handShakeServerPort;
    private String message;
    public static final String UNKNOWN_PING_MESSAGE = "UNKNOWN_PING_MESSAGE";
    public static final String ALIVE = "ALIVE";
    public static final String PING = "PING";
    private static final String FORMAT = "%-28s:%s\n";
    
    public HandShakePacket() {
        this.serverHostName = null;
        this.handShakeServerID = 0L;
        this.message = null;
        try {
            this.setServerHostName(InetAddress.getLocalHost().getHostName());
            this.setHandShakeServerPort(HandShakeServer.getServerListeningPort());
        }
        catch (final UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    public String getServerHostName() {
        return this.serverHostName;
    }
    
    public void setServerHome(final String serverHome) {
        this.serverHome = serverHome;
    }
    
    void setServerHostName(final String serverHostName) {
        this.serverHostName = serverHostName;
    }
    
    public String getServerHome() {
        return this.serverHome;
    }
    
    public int getHandShakeServerPort() {
        return this.handShakeServerPort;
    }
    
    void setHandShakeServerPort(final int serverPort) {
        this.handShakeServerPort = serverPort;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    void setResposeMessage(final String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append("HandShake response details");
        buff.append("\n--------------------------\n");
        buff.append(String.format("%-28s:%s\n", "SERVER_HOME", this.getServerHome()));
        buff.append(String.format("%-28s:%s\n", "SERVER_HOST_NAME", this.getServerHostName()));
        buff.append(String.format("%-28s:%s\n", "SERVER_ID", this.getHandShakeServerID()));
        buff.append(String.format("%-28s:%s\n", "HANDSHAKE_SERVER_PORT", this.getHandShakeServerPort()));
        buff.append(String.format("%-28s:%s\n", "HANDSHAKE_RESPONSE_MESSAGE", this.getMessage()));
        return buff.toString();
    }
    
    public long getHandShakeServerID() {
        return this.handShakeServerID;
    }
    
    public void setHandShakeServerID(final long handShakeServerID) {
        this.handShakeServerID = handShakeServerID;
    }
}

package com.zoho.net.handshake;

import java.net.InetAddress;
import java.io.IOException;
import java.util.logging.Logger;
import java.net.Socket;

public class HandShakeClient
{
    private Socket socket;
    private SendToServer sendToServer;
    private RecvFromServer recvFromServer;
    private static final Logger LOGGER;
    
    HandShakeClient(final String serverHostName, final int serverHandShakePort) throws IOException {
        this.sendToServer = null;
        this.recvFromServer = null;
        this.init(serverHostName, serverHandShakePort);
    }
    
    private void init(final String serverHostName, final int serverHandShakePort) throws IOException {
        try {
            (this.socket = new Socket(serverHostName, serverHandShakePort)).setSoTimeout(Integer.parseInt(System.getProperty("handshakeclient.sotimeout", "10000")));
            HandShakeClient.LOGGER.info("Connection established!!!");
            this.sendToServer = new SendToServer(this.socket);
            this.recvFromServer = new RecvFromServer(this.socket);
        }
        catch (final IOException e) {
            final IOException ioException = new IOException("Exception while connecting HandShakeServer!!!");
            ioException.initCause(e);
            throw ioException;
        }
    }
    
    public HandShakePacket getPingMessage(final String message) throws IOException, ClassNotFoundException {
        HandShakeClient.LOGGER.info("Sending message :: " + message);
        this.sendToServer.sendMessage(message);
        return this.recvFromServer.getMessage();
    }
    
    public HandShakeClient validateClient(final String hostName, final int port) throws IOException, ClassNotFoundException {
        this.sendToServer.sendMessage("PING");
        final String serverHostName = this.recvFromServer.getMessage().getServerHostName();
        HandShakeClient.LOGGER.fine(" server host:: " + serverHostName + "  local : " + InetAddress.getLocalHost().getHostName());
        if (serverHostName == null || !InetAddress.getLocalHost().getHostName().equalsIgnoreCase(serverHostName)) {
            throw new IOException("Could not connect server from this client.");
        }
        return new HandShakeClient(hostName, port);
    }
    
    public HandShakePacket getPingMessageAndExit(final String message) throws IOException, ClassNotFoundException {
        final HandShakePacket responsePacket = this.getPingMessage(message);
        this.close();
        return responsePacket;
    }
    
    public void close() {
        try {
            this.recvFromServer.close();
        }
        catch (final IOException ex) {}
        this.sendToServer.close();
        try {
            this.socket.close();
        }
        catch (final IOException ex2) {}
    }
    
    static {
        LOGGER = Logger.getLogger(HandShakeClient.class.getName());
    }
}

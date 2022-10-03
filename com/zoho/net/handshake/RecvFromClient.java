package com.zoho.net.handshake;

import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

class RecvFromClient implements Callable<Boolean>
{
    Socket sock;
    ObjectInputStream fromClient;
    SendToClient send;
    private static final Logger LOGGER;
    private static HandShakeServerMessageHandler responsedHandler;
    
    public RecvFromClient(final Socket cs) {
        RecvFromClient.LOGGER.info("New HandShakeClient connection established");
        this.send = new SendToClient(cs);
        this.sock = cs;
    }
    
    @Override
    public Boolean call() {
        try {
            HandShakePacket packet = null;
            if (this.fromClient == null) {
                this.fromClient = new ObjectInputStream(this.sock.getInputStream());
            }
            while ((packet = (HandShakePacket)this.fromClient.readObject()) != null && !packet.getMessage().equalsIgnoreCase("bye")) {
                RecvFromClient.LOGGER.info("HandShake message received :: " + packet.getMessage());
                String responseMessage = HandShakeServer.getConfiguredResponseMessage(packet);
                if (responseMessage.equals("UNKNOWN_PING_MESSAGE")) {
                    responseMessage = getMessageFromHandler(packet);
                }
                this.send.reply(responseMessage, RecvFromClient.responsedHandler);
                RecvFromClient.responsedHandler = null;
            }
        }
        catch (final Throwable e) {
            return Boolean.FALSE;
        }
        finally {
            try {
                RecvFromClient.LOGGER.info("Closing HandShakeClient connection.");
                this.fromClient.close();
                this.sock.close();
            }
            catch (final IOException ex) {}
        }
        return Boolean.TRUE;
    }
    
    static String getMessageFromHandler(final HandShakePacket packetFromClient) {
        for (final HandShakeServerMessageHandler handler : HandShakeServer.getMessagehandlers()) {
            final String responseMessage = handler.getResponseMessage(packetFromClient);
            if (responseMessage != null && !responseMessage.equals("UNKNOWN_PING_MESSAGE")) {
                RecvFromClient.responsedHandler = handler;
                return responseMessage;
            }
        }
        return "UNKNOWN_PING_MESSAGE";
    }
    
    static {
        LOGGER = Logger.getLogger(HandShakeServer.class.getName());
        RecvFromClient.responsedHandler = null;
    }
}

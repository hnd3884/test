package com.adventnet.mfw;

import java.util.logging.Level;
import com.zoho.net.handshake.HandShakePacket;
import com.zoho.net.handshake.HandShakeServerMessageHandler;

class ShutdownListener implements HandShakeServerMessageHandler
{
    private ServerInterface server;
    
    public ShutdownListener(final ServerInterface server) {
        this.server = server;
    }
    
    public String getResponseMessage(final HandShakePacket packetFromClient) {
        if (packetFromClient.getMessage().equals("ShutDownML")) {
            System.out.println("ShutDown signal received.");
            return "SHUTDOWN_SIGNAL_RECEIVED";
        }
        return "UNKNOWN_PING_MESSAGE";
    }
    
    public void responsePacketSent(final HandShakePacket packetSentToClient) {
        System.out.println(packetSentToClient);
        if (packetSentToClient.getMessage().equals("SHUTDOWN_SIGNAL_RECEIVED")) {
            System.out.println("Server shutdown using stop script");
            Starter.extshutdown = true;
            Starter.getLogger().log(Level.INFO, "ShutDownML is received");
            try {
                this.server.shutDown(true);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}

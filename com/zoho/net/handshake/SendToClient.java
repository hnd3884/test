package com.zoho.net.handshake;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;
import java.util.logging.Logger;
import java.io.ObjectOutput;
import java.net.Socket;

class SendToClient
{
    Socket sock;
    ObjectOutput toClient;
    private static final Logger LOGGER;
    
    public SendToClient(final Socket cs) {
        this.sock = cs;
    }
    
    public void reply(final String responseMessage, final HandShakeServerMessageHandler handler) throws IOException {
        final HandShakePacket responsePacket = new HandShakePacket();
        responsePacket.setHandShakeServerID(HandShakeServer.getServerStartedTime());
        responsePacket.setResposeMessage(responseMessage);
        responsePacket.setServerHome(new File(".").getAbsolutePath() + File.separator + "..");
        if (this.toClient == null) {
            this.toClient = new ObjectOutputStream(this.sock.getOutputStream());
        }
        SendToClient.LOGGER.info("Sending response to HandShakeClient.");
        this.toClient.writeObject(responsePacket);
        this.toClient.flush();
        handler.responsePacketSent(responsePacket);
    }
    
    static {
        LOGGER = Logger.getLogger(HandShakeServer.class.getName());
    }
}

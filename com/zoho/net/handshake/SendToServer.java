package com.zoho.net.handshake;

import java.io.IOException;
import java.util.logging.Logger;
import java.io.ObjectOutputStream;
import java.net.Socket;

class SendToServer
{
    Socket sock;
    ObjectOutputStream toServ;
    private static final Logger LOGGER;
    
    protected SendToServer(final Socket cs) throws IOException {
        this.sock = cs;
    }
    
    protected void sendMessage(final String message) throws IOException {
        try {
            if (message != null) {
                if (this.toServ == null) {
                    this.toServ = new ObjectOutputStream(this.sock.getOutputStream());
                }
                SendToServer.LOGGER.info("Sending message to HandShakeServer");
                final HandShakePacket packet = new HandShakePacket();
                packet.setResposeMessage(message);
                this.toServ.writeObject(packet);
                this.toServ.flush();
            }
        }
        catch (final IOException e) {
            final IOException ioException = new IOException("Exception while sending message to HandShakeServer!!!");
            ioException.initCause(e);
            throw ioException;
        }
    }
    
    protected void close() {
        try {
            this.toServ.close();
        }
        catch (final IOException ex) {}
    }
    
    static {
        LOGGER = Logger.getLogger(HandShakeClient.class.getName());
    }
}

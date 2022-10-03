package com.zoho.net.handshake;

import java.io.IOException;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.net.Socket;

class RecvFromServer
{
    Socket sock;
    ObjectInputStream fromServ;
    private static final Logger LOGGER;
    
    protected RecvFromServer(final Socket cs) throws IOException {
        this.sock = cs;
    }
    
    protected HandShakePacket getMessage() throws IOException, ClassNotFoundException {
        try {
            if (this.fromServ == null) {
                this.fromServ = new ObjectInputStream(this.sock.getInputStream());
            }
            RecvFromServer.LOGGER.info("Receiving message from HandShakeServer");
            return (HandShakePacket)this.fromServ.readObject();
        }
        catch (final IOException e) {
            final IOException ioException = new IOException("Exception while receiving message from HandShakeServer!!!");
            ioException.initCause(e);
            throw ioException;
        }
    }
    
    protected void close() throws IOException {
        this.fromServ.close();
    }
    
    static {
        LOGGER = Logger.getLogger(HandShakeClient.class.getName());
    }
}

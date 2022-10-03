package com.me.devicemanagement.framework.server.websockets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

class TCPSession
{
    private Socket tcpSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static Logger wsFrameworkLogger;
    
    TCPSession(final Socket tcpSocket) throws Exception {
        try {
            this.tcpSocket = tcpSocket;
            this.inputStream = tcpSocket.getInputStream();
            this.outputStream = tcpSocket.getOutputStream();
        }
        catch (final Exception ex) {
            TCPSession.wsFrameworkLogger.log(Level.SEVERE, "TCPSocket -> Exception while instantiating TCPSocket", ex);
            try {
                tcpSocket.close();
            }
            catch (final IOException ioe) {
                TCPSession.wsFrameworkLogger.log(Level.SEVERE, "TCPSocket -> Exception while closing Socket", ioe);
            }
            throw ex;
        }
    }
    
    Socket getSocket() {
        return this.tcpSocket;
    }
    
    InputStream getInputStream() {
        return this.inputStream;
    }
    
    OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    void closeSocket() throws IOException {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.outputStream != null) {
                this.outputStream.flush();
                this.outputStream.close();
            }
            if (this.tcpSocket != null && this.tcpSocket.isConnected()) {
                this.tcpSocket.close();
            }
        }
        catch (final IOException ioe) {
            TCPSession.wsFrameworkLogger.log(Level.SEVERE, "TCPSocket::closeSocket -> Exception while closing Socket", ioe);
            throw ioe;
        }
    }
    
    static {
        TCPSession.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}

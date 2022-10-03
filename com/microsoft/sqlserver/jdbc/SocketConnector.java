package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.InetSocketAddress;
import java.net.Socket;

final class SocketConnector implements Runnable
{
    private final Socket socket;
    private final SocketFinder socketFinder;
    private final InetSocketAddress inetSocketAddress;
    private final int timeoutInMilliseconds;
    private static final Logger logger;
    private final String traceID;
    private final String threadID;
    private static long lastThreadID;
    
    SocketConnector(final Socket socket, final InetSocketAddress inetSocketAddress, final int timeOutInMilliSeconds, final SocketFinder socketFinder) {
        this.socket = socket;
        this.inetSocketAddress = inetSocketAddress;
        this.timeoutInMilliseconds = timeOutInMilliSeconds;
        this.socketFinder = socketFinder;
        this.threadID = Long.toString(nextThreadID());
        this.traceID = "SocketConnector:" + this.threadID + "(" + socketFinder.toString() + ")";
    }
    
    @Override
    public void run() {
        IOException exception = null;
        final SocketFinder.Result result = this.socketFinder.getResult();
        if (result.equals(SocketFinder.Result.UNKNOWN)) {
            try {
                if (SocketConnector.logger.isLoggable(Level.FINER)) {
                    SocketConnector.logger.finer(this.toString() + " connecting to InetSocketAddress:" + this.inetSocketAddress + " with timeout:" + this.timeoutInMilliseconds);
                }
                this.socket.connect(this.inetSocketAddress, this.timeoutInMilliseconds);
            }
            catch (final IOException ex) {
                if (SocketConnector.logger.isLoggable(Level.FINER)) {
                    SocketConnector.logger.finer(this.toString() + " exception:" + ex.getClass() + " with message:" + ex.getMessage() + " occurred while connecting to InetSocketAddress:" + this.inetSocketAddress);
                }
                exception = ex;
            }
            this.socketFinder.updateResult(this.socket, exception, this.toString());
        }
    }
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    private static synchronized long nextThreadID() {
        if (SocketConnector.lastThreadID == Long.MAX_VALUE) {
            if (SocketConnector.logger.isLoggable(Level.FINER)) {
                SocketConnector.logger.finer("Resetting the Id count");
            }
            SocketConnector.lastThreadID = 1L;
        }
        else {
            ++SocketConnector.lastThreadID;
        }
        return SocketConnector.lastThreadID;
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SocketConnector");
        SocketConnector.lastThreadID = 0L;
    }
}

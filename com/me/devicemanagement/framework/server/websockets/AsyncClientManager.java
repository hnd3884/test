package com.me.devicemanagement.framework.server.websockets;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AsyncClientManager extends ClientManager
{
    private AsyncSocket clientSocket;
    private boolean isSocketReadyHandled;
    private Object socketStatusObj;
    private static Logger wsFrameworkLogger;
    
    public AsyncClientManager(final ClientDetails clientObj) {
        super(clientObj);
        this.connectionMode = Constants.ClientConnentionMode.ASYNC_READ.ordinal();
        this.clientSocket = new WSAsyncSocket(clientObj.socketSessionObject, clientObj.clientId);
        this.isSocketReadyHandled = false;
        this.socketStatusObj = new Object();
    }
    
    @Override
    public Socket getClientSocket() {
        return this.clientSocket;
    }
    
    public void handleSocketReady() {
        super.handleSocketReady();
        this.isSocketReadyHandled = true;
        synchronized (this.socketStatusObj) {
            this.socketStatusObj.notify();
        }
    }
    
    @Override
    public void handleTextMessage(final String textData) {
        if (!this.isSocketReadyHandled) {
            try {
                synchronized (this.socketStatusObj) {
                    this.socketStatusObj.wait();
                }
            }
            catch (final InterruptedException ex) {
                AsyncClientManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while waiting for onSocketReady", ex);
            }
        }
        this.onTextMessage(textData);
    }
    
    @Override
    public void handleBinaryMessage(final byte[] binaryData) {
        if (!this.isSocketReadyHandled) {
            try {
                synchronized (this.socketStatusObj) {
                    this.socketStatusObj.wait();
                }
            }
            catch (final InterruptedException ex) {
                AsyncClientManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while waiting for onSocketReady", ex);
            }
        }
        this.onBinaryMessage(binaryData);
    }
    
    public abstract void onTextMessage(final String p0);
    
    public abstract void onBinaryMessage(final byte[] p0);
    
    static {
        AsyncClientManager.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}

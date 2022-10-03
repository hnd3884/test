package com.me.devicemanagement.framework.server.websockets;

public abstract class SyncClientManager extends ClientManager
{
    private SyncSocket clientSocket;
    
    public SyncClientManager(final ClientDetails clientObj) {
        super(clientObj);
        this.connectionMode = Constants.ClientConnentionMode.BLOCKING_READ.ordinal();
        if (clientObj.socketType == Constants.ClientSocketType.WEBSOCKET.ordinal()) {
            this.clientSocket = new WSSyncSocket(clientObj.socketSessionObject, clientObj.clientId);
        }
        else if (clientObj.socketType == Constants.ClientSocketType.TCP.ordinal()) {
            this.clientSocket = new TCPSyncSocket(clientObj.socketSessionObject, this);
        }
    }
    
    @Override
    public Socket getClientSocket() {
        return this.clientSocket;
    }
    
    @Override
    public void handleTextMessage(final String textData) {
        this.clientSocket.addDataToIncomingDataQueue(textData);
    }
    
    @Override
    public void handleBinaryMessage(final byte[] binaryData) {
        this.clientSocket.addDataToIncomingDataQueue(binaryData);
    }
}

package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.OutputStream;

public class TCPSocketImpl implements Socket
{
    protected static final int MAX_COMMAND_LENGTH = 100;
    protected static final int SO_LINGER_TIMEOUT = 1;
    protected TCPSession tcpSession;
    private OutputStream outputStream;
    private Object queueMutexObject;
    private boolean isSecureMode;
    private String clientIp;
    private boolean isOutgoingDataThreadRunning;
    private boolean isCloseNotified;
    private ConcurrentLinkedQueue<Object> outgoingDataQueue;
    protected ClientManager clientMgr;
    protected static Logger wsFrameworkLogger;
    
    public TCPSocketImpl(final Object tcpSession, final ClientManager clientMgr) {
        try {
            this.isCloseNotified = false;
            this.clientMgr = clientMgr;
            this.tcpSession = (TCPSession)tcpSession;
            this.clientIp = this.tcpSession.getSocket().getInetAddress().toString();
            this.outputStream = this.tcpSession.getOutputStream();
            this.isSecureMode = true;
            this.tcpSession.getSocket().setSoLinger(true, 1);
        }
        catch (final Exception ex) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while Creating TCPSocketImpl Object", ex);
            this.performInternalSocketCleanup("TCPSocketImpl - Error while initializing");
        }
    }
    
    @Override
    public void sendString(final String msgString, final boolean isBlocking) throws Exception {
        if (this.tcpSession.getSocket().isConnected()) {
            if (!isBlocking) {
                if (!this.isOutgoingDataThreadRunning) {
                    this.startOutgoingDataSenderTask();
                }
                this.addDataToQueue(msgString);
            }
            else {
                this.sendString(msgString);
            }
            return;
        }
        TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendString: Throwing Socket already closed exception.");
        this.performInternalSocketCleanup("sendString: Socket Already Closed");
        throw new SocketFrameworkException("Socket Already closed");
    }
    
    private void sendString(final String msgString) throws Exception {
        try {
            if (msgString != null && msgString.length() > 0) {
                final byte[] buffer = msgString.getBytes("ASCII");
                this.outputStream.write(buffer);
                this.outputStream.flush();
            }
            else {
                TCPSocketImpl.wsFrameworkLogger.log(Level.INFO, "sendString - MsgString passed is NULL");
            }
        }
        catch (final Exception ex) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while Sending String", ex);
            this.performInternalSocketCleanup("sendString: Exception - " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public void sendBytes(final byte[] msgBinary, final boolean isBlocking) throws Exception {
        if (this.tcpSession.getSocket().isConnected()) {
            if (!isBlocking) {
                if (!this.isOutgoingDataThreadRunning) {
                    this.startOutgoingDataSenderTask();
                }
                this.addDataToQueue(msgBinary);
            }
            else {
                this.sendBytes(msgBinary);
            }
            return;
        }
        TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendBytes: Throwing Socket already closed exception.");
        this.performInternalSocketCleanup("sendBytes: Socket Already Closed");
        throw new SocketFrameworkException("Socket Already closed");
    }
    
    private void sendBytes(final byte[] byteData) throws Exception {
        try {
            if (byteData != null && byteData.length != 0) {
                this.outputStream.write(byteData);
                this.outputStream.flush();
            }
            else {
                TCPSocketImpl.wsFrameworkLogger.log(Level.INFO, "sendBytes: Byte Data passed is NULL");
            }
        }
        catch (final Exception ex) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendBytes: Exception while sending byte data", ex);
            this.performInternalSocketCleanup("sendBytes: Exception - " + ex.getMessage());
            throw ex;
        }
    }
    
    @Override
    public Future<Void> sendStringAsync(final String msgString) throws Exception {
        return null;
    }
    
    @Override
    public Future<Void> sendBytesAsync(final byte[] msgBinary) throws Exception {
        return null;
    }
    
    @Override
    public boolean isSecureConnection() throws Exception {
        return this.isSecureMode;
    }
    
    @Override
    public String getClientIpAddress() {
        return this.clientIp;
    }
    
    @Override
    public void closeSocket() throws Exception {
        try {
            this.performInternalSocketCleanup("closeSocket : Normal Closure");
        }
        catch (final Exception ex) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while closing the socket", ex);
            throw ex;
        }
    }
    
    @Override
    public void setMaxIdleTime(final Long timeInMilliSeconds) throws Exception {
    }
    
    @Override
    public long getMaxIdleTime() throws Exception {
        return 0L;
    }
    
    private void addDataToQueue(final Object data) {
        if (data != null) {
            this.outgoingDataQueue.add(data);
            synchronized (this.queueMutexObject) {
                this.queueMutexObject.notify();
            }
            TCPSocketImpl.wsFrameworkLogger.log(Level.FINE, "Data added to the queue");
        }
        else {
            TCPSocketImpl.wsFrameworkLogger.log(Level.WARNING, "Data is null");
        }
    }
    
    private void startOutgoingDataSenderTask() {
        this.queueMutexObject = new Object();
        this.outgoingDataQueue = new ConcurrentLinkedQueue<Object>();
        this.isOutgoingDataThreadRunning = true;
        final Properties outgoingDataSenderProps = new Properties();
        ((Hashtable<String, Long>)outgoingDataSenderProps).put("clientId", this.clientMgr.getClientId());
        ((Hashtable<String, Constants.ClientSocketType>)outgoingDataSenderProps).put("socketType", Constants.ClientSocketType.TCP);
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "OutgoingDataSender");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "wsOutgoingDataPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(OutgoingDataSender.class.getName(), taskInfoMap, outgoingDataSenderProps);
        }
        catch (final Exception ex) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while starting outgoing data sender", ex);
        }
    }
    
    void sendDataFromQueue() {
        Object data = null;
        try {
            while (this.isOutgoingDataThreadRunning) {
                data = this.outgoingDataQueue.peek();
                if (data != null && data.getClass().getSimpleName().equals("String")) {
                    final String strData = (String)data;
                    this.sendString(strData);
                    this.outgoingDataQueue.remove();
                }
                else if (data != null && data.getClass().getSimpleName().equals("byte[]")) {
                    final byte[] binData = (byte[])data;
                    this.sendBytes(binData);
                    this.outgoingDataQueue.remove();
                }
                else if (data == null) {
                    try {
                        synchronized (this.queueMutexObject) {
                            this.queueMutexObject.wait();
                        }
                    }
                    catch (final InterruptedException ex) {
                        TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendDataFromQueue -> Exception while waiting for the queue data", ex);
                    }
                }
                else {
                    TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendDataFromQueue -> Unhandled data type  while sending data");
                }
            }
        }
        catch (final Exception ex2) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendDataFromQueue -> Exception while reading/sending data from the queue", ex2);
            this.performInternalSocketCleanup("sendDataFromQueue Exception - " + ex2.getMessage());
        }
    }
    
    private void stopOutgoingDataThread() {
        if (this.isOutgoingDataThreadRunning) {
            this.isOutgoingDataThreadRunning = false;
            synchronized (this.queueMutexObject) {
                this.queueMutexObject.notify();
            }
        }
    }
    
    void performInternalSocketCleanup(final String closeReason) {
        try {
            if (this.tcpSession != null && this.tcpSession.getSocket().isConnected()) {
                this.tcpSession.closeSocket();
            }
        }
        catch (final Exception ex) {
            TCPSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while Closing Socket - ", ex);
        }
        this.stopOutgoingDataThread();
        if (!this.isCloseNotified && this.clientMgr != null) {
            this.isCloseNotified = true;
            TCPSocketImpl.wsFrameworkLogger.log(Level.INFO, "performInternalSocketCleanup: Trigerring OnSocketClose with reason - " + closeReason);
            this.clientMgr.handleSocketClose(closeReason);
            ClientAccessLogger.logClientAccess(String.valueOf(this.clientMgr.getClientId()), this.clientMgr.getRequestParamsMap().get("clientName"), this.clientMgr.getClientType(), "DISCONNECTED", Constants.ClientConnentionMode.BLOCKING_READ.ordinal(), Constants.ClientSocketType.TCP.ordinal(), closeReason);
        }
    }
    
    static {
        TCPSocketImpl.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}

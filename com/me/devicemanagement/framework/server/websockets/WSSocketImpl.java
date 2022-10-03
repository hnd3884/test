package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import java.io.IOException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.RemoteEndpoint;

public class WSSocketImpl implements Socket
{
    private RemoteEndpoint.Basic basicRemote;
    private RemoteEndpoint.Async asyncRemote;
    private boolean isOutgoingDataThreadRunning;
    private ConcurrentLinkedQueue<Object> outgoingDataQueue;
    private Object queueMutexObject;
    private Object basicRemoteMutex;
    protected boolean isWSSocketOpened;
    protected Session wsSocketSession;
    protected String clientTypeParam;
    private Long clientId;
    protected static Logger wsFrameworkLogger;
    
    public WSSocketImpl(final Object wsSocketSession, final Long clientId) {
        this.wsSocketSession = (Session)wsSocketSession;
        this.clientId = clientId;
        this.basicRemote = this.wsSocketSession.getBasicRemote();
        this.asyncRemote = this.wsSocketSession.getAsyncRemote();
        this.isOutgoingDataThreadRunning = false;
        this.basicRemoteMutex = new Object();
        this.isWSSocketOpened = this.wsSocketSession.isOpen();
        this.clientTypeParam = this.wsSocketSession.getPathParameters().get("clientType");
    }
    
    @Override
    public void sendString(final String msgString, final boolean isBlocking) throws Exception {
        if (this.isWSSocketOpened) {
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
        WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendString: Throwing Socket Already Closed Exception.");
        throw new SocketFrameworkException("Socket already closed");
    }
    
    private void sendString(final String msgString) throws Exception {
        if (msgString != null) {
            try {
                synchronized (this.basicRemoteMutex) {
                    if (this.isWSSocketOpened) {
                        this.basicRemote.sendText(msgString);
                        WSSocketImpl.wsFrameworkLogger.log(Level.FINE, "Send -> " + this.clientTypeParam + " - String - " + msgString);
                    }
                }
                return;
            }
            catch (final Exception ex) {
                WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while sending string data to " + this.clientTypeParam + " synchronously", ex);
                throw ex;
            }
        }
        WSSocketImpl.wsFrameworkLogger.log(Level.WARNING, "String data is null");
    }
    
    @Override
    public Future<Void> sendStringAsync(final String msgString) throws Exception {
        Future<Void> future = null;
        if (msgString != null) {
            try {
                if (this.isWSSocketOpened) {
                    future = this.asyncRemote.sendText(msgString);
                    WSSocketImpl.wsFrameworkLogger.log(Level.FINE, "String data sent to the remote endpoint asynchronously");
                }
                return future;
            }
            catch (final Exception ex) {
                WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while sending string data to " + this.clientTypeParam + " asynchronously", ex);
                throw ex;
            }
        }
        WSSocketImpl.wsFrameworkLogger.log(Level.WARNING, "String data is null");
        return future;
    }
    
    @Override
    public void sendBytes(final byte[] msgBinary, final boolean isBlocking) throws Exception {
        if (this.isWSSocketOpened) {
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
        WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "sendBytes: Throwing Socket Already Closed Exception.");
        throw new SocketFrameworkException("Socket already closed");
    }
    
    private void sendBytes(final byte[] msgBinary) throws Exception {
        if (msgBinary != null) {
            try {
                final ByteBuffer binaryData = ByteBuffer.wrap(msgBinary);
                synchronized (this.basicRemoteMutex) {
                    if (this.isWSSocketOpened) {
                        this.basicRemote.sendBinary(binaryData);
                        WSSocketImpl.wsFrameworkLogger.log(Level.FINE, "Send -> " + this.clientTypeParam + " - Byte - " + msgBinary);
                    }
                }
                return;
            }
            catch (final Exception ex) {
                WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while sending binary data to " + this.clientTypeParam + " synchronously", ex);
                throw ex;
            }
        }
        WSSocketImpl.wsFrameworkLogger.log(Level.WARNING, "Binary data is null");
    }
    
    @Override
    public Future<Void> sendBytesAsync(final byte[] msgBinary) throws Exception {
        Future<Void> future = null;
        if (msgBinary != null) {
            try {
                if (this.isWSSocketOpened) {
                    final ByteBuffer binaryData = ByteBuffer.wrap(msgBinary);
                    future = this.asyncRemote.sendBinary(binaryData);
                    WSSocketImpl.wsFrameworkLogger.log(Level.FINE, "Binary data sent to the remote endpoint asynchronously");
                }
                return future;
            }
            catch (final Exception ex) {
                WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while sending binary data to " + this.clientTypeParam + " asynchronously", ex);
                throw ex;
            }
        }
        WSSocketImpl.wsFrameworkLogger.log(Level.WARNING, "Binary data is null");
        return future;
    }
    
    @Override
    public boolean isSecureConnection() throws Exception {
        boolean isSecure = false;
        try {
            if (this.isWSSocketOpened) {
                isSecure = this.wsSocketSession.isSecure();
            }
        }
        catch (final Exception ex) {
            WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while checking if the socket is secure", ex);
            throw ex;
        }
        return isSecure;
    }
    
    @Override
    public String getClientIpAddress() {
        return null;
    }
    
    @Override
    public void closeSocket() throws Exception {
        try {
            this.performInternalSocketCleanup();
            if (this.wsSocketSession.isOpen()) {
                this.wsSocketSession.close();
            }
        }
        catch (final Exception ex) {
            WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while closing the socket", ex);
            throw ex;
        }
    }
    
    @Override
    public void setMaxIdleTime(final Long timeInMilliSeconds) throws Exception {
        try {
            if (this.wsSocketSession.isOpen()) {
                this.wsSocketSession.setMaxIdleTimeout((long)timeInMilliSeconds);
            }
        }
        catch (final Exception ex) {
            WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while setting the idle time", ex);
            throw ex;
        }
    }
    
    @Override
    public long getMaxIdleTime() throws Exception {
        long maxIdleTime = -1L;
        try {
            if (this.wsSocketSession.isOpen()) {
                maxIdleTime = this.wsSocketSession.getMaxIdleTimeout();
            }
        }
        catch (final Exception ex) {
            WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "Exception while getting the maxIdleTime", ex);
            throw ex;
        }
        return maxIdleTime;
    }
    
    private void addDataToQueue(final Object data) {
        if (data != null) {
            this.outgoingDataQueue.add(data);
            synchronized (this.queueMutexObject) {
                this.queueMutexObject.notify();
            }
            WSSocketImpl.wsFrameworkLogger.log(Level.FINE, "Data added to the queue");
        }
        else {
            WSSocketImpl.wsFrameworkLogger.log(Level.WARNING, "Data is null");
        }
    }
    
    private void startOutgoingDataSenderTask() {
        this.queueMutexObject = new Object();
        this.outgoingDataQueue = new ConcurrentLinkedQueue<Object>();
        if (this.wsSocketSession.isOpen()) {
            this.isOutgoingDataThreadRunning = true;
            final Properties outgoingDataSenderProps = new Properties();
            ((Hashtable<String, Long>)outgoingDataSenderProps).put("clientId", this.clientId);
            ((Hashtable<String, Constants.ClientSocketType>)outgoingDataSenderProps).put("socketType", Constants.ClientSocketType.WEBSOCKET);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "OutgoingDataSender");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "wsOutgoingDataPool");
            try {
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(OutgoingDataSender.class.getName(), taskInfoMap, outgoingDataSenderProps);
            }
            catch (final Exception ex) {
                WSSocketImpl.wsFrameworkLogger.log(Level.WARNING, "Exception occured while starting outgoing data sender", ex);
            }
        }
        else {
            WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, "startOutgoingDataThread: WSSession is already closed. Not starting OutgoingData thread");
        }
    }
    
    void sendDataFromQueue() throws IOException {
        Object data = null;
        try {
            while (this.isOutgoingDataThreadRunning && this.isWSSocketOpened) {
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
                        WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, this.clientTypeParam + " -> Exception while waiting for the queue data", ex);
                    }
                }
                else {
                    WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, this.clientTypeParam + " -> Unhandled data type  while sending data");
                }
            }
            WSSocketImpl.wsFrameworkLogger.log(Level.INFO, this.clientTypeParam + " -> sendDataFromQueue: Outgoing Data thread quitting");
        }
        catch (final Exception ex2) {
            WSSocketImpl.wsFrameworkLogger.log(Level.SEVERE, this.clientTypeParam + " -> Exception while reading/sending data from the queue", ex2);
            this.wsSocketSession.close();
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
    
    void performInternalSocketCleanup() {
        this.isWSSocketOpened = false;
        this.stopOutgoingDataThread();
        WSSocketImpl.wsFrameworkLogger.log(Level.INFO, this.clientTypeParam + " - Internal Socket Cleanup completed.");
    }
    
    static {
        WSSocketImpl.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}

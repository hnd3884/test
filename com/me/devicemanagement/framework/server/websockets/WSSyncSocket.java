package com.me.devicemanagement.framework.server.websockets;

import java.util.logging.Level;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WSSyncSocket extends WSSocketImpl implements SyncSocket
{
    private final Object queueMutexObject;
    private ConcurrentLinkedQueue<Object> incomingDataQueue;
    private Charset conversionCharset;
    
    public WSSyncSocket(final Object wsSocketSession, final Long clientId) {
        super(wsSocketSession, clientId);
        this.conversionCharset = StandardCharsets.UTF_8;
        this.queueMutexObject = new Object();
        this.incomingDataQueue = new ConcurrentLinkedQueue<Object>();
    }
    
    @Override
    public void addDataToIncomingDataQueue(final Object data) {
        if (data != null) {
            this.incomingDataQueue.add(data);
            synchronized (this.queueMutexObject) {
                this.queueMutexObject.notify();
            }
            WSSyncSocket.wsFrameworkLogger.log(Level.FINE, "Data added to the queue");
        }
        else {
            WSSyncSocket.wsFrameworkLogger.log(Level.WARNING, "Data is null");
        }
    }
    
    @Override
    public String readString() throws Exception {
        Object data = null;
        String strData = null;
        try {
            while (data == null) {
                if (!this.isWSSocketOpened) {
                    WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "readString: Throwing Socket already closed exception");
                    throw new SocketFrameworkException("Socket already closed");
                }
                synchronized (this.queueMutexObject) {
                    data = this.incomingDataQueue.peek();
                    if (data == null) {
                        try {
                            this.queueMutexObject.wait();
                        }
                        catch (final InterruptedException iEx) {
                            WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "Exception while waiting for the queue data", iEx);
                        }
                        continue;
                    }
                }
                if (data.getClass().getSimpleName().equals("String")) {
                    strData = (String)data;
                    this.incomingDataQueue.remove();
                }
                else {
                    if (!data.getClass().getSimpleName().equals("byte[]")) {
                        continue;
                    }
                    strData = new String((byte[])data, this.conversionCharset);
                    this.incomingDataQueue.remove();
                }
            }
        }
        catch (final SocketFrameworkException sfEx) {
            WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "readString: Blocking call returning ... since socket is closed");
            throw sfEx;
        }
        catch (final Exception ex) {
            WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "Exception while reading string data from the queue", ex);
            throw ex;
        }
        WSSyncSocket.wsFrameworkLogger.log(Level.FINE, "String data read from the queue");
        return strData;
    }
    
    @Override
    public byte[] readBytes(final int bytesToBeRead) throws Exception {
        Object data = null;
        byte[] binData = null;
        try {
            while (data == null) {
                if (!this.isWSSocketOpened) {
                    WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "readBytes: Throwing Socket already closed exception");
                    throw new SocketFrameworkException("Socket already closed");
                }
                synchronized (this.queueMutexObject) {
                    data = this.incomingDataQueue.peek();
                    if (data == null) {
                        try {
                            this.queueMutexObject.wait();
                        }
                        catch (final InterruptedException iex) {
                            WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "Exception while waiting for the queue data", iex);
                        }
                        continue;
                    }
                }
                if (data.getClass().getSimpleName().equals("byte[]")) {
                    binData = (byte[])data;
                    this.incomingDataQueue.remove();
                }
                else {
                    if (!data.getClass().getSimpleName().equals("String")) {
                        continue;
                    }
                    binData = data.toString().getBytes(this.conversionCharset);
                    this.incomingDataQueue.remove();
                }
            }
        }
        catch (final Exception ex) {
            WSSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "Exception while reading binary data from the queue", ex);
            throw ex;
        }
        WSSyncSocket.wsFrameworkLogger.log(Level.FINE, "Binary data read from the queue");
        return binData;
    }
    
    @Override
    public void setConversionCharset(final Charset conversionCharset) {
        this.conversionCharset = conversionCharset;
    }
    
    @Override
    public Charset getConversionCharset() {
        return this.conversionCharset;
    }
    
    @Override
    public void flushIncomingDataQueue() {
        this.incomingDataQueue.clear();
    }
    
    @Override
    void performInternalSocketCleanup() {
        super.performInternalSocketCleanup();
        synchronized (this.queueMutexObject) {
            this.queueMutexObject.notify();
        }
    }
}

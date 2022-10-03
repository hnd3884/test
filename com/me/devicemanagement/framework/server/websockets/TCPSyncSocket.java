package com.me.devicemanagement.framework.server.websockets;

import java.io.IOException;
import java.util.logging.Level;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.io.InputStream;

public class TCPSyncSocket extends TCPSocketImpl implements SyncSocket
{
    private InputStream inputStream;
    private byte[] readBuffer;
    private int unreadIndex;
    private int bytesRemaining;
    private boolean ignoreInitialSlashN;
    private Charset conversionCharset;
    
    public TCPSyncSocket(final Object tcpSession, final ClientManager clientMgr) {
        super(tcpSession, clientMgr);
        this.conversionCharset = StandardCharsets.UTF_8;
        try {
            this.readBuffer = new byte[100];
            this.inputStream = this.tcpSession.getInputStream();
        }
        catch (final Exception ex) {
            TCPSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "TCPSyncSocket - Exception while initializing TCP Sync Socket", ex);
            this.performInternalSocketCleanup("TCPSocketImpl - Error while initializing");
        }
    }
    
    @Override
    public byte[] readBytes(int bytesToBeRead) throws Exception {
        final byte[] imageBuffer = new byte[bytesToBeRead];
        TCPSyncSocket.wsFrameworkLogger.log(Level.FINEST, "imageBuffer.length -> " + bytesToBeRead + "unreadIndex " + this.unreadIndex + " bytesRemaining " + this.bytesRemaining);
        try {
            int offset;
            if (0 == this.bytesRemaining) {
                offset = 0;
            }
            else {
                TCPSyncSocket.wsFrameworkLogger.log(Level.FINEST, "Image data available with header!");
                if (bytesToBeRead < this.bytesRemaining) {
                    System.arraycopy(this.readBuffer, this.unreadIndex, imageBuffer, 0, bytesToBeRead);
                    this.bytesRemaining -= bytesToBeRead;
                    this.unreadIndex += bytesToBeRead;
                    this.clientMgr.setLastContactTime(System.currentTimeMillis());
                    return imageBuffer;
                }
                System.arraycopy(this.readBuffer, this.unreadIndex, imageBuffer, 0, this.bytesRemaining);
                bytesToBeRead -= this.bytesRemaining;
                offset = this.bytesRemaining;
                final int n = 0;
                this.bytesRemaining = n;
                this.unreadIndex = n;
            }
            int bytesReceived = 0;
            while (true) {
                bytesReceived = this.inputStream.read(imageBuffer, offset, bytesToBeRead);
                if (-1 == bytesReceived) {
                    throw new IOException("Image could not be read fully");
                }
                if (bytesReceived >= bytesToBeRead) {
                    break;
                }
                offset += bytesReceived;
                bytesToBeRead -= bytesReceived;
            }
        }
        catch (final Exception ex) {
            TCPSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "readBytes: Exception occurred while reading binary data", ex);
            this.performInternalSocketCleanup("readBytes: Exception - " + ex.getMessage());
            throw ex;
        }
        this.clientMgr.setLastContactTime(System.currentTimeMillis());
        return imageBuffer;
    }
    
    @Override
    public String readString() throws IOException {
        final StringBuffer dataBuffer = new StringBuffer();
        try {
            while (this.bytesRemaining != 0) {
                if (10 == this.readBuffer[this.unreadIndex]) {
                    ++this.unreadIndex;
                    --this.bytesRemaining;
                    this.clientMgr.setLastContactTime(System.currentTimeMillis());
                    return dataBuffer.toString();
                }
                if (13 == this.readBuffer[this.unreadIndex]) {
                    ++this.unreadIndex;
                    --this.bytesRemaining;
                    if (0 != this.bytesRemaining) {
                        if (10 == this.readBuffer[this.unreadIndex]) {
                            ++this.unreadIndex;
                            --this.bytesRemaining;
                        }
                    }
                    else {
                        this.ignoreInitialSlashN = true;
                    }
                    this.clientMgr.setLastContactTime(System.currentTimeMillis());
                    return dataBuffer.toString();
                }
                if (0 == this.readBuffer[this.unreadIndex]) {
                    ++this.unreadIndex;
                    --this.bytesRemaining;
                }
                else {
                    dataBuffer.append((char)this.readBuffer[this.unreadIndex++]);
                    --this.bytesRemaining;
                }
            }
            int bytesReceived = 0;
            int bytesProcessed = 0;
        Label_0392:
            while (true) {
                bytesReceived = this.inputStream.read(this.readBuffer);
                if (-1 == bytesReceived) {
                    throw new IOException("InputStream is closed");
                }
                if (bytesReceived <= 0) {
                    continue;
                }
                bytesProcessed = 0;
                if (this.ignoreInitialSlashN) {
                    if (10 == this.readBuffer[0]) {
                        ++bytesProcessed;
                        TCPSyncSocket.wsFrameworkLogger.log(Level.FINEST, "Ignored '\\n' in the beginning of readBuffer!");
                    }
                    this.ignoreInitialSlashN = false;
                }
                while (bytesProcessed < bytesReceived) {
                    if (10 == this.readBuffer[bytesProcessed]) {
                        ++bytesProcessed;
                        break Label_0392;
                    }
                    if (13 == this.readBuffer[bytesProcessed]) {
                        if (++bytesProcessed >= bytesReceived) {
                            this.ignoreInitialSlashN = true;
                            break Label_0392;
                        }
                        if (10 == this.readBuffer[bytesProcessed]) {
                            ++bytesProcessed;
                            break Label_0392;
                        }
                        break Label_0392;
                    }
                    else {
                        dataBuffer.append((char)this.readBuffer[bytesProcessed]);
                        ++bytesProcessed;
                    }
                }
            }
            if (bytesProcessed < bytesReceived) {
                this.bytesRemaining = bytesReceived - bytesProcessed;
                this.unreadIndex = bytesProcessed;
            }
            else {
                final int n = 0;
                this.unreadIndex = n;
                this.bytesRemaining = n;
            }
        }
        catch (final Exception ex) {
            TCPSyncSocket.wsFrameworkLogger.log(Level.SEVERE, "readString : Exception while reading string", ex);
            this.performInternalSocketCleanup("Exception in readString - " + ex.getMessage());
            throw ex;
        }
        this.clientMgr.setLastContactTime(System.currentTimeMillis());
        return dataBuffer.toString();
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
    }
    
    @Override
    public void addDataToIncomingDataQueue(final Object data) {
    }
}

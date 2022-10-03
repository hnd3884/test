package com.me.devicemanagement.framework.server.websockets;

import java.nio.charset.Charset;

public interface SyncSocket extends Socket
{
    String readString() throws Exception;
    
    byte[] readBytes(final int p0) throws Exception;
    
    void flushIncomingDataQueue();
    
    void addDataToIncomingDataQueue(final Object p0);
    
    Charset getConversionCharset();
    
    void setConversionCharset(final Charset p0);
}

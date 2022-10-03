package com.me.devicemanagement.framework.server.queue;

public interface DCQueueHelper
{
    String readFile(final String p0) throws Exception;
    
    boolean deleteFile(final String p0) throws Exception;
    
    void deleteDBEntry(final DCQueueData p0, final boolean p1, final DCQueueMetaData p2) throws Exception;
    
    String unCompressString(final DCQueueData p0);
}

package org.apache.tomcat.util.net;

public abstract class SendfileDataBase
{
    public SendfileKeepAliveState keepAliveState;
    public final String fileName;
    public long pos;
    public long length;
    
    public SendfileDataBase(final String filename, final long pos, final long length) {
        this.keepAliveState = SendfileKeepAliveState.NONE;
        this.fileName = filename;
        this.pos = pos;
        this.length = length;
    }
}

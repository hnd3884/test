package com.maverick.ssh;

public class ChannelOpenException extends Exception
{
    public static final int ADMINISTRATIVIVELY_PROHIBITED = 1;
    public static final int CONNECT_FAILED = 2;
    public static final int UNKNOWN_CHANNEL_TYPE = 3;
    public static final int RESOURCE_SHORTAGE = 4;
    int b;
    
    public ChannelOpenException(final String s, final int b) {
        super(s);
        this.b = b;
    }
    
    public int getReason() {
        return this.b;
    }
}

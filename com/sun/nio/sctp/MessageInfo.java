package com.sun.nio.sctp;

import sun.nio.ch.sctp.MessageInfoImpl;
import java.net.SocketAddress;
import jdk.Exported;

@Exported
public abstract class MessageInfo
{
    protected MessageInfo() {
    }
    
    public static MessageInfo createOutgoing(final SocketAddress socketAddress, final int n) {
        if (n < 0 || n > 65536) {
            throw new IllegalArgumentException("Invalid stream number");
        }
        return new MessageInfoImpl(null, socketAddress, n);
    }
    
    public static MessageInfo createOutgoing(final Association association, final SocketAddress socketAddress, final int n) {
        if (association == null) {
            throw new IllegalArgumentException("association cannot be null");
        }
        if (n < 0 || n > 65536) {
            throw new IllegalArgumentException("Invalid stream number");
        }
        return new MessageInfoImpl(association, socketAddress, n);
    }
    
    public abstract SocketAddress address();
    
    public abstract Association association();
    
    public abstract int bytes();
    
    public abstract boolean isComplete();
    
    public abstract MessageInfo complete(final boolean p0);
    
    public abstract boolean isUnordered();
    
    public abstract MessageInfo unordered(final boolean p0);
    
    public abstract int payloadProtocolID();
    
    public abstract MessageInfo payloadProtocolID(final int p0);
    
    public abstract int streamNumber();
    
    public abstract MessageInfo streamNumber(final int p0);
    
    public abstract long timeToLive();
    
    public abstract MessageInfo timeToLive(final long p0);
}

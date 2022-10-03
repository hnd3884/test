package org.ietf.jgss;

import java.util.Arrays;
import java.net.InetAddress;

public class ChannelBinding
{
    private InetAddress initiator;
    private InetAddress acceptor;
    private byte[] appData;
    
    public ChannelBinding(final InetAddress initiator, final InetAddress acceptor, final byte[] array) {
        this.initiator = initiator;
        this.acceptor = acceptor;
        if (array != null) {
            System.arraycopy(array, 0, this.appData = new byte[array.length], 0, array.length);
        }
    }
    
    public ChannelBinding(final byte[] array) {
        this(null, null, array);
    }
    
    public InetAddress getInitiatorAddress() {
        return this.initiator;
    }
    
    public InetAddress getAcceptorAddress() {
        return this.acceptor;
    }
    
    public byte[] getApplicationData() {
        if (this.appData == null) {
            return null;
        }
        final byte[] array = new byte[this.appData.length];
        System.arraycopy(this.appData, 0, array, 0, this.appData.length);
        return array;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChannelBinding)) {
            return false;
        }
        final ChannelBinding channelBinding = (ChannelBinding)o;
        return (this.initiator == null || channelBinding.initiator != null) && (this.initiator != null || channelBinding.initiator == null) && (this.initiator == null || this.initiator.equals(channelBinding.initiator)) && (this.acceptor == null || channelBinding.acceptor != null) && (this.acceptor != null || channelBinding.acceptor == null) && (this.acceptor == null || this.acceptor.equals(channelBinding.acceptor)) && Arrays.equals(this.appData, channelBinding.appData);
    }
    
    @Override
    public int hashCode() {
        if (this.initiator != null) {
            return this.initiator.hashCode();
        }
        if (this.acceptor != null) {
            return this.acceptor.hashCode();
        }
        if (this.appData != null) {
            return new String(this.appData).hashCode();
        }
        return 1;
    }
}

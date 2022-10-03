package sun.nio.ch;

import java.io.IOException;
import java.util.HashSet;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.nio.channels.MulticastChannel;
import java.nio.channels.MembershipKey;

class MembershipKeyImpl extends MembershipKey
{
    private final MulticastChannel ch;
    private final InetAddress group;
    private final NetworkInterface interf;
    private final InetAddress source;
    private volatile boolean valid;
    private Object stateLock;
    private HashSet<InetAddress> blockedSet;
    
    private MembershipKeyImpl(final MulticastChannel ch, final InetAddress group, final NetworkInterface interf, final InetAddress source) {
        this.valid = true;
        this.stateLock = new Object();
        this.ch = ch;
        this.group = group;
        this.interf = interf;
        this.source = source;
    }
    
    @Override
    public boolean isValid() {
        return this.valid;
    }
    
    void invalidate() {
        this.valid = false;
    }
    
    @Override
    public void drop() {
        ((DatagramChannelImpl)this.ch).drop(this);
    }
    
    @Override
    public MulticastChannel channel() {
        return this.ch;
    }
    
    @Override
    public InetAddress group() {
        return this.group;
    }
    
    @Override
    public NetworkInterface networkInterface() {
        return this.interf;
    }
    
    @Override
    public InetAddress sourceAddress() {
        return this.source;
    }
    
    @Override
    public MembershipKey block(final InetAddress inetAddress) throws IOException {
        if (this.source != null) {
            throw new IllegalStateException("key is source-specific");
        }
        synchronized (this.stateLock) {
            if (this.blockedSet != null && this.blockedSet.contains(inetAddress)) {
                return this;
            }
            ((DatagramChannelImpl)this.ch).block(this, inetAddress);
            if (this.blockedSet == null) {
                this.blockedSet = new HashSet<InetAddress>();
            }
            this.blockedSet.add(inetAddress);
        }
        return this;
    }
    
    @Override
    public MembershipKey unblock(final InetAddress inetAddress) {
        synchronized (this.stateLock) {
            if (this.blockedSet == null || !this.blockedSet.contains(inetAddress)) {
                throw new IllegalStateException("not blocked");
            }
            ((DatagramChannelImpl)this.ch).unblock(this, inetAddress);
            this.blockedSet.remove(inetAddress);
        }
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append('<');
        sb.append(this.group.getHostAddress());
        sb.append(',');
        sb.append(this.interf.getName());
        if (this.source != null) {
            sb.append(',');
            sb.append(this.source.getHostAddress());
        }
        sb.append('>');
        return sb.toString();
    }
    
    static class Type4 extends MembershipKeyImpl
    {
        private final int groupAddress;
        private final int interfAddress;
        private final int sourceAddress;
        
        Type4(final MulticastChannel multicastChannel, final InetAddress inetAddress, final NetworkInterface networkInterface, final InetAddress inetAddress2, final int groupAddress, final int interfAddress, final int sourceAddress) {
            super(multicastChannel, inetAddress, networkInterface, inetAddress2, null);
            this.groupAddress = groupAddress;
            this.interfAddress = interfAddress;
            this.sourceAddress = sourceAddress;
        }
        
        int groupAddress() {
            return this.groupAddress;
        }
        
        int interfaceAddress() {
            return this.interfAddress;
        }
        
        int source() {
            return this.sourceAddress;
        }
    }
    
    static class Type6 extends MembershipKeyImpl
    {
        private final byte[] groupAddress;
        private final int index;
        private final byte[] sourceAddress;
        
        Type6(final MulticastChannel multicastChannel, final InetAddress inetAddress, final NetworkInterface networkInterface, final InetAddress inetAddress2, final byte[] groupAddress, final int index, final byte[] sourceAddress) {
            super(multicastChannel, inetAddress, networkInterface, inetAddress2, null);
            this.groupAddress = groupAddress;
            this.index = index;
            this.sourceAddress = sourceAddress;
        }
        
        byte[] groupAddress() {
            return this.groupAddress;
        }
        
        int index() {
            return this.index;
        }
        
        byte[] source() {
            return this.sourceAddress;
        }
    }
}

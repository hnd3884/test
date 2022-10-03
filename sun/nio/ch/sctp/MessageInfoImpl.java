package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import java.net.SocketAddress;
import com.sun.nio.sctp.MessageInfo;

public class MessageInfoImpl extends MessageInfo
{
    private final SocketAddress address;
    private final int bytes;
    private Association association;
    private int assocId;
    private int streamNumber;
    private boolean complete;
    private boolean unordered;
    private long timeToLive;
    private int ppid;
    
    public MessageInfoImpl(final Association association, final SocketAddress address, final int streamNumber) {
        this.complete = true;
        this.association = association;
        this.address = address;
        this.streamNumber = streamNumber;
        this.bytes = 0;
    }
    
    private MessageInfoImpl(final int assocId, final SocketAddress address, final int bytes, final int streamNumber, final boolean complete, final boolean unordered, final int ppid) {
        this.complete = true;
        this.assocId = assocId;
        this.address = address;
        this.bytes = bytes;
        this.streamNumber = streamNumber;
        this.complete = complete;
        this.unordered = unordered;
        this.ppid = ppid;
    }
    
    @Override
    public Association association() {
        return this.association;
    }
    
    void setAssociation(final Association association) {
        this.association = association;
    }
    
    int associationID() {
        return this.assocId;
    }
    
    @Override
    public SocketAddress address() {
        return this.address;
    }
    
    @Override
    public int bytes() {
        return this.bytes;
    }
    
    @Override
    public int streamNumber() {
        return this.streamNumber;
    }
    
    @Override
    public MessageInfo streamNumber(final int streamNumber) {
        if (streamNumber < 0 || streamNumber > 65536) {
            throw new IllegalArgumentException("Invalid stream number");
        }
        this.streamNumber = streamNumber;
        return this;
    }
    
    @Override
    public int payloadProtocolID() {
        return this.ppid;
    }
    
    @Override
    public MessageInfo payloadProtocolID(final int ppid) {
        this.ppid = ppid;
        return this;
    }
    
    @Override
    public boolean isComplete() {
        return this.complete;
    }
    
    @Override
    public MessageInfo complete(final boolean complete) {
        this.complete = complete;
        return this;
    }
    
    @Override
    public boolean isUnordered() {
        return this.unordered;
    }
    
    @Override
    public MessageInfo unordered(final boolean unordered) {
        this.unordered = unordered;
        return this;
    }
    
    @Override
    public long timeToLive() {
        return this.timeToLive;
    }
    
    @Override
    public MessageInfo timeToLive(final long timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[Address: ").append(this.address).append(", Association: ").append(this.association).append(", Assoc ID: ").append(this.assocId).append(", Bytes: ").append(this.bytes).append(", Stream Number: ").append(this.streamNumber).append(", Complete: ").append(this.complete).append(", isUnordered: ").append(this.unordered).append("]");
        return sb.toString();
    }
}

package sun.nio.ch;

public abstract class AbstractPollArrayWrapper
{
    static final short SIZE_POLLFD = 8;
    static final short FD_OFFSET = 0;
    static final short EVENT_OFFSET = 4;
    static final short REVENT_OFFSET = 6;
    protected AllocatedNativeObject pollArray;
    protected int totalChannels;
    protected long pollArrayAddress;
    
    public AbstractPollArrayWrapper() {
        this.totalChannels = 0;
    }
    
    int getEventOps(final int n) {
        return this.pollArray.getShort(8 * n + 4);
    }
    
    int getReventOps(final int n) {
        return this.pollArray.getShort(8 * n + 6);
    }
    
    int getDescriptor(final int n) {
        return this.pollArray.getInt(8 * n + 0);
    }
    
    void putEventOps(final int n, final int n2) {
        this.pollArray.putShort(8 * n + 4, (short)n2);
    }
    
    void putReventOps(final int n, final int n2) {
        this.pollArray.putShort(8 * n + 6, (short)n2);
    }
    
    void putDescriptor(final int n, final int n2) {
        this.pollArray.putInt(8 * n + 0, n2);
    }
}

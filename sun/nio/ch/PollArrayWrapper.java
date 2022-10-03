package sun.nio.ch;

class PollArrayWrapper
{
    private AllocatedNativeObject pollArray;
    long pollArrayAddress;
    private static final short FD_OFFSET = 0;
    private static final short EVENT_OFFSET = 4;
    static short SIZE_POLLFD;
    private int size;
    
    PollArrayWrapper(final int size) {
        this.pollArray = new AllocatedNativeObject(size * PollArrayWrapper.SIZE_POLLFD, true);
        this.pollArrayAddress = this.pollArray.address();
        this.size = size;
    }
    
    void addEntry(final int n, final SelectionKeyImpl selectionKeyImpl) {
        this.putDescriptor(n, selectionKeyImpl.channel.getFDVal());
    }
    
    void replaceEntry(final PollArrayWrapper pollArrayWrapper, final int n, final PollArrayWrapper pollArrayWrapper2, final int n2) {
        pollArrayWrapper2.putDescriptor(n2, pollArrayWrapper.getDescriptor(n));
        pollArrayWrapper2.putEventOps(n2, pollArrayWrapper.getEventOps(n));
    }
    
    void grow(final int n) {
        final PollArrayWrapper pollArrayWrapper = new PollArrayWrapper(n);
        for (int i = 0; i < this.size; ++i) {
            this.replaceEntry(this, i, pollArrayWrapper, i);
        }
        this.pollArray.free();
        this.pollArray = pollArrayWrapper.pollArray;
        this.size = pollArrayWrapper.size;
        this.pollArrayAddress = this.pollArray.address();
    }
    
    void free() {
        this.pollArray.free();
    }
    
    void putDescriptor(final int n, final int n2) {
        this.pollArray.putInt(PollArrayWrapper.SIZE_POLLFD * n + 0, n2);
    }
    
    void putEventOps(final int n, final int n2) {
        this.pollArray.putShort(PollArrayWrapper.SIZE_POLLFD * n + 4, (short)n2);
    }
    
    int getEventOps(final int n) {
        return this.pollArray.getShort(PollArrayWrapper.SIZE_POLLFD * n + 4);
    }
    
    int getDescriptor(final int n) {
        return this.pollArray.getInt(PollArrayWrapper.SIZE_POLLFD * n + 0);
    }
    
    void addWakeupSocket(final int n, final int n2) {
        this.putDescriptor(n2, n);
        this.putEventOps(n2, Net.POLLIN);
    }
    
    static {
        PollArrayWrapper.SIZE_POLLFD = 8;
    }
}

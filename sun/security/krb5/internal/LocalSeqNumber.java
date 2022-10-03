package sun.security.krb5.internal;

import sun.security.krb5.Confounder;

public class LocalSeqNumber implements SeqNumber
{
    private int lastSeqNumber;
    
    public LocalSeqNumber() {
        this.randInit();
    }
    
    public LocalSeqNumber(final int n) {
        this.init(n);
    }
    
    public LocalSeqNumber(final Integer n) {
        this.init(n);
    }
    
    @Override
    public synchronized void randInit() {
        final byte[] bytes = Confounder.bytes(4);
        bytes[0] &= 0x3F;
        int lastSeqNumber = (bytes[3] & 0xFF) | (bytes[2] & 0xFF) << 8 | (bytes[1] & 0xFF) << 16 | (bytes[0] & 0xFF) << 24;
        if (lastSeqNumber == 0) {
            lastSeqNumber = 1;
        }
        this.lastSeqNumber = lastSeqNumber;
    }
    
    @Override
    public synchronized void init(final int lastSeqNumber) {
        this.lastSeqNumber = lastSeqNumber;
    }
    
    @Override
    public synchronized int current() {
        return this.lastSeqNumber;
    }
    
    @Override
    public synchronized int next() {
        return this.lastSeqNumber + 1;
    }
    
    @Override
    public synchronized int step() {
        return ++this.lastSeqNumber;
    }
}

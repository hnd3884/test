package sun.rmi.transport;

class SequenceEntry
{
    long sequenceNum;
    boolean keep;
    
    SequenceEntry(final long sequenceNum) {
        this.sequenceNum = sequenceNum;
        this.keep = false;
    }
    
    void retain(final long sequenceNum) {
        this.sequenceNum = sequenceNum;
        this.keep = true;
    }
    
    void update(final long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }
}

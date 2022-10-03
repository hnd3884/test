package sun.text.bidi;

public class BidiRun
{
    int start;
    int limit;
    int insertRemove;
    byte level;
    
    BidiRun() {
        this(0, 0, (byte)0);
    }
    
    BidiRun(final int start, final int limit, final byte level) {
        this.start = start;
        this.limit = limit;
        this.level = level;
    }
    
    void copyFrom(final BidiRun bidiRun) {
        this.start = bidiRun.start;
        this.limit = bidiRun.limit;
        this.level = bidiRun.level;
        this.insertRemove = bidiRun.insertRemove;
    }
    
    public byte getEmbeddingLevel() {
        return this.level;
    }
    
    boolean isEvenRun() {
        return (this.level & 0x1) == 0x0;
    }
}

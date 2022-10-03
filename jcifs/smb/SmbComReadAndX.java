package jcifs.smb;

import jcifs.Config;

class SmbComReadAndX extends AndXServerMessageBlock
{
    private static final int BATCH_LIMIT;
    private long offset;
    private int fid;
    private int maxCount;
    private int minCount;
    private int openTimeout;
    private int remaining;
    
    SmbComReadAndX() {
        super(null);
        this.command = 46;
        this.openTimeout = -1;
    }
    
    SmbComReadAndX(final int fid, final long offset, final int maxCount, final ServerMessageBlock andx) {
        super(andx);
        this.fid = fid;
        this.offset = offset;
        this.minCount = maxCount;
        this.maxCount = maxCount;
        this.command = 46;
        this.openTimeout = -1;
    }
    
    void setParam(final int fid, final long offset, final int maxCount) {
        this.fid = fid;
        this.offset = offset;
        this.minCount = maxCount;
        this.maxCount = maxCount;
    }
    
    int getBatchLimit(final byte command) {
        return (command == 4) ? SmbComReadAndX.BATCH_LIMIT : 0;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.offset, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.maxCount, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.minCount, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.openTimeout, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.remaining, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.offset >> 32, dst, dstIndex);
        dstIndex += 4;
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComReadAndX[" + super.toString() + ",fid=" + this.fid + ",offset=" + this.offset + ",maxCount=" + this.maxCount + ",minCount=" + this.minCount + ",openTimeout=" + this.openTimeout + ",remaining=" + this.remaining + ",offset=" + this.offset + "]");
    }
    
    static {
        BATCH_LIMIT = Config.getInt("jcifs.smb.client.ReadAndX.Close", 1);
    }
}

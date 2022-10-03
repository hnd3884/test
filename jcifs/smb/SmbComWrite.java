package jcifs.smb;

class SmbComWrite extends ServerMessageBlock
{
    private int fid;
    private int count;
    private int offset;
    private int remaining;
    private int off;
    private byte[] b;
    
    SmbComWrite() {
        this.command = 11;
    }
    
    SmbComWrite(final int fid, final int offset, final int remaining, final byte[] b, final int off, final int len) {
        this.fid = fid;
        this.count = len;
        this.offset = offset;
        this.remaining = remaining;
        this.b = b;
        this.off = off;
        this.command = 11;
    }
    
    void setParam(final int fid, final long offset, final int remaining, final byte[] b, final int off, final int len) {
        this.fid = fid;
        this.offset = (int)(offset & 0xFFFFFFFFL);
        this.remaining = remaining;
        this.b = b;
        this.off = off;
        this.count = len;
        this.digest = null;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.count, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.offset, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.remaining, dst, dstIndex);
        dstIndex += 2;
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dst[dstIndex++] = 1;
        ServerMessageBlock.writeInt2(this.count, dst, dstIndex);
        dstIndex += 2;
        System.arraycopy(this.b, this.off, dst, dstIndex, this.count);
        dstIndex += this.count;
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComWrite[" + super.toString() + ",fid=" + this.fid + ",count=" + this.count + ",offset=" + this.offset + ",remaining=" + this.remaining + "]");
    }
}

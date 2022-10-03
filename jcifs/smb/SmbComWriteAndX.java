package jcifs.smb;

import jcifs.Config;

class SmbComWriteAndX extends AndXServerMessageBlock
{
    private static final int READ_ANDX_BATCH_LIMIT;
    private static final int CLOSE_BATCH_LIMIT;
    private int fid;
    private int writeMode;
    private int remaining;
    private int dataLength;
    private int dataOffset;
    private int off;
    private byte[] b;
    private long offset;
    
    SmbComWriteAndX() {
        super(null);
        this.command = 47;
    }
    
    SmbComWriteAndX(final int fid, final long offset, final int remaining, final byte[] b, final int off, final int len, final ServerMessageBlock andx) {
        super(andx);
        this.fid = fid;
        this.offset = offset;
        this.remaining = remaining;
        this.b = b;
        this.off = off;
        this.dataLength = len;
        this.command = 47;
    }
    
    void setParam(final int fid, final long offset, final int remaining, final byte[] b, final int off, final int len) {
        this.fid = fid;
        this.offset = offset;
        this.remaining = remaining;
        this.b = b;
        this.off = off;
        this.dataLength = len;
        this.digest = null;
    }
    
    int getBatchLimit(final byte command) {
        if (command == 46) {
            return SmbComWriteAndX.READ_ANDX_BATCH_LIMIT;
        }
        if (command == 4) {
            return SmbComWriteAndX.CLOSE_BATCH_LIMIT;
        }
        return 0;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        this.dataOffset = dstIndex - this.headerStart + 26;
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.offset, dst, dstIndex);
        dstIndex += 4;
        for (int i = 0; i < 4; ++i) {
            dst[dstIndex++] = 0;
        }
        ServerMessageBlock.writeInt2(this.writeMode, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.remaining, dst, dstIndex);
        dstIndex += 2;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt2(this.dataLength, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.dataOffset, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.offset >> 32, dst, dstIndex);
        dstIndex += 4;
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        System.arraycopy(this.b, this.off, dst, dstIndex, this.dataLength);
        dstIndex += this.dataLength;
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComWriteAndX[" + super.toString() + ",fid=" + this.fid + ",offset=" + this.offset + ",writeMode=" + this.writeMode + ",remaining=" + this.remaining + ",dataLength=" + this.dataLength + ",dataOffset=" + this.dataOffset + "]");
    }
    
    static {
        READ_ANDX_BATCH_LIMIT = Config.getInt("jcifs.smb.client.WriteAndX.ReadAndX", 1);
        CLOSE_BATCH_LIMIT = Config.getInt("jcifs.smb.client.WriteAndX.Close", 1);
    }
}

package jcifs.smb;

class SmbComClose extends ServerMessageBlock
{
    private int fid;
    private long lastWriteTime;
    
    SmbComClose(final int fid, final long lastWriteTime) {
        this.fid = fid;
        this.lastWriteTime = lastWriteTime;
        this.command = 4;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeUTime(this.lastWriteTime, dst, dstIndex);
        return 6;
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
        return new String("SmbComClose[" + super.toString() + ",fid=" + this.fid + ",lastWriteTime=" + this.lastWriteTime + "]");
    }
}

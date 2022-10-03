package jcifs.smb;

class Trans2SetFileInformation extends SmbComTransaction
{
    static final int SMB_FILE_BASIC_INFO = 257;
    private int fid;
    private int attributes;
    private long createTime;
    private long lastWriteTime;
    
    Trans2SetFileInformation(final int fid, final int attributes, final long createTime, final long lastWriteTime) {
        this.fid = fid;
        this.attributes = attributes;
        this.createTime = createTime;
        this.lastWriteTime = lastWriteTime;
        this.command = 50;
        this.subCommand = 8;
        this.maxParameterCount = 6;
        this.maxDataCount = 0;
        this.maxSetupCount = 0;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        return 2;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(257L, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(0L, dst, dstIndex);
        dstIndex += 2;
        return dstIndex - start;
    }
    
    int writeDataWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeTime(this.createTime, dst, dstIndex);
        dstIndex += 8;
        ServerMessageBlock.writeInt8(0L, dst, dstIndex);
        dstIndex += 8;
        ServerMessageBlock.writeTime(this.lastWriteTime, dst, dstIndex);
        dstIndex += 8;
        ServerMessageBlock.writeInt8(0L, dst, dstIndex);
        dstIndex += 8;
        ServerMessageBlock.writeInt2(0x80 | this.attributes, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt8(0L, dst, dstIndex);
        dstIndex += 6;
        return dstIndex - start;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("Trans2SetFileInformation[" + super.toString() + ",fid=" + this.fid + "]");
    }
}

package jcifs.smb;

class SmbComOpenAndXResponse extends AndXServerMessageBlock
{
    int fid;
    int fileAttributes;
    int dataSize;
    int grantedAccess;
    int fileType;
    int deviceState;
    int action;
    int serverFid;
    long lastWriteTime;
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        this.fid = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.fileAttributes = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.lastWriteTime = ServerMessageBlock.readUTime(buffer, bufferIndex);
        bufferIndex += 4;
        this.dataSize = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.grantedAccess = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.fileType = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.deviceState = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.action = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.serverFid = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 6;
        return bufferIndex - start;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComOpenAndXResponse[" + super.toString() + ",fid=" + this.fid + ",fileAttributes=" + this.fileAttributes + ",lastWriteTime=" + this.lastWriteTime + ",dataSize=" + this.dataSize + ",grantedAccess=" + this.grantedAccess + ",fileType=" + this.fileType + ",deviceState=" + this.deviceState + ",action=" + this.action + ",serverFid=" + this.serverFid + "]");
    }
}

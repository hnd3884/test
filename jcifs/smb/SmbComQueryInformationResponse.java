package jcifs.smb;

import java.util.Date;
import jcifs.util.Hexdump;

class SmbComQueryInformationResponse extends ServerMessageBlock implements Info
{
    private int fileAttributes;
    private long lastWriteTime;
    private long serverTimeZoneOffset;
    private int fileSize;
    
    SmbComQueryInformationResponse(final long serverTimeZoneOffset) {
        this.fileAttributes = 0;
        this.lastWriteTime = 0L;
        this.fileSize = 0;
        this.serverTimeZoneOffset = serverTimeZoneOffset;
        this.command = 8;
    }
    
    public int getAttributes() {
        return this.fileAttributes;
    }
    
    public long getCreateTime() {
        return this.lastWriteTime + this.serverTimeZoneOffset;
    }
    
    public long getLastWriteTime() {
        return this.lastWriteTime + this.serverTimeZoneOffset;
    }
    
    public long getSize() {
        return this.fileSize;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, int bufferIndex) {
        if (this.wordCount == 0) {
            return 0;
        }
        this.fileAttributes = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.lastWriteTime = ServerMessageBlock.readUTime(buffer, bufferIndex);
        bufferIndex += 4;
        this.fileSize = ServerMessageBlock.readInt4(buffer, bufferIndex);
        return 20;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComQueryInformationResponse[" + super.toString() + ",fileAttributes=0x" + Hexdump.toHexString(this.fileAttributes, 4) + ",lastWriteTime=" + new Date(this.lastWriteTime) + ",fileSize=" + this.fileSize + "]");
    }
}

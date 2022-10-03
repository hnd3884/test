package jcifs.smb;

import jcifs.util.Hexdump;
import java.util.Date;

class Trans2QueryPathInformationResponse extends SmbComTransactionResponse
{
    static final int SMB_QUERY_FILE_BASIC_INFO = 257;
    static final int SMB_QUERY_FILE_STANDARD_INFO = 258;
    private int informationLevel;
    Info info;
    
    Trans2QueryPathInformationResponse(final int informationLevel) {
        this.informationLevel = informationLevel;
        this.subCommand = 5;
    }
    
    int writeSetupWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 2;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        switch (this.informationLevel) {
            case 257: {
                return this.readSmbQueryFileBasicInfoWireFormat(buffer, bufferIndex);
            }
            case 258: {
                return this.readSmbQueryFileStandardInfoWireFormat(buffer, bufferIndex);
            }
            default: {
                return 0;
            }
        }
    }
    
    int readSmbQueryFileStandardInfoWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final SmbQueryFileStandardInfo info = new SmbQueryFileStandardInfo();
        info.allocationSize = ServerMessageBlock.readInt8(buffer, bufferIndex);
        bufferIndex += 8;
        info.endOfFile = ServerMessageBlock.readInt8(buffer, bufferIndex);
        bufferIndex += 8;
        info.numberOfLinks = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        info.deletePending = ((buffer[bufferIndex++] & 0xFF) > 0);
        info.directory = ((buffer[bufferIndex++] & 0xFF) > 0);
        this.info = info;
        return bufferIndex - start;
    }
    
    int readSmbQueryFileBasicInfoWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final SmbQueryFileBasicInfo info = new SmbQueryFileBasicInfo();
        info.createTime = ServerMessageBlock.readTime(buffer, bufferIndex);
        bufferIndex += 8;
        info.lastAccessTime = ServerMessageBlock.readTime(buffer, bufferIndex);
        bufferIndex += 8;
        info.lastWriteTime = ServerMessageBlock.readTime(buffer, bufferIndex);
        bufferIndex += 8;
        info.changeTime = ServerMessageBlock.readTime(buffer, bufferIndex);
        bufferIndex += 8;
        info.attributes = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.info = info;
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("Trans2QueryPathInformationResponse[" + super.toString() + "]");
    }
    
    class SmbQueryFileBasicInfo implements Info
    {
        long createTime;
        long lastAccessTime;
        long lastWriteTime;
        long changeTime;
        int attributes;
        
        public int getAttributes() {
            return this.attributes;
        }
        
        public long getCreateTime() {
            return this.createTime;
        }
        
        public long getLastWriteTime() {
            return this.lastWriteTime;
        }
        
        public long getSize() {
            return 0L;
        }
        
        public String toString() {
            return new String("SmbQueryFileBasicInfo[createTime=" + new Date(this.createTime) + ",lastAccessTime=" + new Date(this.lastAccessTime) + ",lastWriteTime=" + new Date(this.lastWriteTime) + ",changeTime=" + new Date(this.changeTime) + ",attributes=0x" + Hexdump.toHexString(this.attributes, 4) + "]");
        }
    }
    
    class SmbQueryFileStandardInfo implements Info
    {
        long allocationSize;
        long endOfFile;
        int numberOfLinks;
        boolean deletePending;
        boolean directory;
        
        public int getAttributes() {
            return 0;
        }
        
        public long getCreateTime() {
            return 0L;
        }
        
        public long getLastWriteTime() {
            return 0L;
        }
        
        public long getSize() {
            return this.endOfFile;
        }
        
        public String toString() {
            return new String("SmbQueryInfoStandard[allocationSize=" + this.allocationSize + ",endOfFile=" + this.endOfFile + ",numberOfLinks=" + this.numberOfLinks + ",deletePending=" + this.deletePending + ",directory=" + this.directory + "]");
        }
    }
}

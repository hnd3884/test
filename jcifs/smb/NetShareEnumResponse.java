package jcifs.smb;

import jcifs.util.Hexdump;
import jcifs.util.LogStream;

class NetShareEnumResponse extends SmbComTransactionResponse
{
    private int converter;
    private int totalAvailableEntries;
    
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
    
    int readParametersWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        this.status = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.converter = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.numEntries = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.totalAvailableEntries = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        return bufferIndex - start;
    }
    
    int readDataWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        this.useUnicode = false;
        this.results = new ShareInfo1[this.numEntries];
        for (int i = 0; i < this.numEntries; ++i) {
            final ShareInfo1 e = (ShareInfo1)(this.results[i] = new ShareInfo1());
            e.netName = this.readString(buffer, bufferIndex, 13, false);
            bufferIndex += 14;
            e.type = ServerMessageBlock.readInt2(buffer, bufferIndex);
            bufferIndex += 2;
            int off = ServerMessageBlock.readInt4(buffer, bufferIndex);
            bufferIndex += 4;
            off = (off & 0xFFFF) - this.converter;
            off += start;
            e.remark = this.readString(buffer, off, 128, false);
            final LogStream log = NetShareEnumResponse.log;
            if (LogStream.level >= 4) {
                NetShareEnumResponse.log.println(e);
            }
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("NetShareEnumResponse[" + super.toString() + ",status=" + this.status + ",converter=" + this.converter + ",entriesReturned=" + this.numEntries + ",totalAvailableEntries=" + this.totalAvailableEntries + "]");
    }
    
    class ShareInfo1 implements FileEntry
    {
        String netName;
        int type;
        String remark;
        
        public String getName() {
            return this.netName;
        }
        
        public int getType() {
            switch (this.type) {
                case 1: {
                    return 32;
                }
                case 3: {
                    return 16;
                }
                default: {
                    return 8;
                }
            }
        }
        
        public int getAttributes() {
            return 17;
        }
        
        public long createTime() {
            return 0L;
        }
        
        public long lastModified() {
            return 0L;
        }
        
        public long length() {
            return 0L;
        }
        
        public String toString() {
            return new String("ShareInfo1[netName=" + this.netName + ",type=0x" + Hexdump.toHexString(this.type, 4) + ",remark=" + this.remark + "]");
        }
    }
}

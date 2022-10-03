package jcifs.smb;

import jcifs.util.Hexdump;
import jcifs.util.LogStream;

class NetServerEnum2Response extends SmbComTransactionResponse
{
    private int converter;
    private int totalAvailableEntries;
    String lastName;
    
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
        ServerInfo1 e = null;
        this.results = new ServerInfo1[this.numEntries];
        for (int i = 0; i < this.numEntries; ++i) {
            e = (ServerInfo1)(this.results[i] = new ServerInfo1());
            e.name = this.readString(buffer, bufferIndex, 16, false);
            bufferIndex += 16;
            e.versionMajor = (buffer[bufferIndex++] & 0xFF);
            e.versionMinor = (buffer[bufferIndex++] & 0xFF);
            e.type = ServerMessageBlock.readInt4(buffer, bufferIndex);
            bufferIndex += 4;
            int off = ServerMessageBlock.readInt4(buffer, bufferIndex);
            bufferIndex += 4;
            off = (off & 0xFFFF) - this.converter;
            off += start;
            e.commentOrMasterBrowser = this.readString(buffer, off, 48, false);
            final LogStream log = NetServerEnum2Response.log;
            if (LogStream.level >= 4) {
                NetServerEnum2Response.log.println(e);
            }
        }
        this.lastName = ((this.numEntries == 0) ? null : e.name);
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("NetServerEnum2Response[" + super.toString() + ",status=" + this.status + ",converter=" + this.converter + ",entriesReturned=" + this.numEntries + ",totalAvailableEntries=" + this.totalAvailableEntries + ",lastName=" + this.lastName + "]");
    }
    
    class ServerInfo1 implements FileEntry
    {
        String name;
        int versionMajor;
        int versionMinor;
        int type;
        String commentOrMasterBrowser;
        
        public String getName() {
            return this.name;
        }
        
        public int getType() {
            return ((this.type & Integer.MIN_VALUE) != 0x0) ? 2 : 4;
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
            return new String("ServerInfo1[name=" + this.name + ",versionMajor=" + this.versionMajor + ",versionMinor=" + this.versionMinor + ",type=0x" + Hexdump.toHexString(this.type, 8) + ",commentOrMasterBrowser=" + this.commentOrMasterBrowser + "]");
        }
    }
}

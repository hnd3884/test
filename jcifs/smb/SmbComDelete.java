package jcifs.smb;

import jcifs.util.Hexdump;

class SmbComDelete extends ServerMessageBlock
{
    private int searchAttributes;
    
    SmbComDelete(final String fileName) {
        this.path = fileName;
        this.command = 6;
        this.searchAttributes = 6;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        ServerMessageBlock.writeInt2(this.searchAttributes, dst, dstIndex);
        return 2;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dst[dstIndex++] = 4;
        dstIndex += this.writeString(this.path, dst, dstIndex);
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComDelete[" + super.toString() + ",searchAttributes=0x" + Hexdump.toHexString(this.searchAttributes, 4) + ",fileName=" + this.path + "]");
    }
}

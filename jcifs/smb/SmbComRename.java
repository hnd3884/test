package jcifs.smb;

import jcifs.util.Hexdump;

class SmbComRename extends ServerMessageBlock
{
    private int searchAttributes;
    private String oldFileName;
    private String newFileName;
    
    SmbComRename(final String oldFileName, final String newFileName) {
        this.command = 7;
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
        this.searchAttributes = 22;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        ServerMessageBlock.writeInt2(this.searchAttributes, dst, dstIndex);
        return 2;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dst[dstIndex++] = 4;
        dstIndex += this.writeString(this.oldFileName, dst, dstIndex);
        dst[dstIndex++] = 4;
        if (this.useUnicode) {
            dst[dstIndex++] = 0;
        }
        dstIndex += this.writeString(this.newFileName, dst, dstIndex);
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComRename[" + super.toString() + ",searchAttributes=0x" + Hexdump.toHexString(this.searchAttributes, 4) + ",oldFileName=" + this.oldFileName + ",newFileName=" + this.newFileName + "]");
    }
}

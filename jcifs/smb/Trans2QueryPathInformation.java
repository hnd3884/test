package jcifs.smb;

import jcifs.util.Hexdump;

class Trans2QueryPathInformation extends SmbComTransaction
{
    private int informationLevel;
    
    Trans2QueryPathInformation(final String filename, final int informationLevel) {
        this.path = filename;
        this.informationLevel = informationLevel;
        this.command = 50;
        this.subCommand = 5;
        this.totalDataCount = 0;
        this.maxParameterCount = 2;
        this.maxDataCount = 40;
        this.maxSetupCount = 0;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        return 2;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.informationLevel, dst, dstIndex);
        dstIndex += 2;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dstIndex += this.writeString(this.path, dst, dstIndex);
        return dstIndex - start;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
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
        return new String("Trans2QueryPathInformation[" + super.toString() + ",informationLevel=0x" + Hexdump.toHexString(this.informationLevel, 3) + ",filename=" + this.path + "]");
    }
}

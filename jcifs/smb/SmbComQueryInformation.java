package jcifs.smb;

class SmbComQueryInformation extends ServerMessageBlock
{
    SmbComQueryInformation(final String filename) {
        this.path = filename;
        this.command = 8;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
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
        return new String("SmbComQueryInformation[" + super.toString() + ",filename=" + this.path + "]");
    }
}

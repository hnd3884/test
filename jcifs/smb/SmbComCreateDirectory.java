package jcifs.smb;

class SmbComCreateDirectory extends ServerMessageBlock
{
    SmbComCreateDirectory(final String directoryName) {
        this.path = directoryName;
        this.command = 0;
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
        return new String("SmbComCreateDirectory[" + super.toString() + ",directoryName=" + this.path + "]");
    }
}

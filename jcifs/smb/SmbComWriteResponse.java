package jcifs.smb;

class SmbComWriteResponse extends ServerMessageBlock
{
    long count;
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        this.count = ((long)ServerMessageBlock.readInt2(buffer, bufferIndex) & 0xFFFFL);
        return 8;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComWriteResponse[" + super.toString() + ",count=" + this.count + "]");
    }
}

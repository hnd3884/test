package jcifs.smb;

class SmbComLogoffAndX extends AndXServerMessageBlock
{
    SmbComLogoffAndX(final ServerMessageBlock andx) {
        super(andx);
        this.command = 116;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComLogoffAndX[" + super.toString() + "]");
    }
}

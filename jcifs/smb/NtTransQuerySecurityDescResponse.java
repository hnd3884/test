package jcifs.smb;

class NtTransQuerySecurityDescResponse extends SmbComNtTransactionResponse
{
    SecurityDescriptor securityDescriptor;
    
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
        this.length = ServerMessageBlock.readInt4(buffer, bufferIndex);
        return 4;
    }
    
    int readDataWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        if (this.errorCode != 0) {
            return 4;
        }
        this.securityDescriptor = new SecurityDescriptor();
        bufferIndex += this.securityDescriptor.decode(buffer, bufferIndex, len);
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("NtTransQuerySecurityResponse[" + super.toString() + "]");
    }
}

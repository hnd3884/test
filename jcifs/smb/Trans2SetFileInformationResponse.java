package jcifs.smb;

class Trans2SetFileInformationResponse extends SmbComTransactionResponse
{
    Trans2SetFileInformationResponse() {
        this.subCommand = 8;
    }
    
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
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("Trans2SetFileInformationResponse[" + super.toString() + "]");
    }
}

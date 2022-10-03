package jcifs.smb;

class Trans2GetDfsReferral extends SmbComTransaction
{
    private int maxReferralLevel;
    
    Trans2GetDfsReferral(final String filename) {
        this.maxReferralLevel = 3;
        this.path = filename;
        this.command = 50;
        this.subCommand = 16;
        this.totalDataCount = 0;
        this.maxParameterCount = 0;
        this.maxDataCount = 4096;
        this.maxSetupCount = 0;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        return 2;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.maxReferralLevel, dst, dstIndex);
        dstIndex += 2;
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
        return new String("Trans2GetDfsReferral[" + super.toString() + ",maxReferralLevel=0x" + this.maxReferralLevel + ",filename=" + this.path + "]");
    }
}

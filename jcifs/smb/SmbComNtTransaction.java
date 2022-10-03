package jcifs.smb;

abstract class SmbComNtTransaction extends SmbComTransaction
{
    private static final int NTT_PRIMARY_SETUP_OFFSET = 69;
    private static final int NTT_SECONDARY_PARAMETER_OFFSET = 51;
    static final int NT_TRANSACT_QUERY_SECURITY_DESC = 6;
    int function;
    
    SmbComNtTransaction() {
        this.primarySetupOffset = 69;
        this.secondaryParameterOffset = 51;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        if (this.command != -95) {
            dst[dstIndex++] = this.maxSetupCount;
        }
        else {
            dst[dstIndex++] = 0;
        }
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt4(this.totalParameterCount, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.totalDataCount, dst, dstIndex);
        dstIndex += 4;
        if (this.command != -95) {
            ServerMessageBlock.writeInt4(this.maxParameterCount, dst, dstIndex);
            dstIndex += 4;
            ServerMessageBlock.writeInt4(this.maxDataCount, dst, dstIndex);
            dstIndex += 4;
        }
        ServerMessageBlock.writeInt4(this.parameterCount, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4((this.parameterCount == 0) ? 0 : this.parameterOffset, dst, dstIndex);
        dstIndex += 4;
        if (this.command == -95) {
            ServerMessageBlock.writeInt4(this.parameterDisplacement, dst, dstIndex);
            dstIndex += 4;
        }
        ServerMessageBlock.writeInt4(this.dataCount, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4((this.dataCount == 0) ? 0 : this.dataOffset, dst, dstIndex);
        dstIndex += 4;
        if (this.command == -95) {
            ServerMessageBlock.writeInt4(this.dataDisplacement, dst, dstIndex);
            dstIndex += 4;
            dst[dstIndex++] = 0;
        }
        else {
            dst[dstIndex++] = (byte)this.setupCount;
            ServerMessageBlock.writeInt2(this.function, dst, dstIndex);
            dstIndex += 2;
            dstIndex += this.writeSetupWireFormat(dst, dstIndex);
        }
        return dstIndex - start;
    }
}

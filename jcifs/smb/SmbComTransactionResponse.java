package jcifs.smb;

import jcifs.util.LogStream;
import java.util.Enumeration;

abstract class SmbComTransactionResponse extends ServerMessageBlock implements Enumeration
{
    private static final int SETUP_OFFSET = 61;
    private static final int DISCONNECT_TID = 1;
    private static final int ONE_WAY_TRANSACTION = 2;
    private int pad;
    private int pad1;
    private boolean parametersDone;
    private boolean dataDone;
    protected int totalParameterCount;
    protected int totalDataCount;
    protected int parameterCount;
    protected int parameterOffset;
    protected int parameterDisplacement;
    protected int dataOffset;
    protected int dataDisplacement;
    protected int setupCount;
    protected int bufParameterStart;
    protected int bufDataStart;
    int dataCount;
    byte subCommand;
    boolean hasMore;
    boolean isPrimary;
    byte[] txn_buf;
    int status;
    int numEntries;
    FileEntry[] results;
    
    SmbComTransactionResponse() {
        this.hasMore = true;
        this.isPrimary = true;
        this.txn_buf = null;
    }
    
    void reset() {
        super.reset();
        this.bufDataStart = 0;
        final boolean b = true;
        this.hasMore = b;
        this.isPrimary = b;
        final boolean b2 = false;
        this.dataDone = b2;
        this.parametersDone = b2;
    }
    
    public boolean hasMoreElements() {
        return this.errorCode == 0 && this.hasMore;
    }
    
    public Object nextElement() {
        if (this.isPrimary) {
            this.isPrimary = false;
        }
        return this;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        this.totalParameterCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
        if (this.bufDataStart == 0) {
            this.bufDataStart = this.totalParameterCount;
        }
        bufferIndex += 2;
        this.totalDataCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 4;
        this.parameterCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.parameterOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.parameterDisplacement = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.dataCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.dataOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.dataDisplacement = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.setupCount = (buffer[bufferIndex] & 0xFF);
        bufferIndex += 2;
        if (this.setupCount != 0) {
            final LogStream log = SmbComTransactionResponse.log;
            if (LogStream.level > 2) {
                SmbComTransactionResponse.log.println("setupCount is not zero: " + this.setupCount);
            }
        }
        return bufferIndex - start;
    }
    
    int readBytesWireFormat(final byte[] buffer, int bufferIndex) {
        final int n = 0;
        this.pad1 = n;
        this.pad = n;
        if (this.parameterCount > 0) {
            final int n2 = bufferIndex;
            final int pad = this.parameterOffset - (bufferIndex - this.headerStart);
            this.pad = pad;
            bufferIndex = n2 + pad;
            System.arraycopy(buffer, bufferIndex, this.txn_buf, this.bufParameterStart + this.parameterDisplacement, this.parameterCount);
            bufferIndex += this.parameterCount;
        }
        if (this.dataCount > 0) {
            final int n3 = bufferIndex;
            final int pad2 = this.dataOffset - (bufferIndex - this.headerStart);
            this.pad1 = pad2;
            bufferIndex = n3 + pad2;
            System.arraycopy(buffer, bufferIndex, this.txn_buf, this.bufDataStart + this.dataDisplacement, this.dataCount);
            bufferIndex += this.dataCount;
        }
        if (!this.parametersDone && this.parameterDisplacement + this.parameterCount == this.totalParameterCount) {
            this.parametersDone = true;
        }
        if (!this.dataDone && this.dataDisplacement + this.dataCount == this.totalDataCount) {
            this.dataDone = true;
        }
        if (this.parametersDone && this.dataDone) {
            this.hasMore = false;
            this.readParametersWireFormat(this.txn_buf, this.bufParameterStart, this.totalParameterCount);
            this.readDataWireFormat(this.txn_buf, this.bufDataStart, this.totalDataCount);
        }
        return this.pad + this.parameterCount + this.pad1 + this.dataCount;
    }
    
    abstract int writeSetupWireFormat(final byte[] p0, final int p1);
    
    abstract int writeParametersWireFormat(final byte[] p0, final int p1);
    
    abstract int writeDataWireFormat(final byte[] p0, final int p1);
    
    abstract int readSetupWireFormat(final byte[] p0, final int p1, final int p2);
    
    abstract int readParametersWireFormat(final byte[] p0, final int p1, final int p2);
    
    abstract int readDataWireFormat(final byte[] p0, final int p1, final int p2);
    
    public String toString() {
        return new String(super.toString() + ",totalParameterCount=" + this.totalParameterCount + ",totalDataCount=" + this.totalDataCount + ",parameterCount=" + this.parameterCount + ",parameterOffset=" + this.parameterOffset + ",parameterDisplacement=" + this.parameterDisplacement + ",dataCount=" + this.dataCount + ",dataOffset=" + this.dataOffset + ",dataDisplacement=" + this.dataDisplacement + ",setupCount=" + this.setupCount + ",pad=" + this.pad + ",pad1=" + this.pad1);
    }
}

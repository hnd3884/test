package jcifs.smb;

import jcifs.Config;
import jcifs.util.Hexdump;
import java.util.Enumeration;

abstract class SmbComTransaction extends ServerMessageBlock implements Enumeration
{
    private static final int DEFAULT_MAX_DATA_COUNT;
    private static final int PRIMARY_SETUP_OFFSET = 61;
    private static final int SECONDARY_PARAMETER_OFFSET = 51;
    private static final int DISCONNECT_TID = 1;
    private static final int ONE_WAY_TRANSACTION = 2;
    private static final int PADDING_SIZE = 2;
    private int flags;
    private int fid;
    private int pad;
    private int pad1;
    private boolean hasMore;
    private boolean isPrimary;
    private int bufParameterOffset;
    private int bufDataOffset;
    static final int TRANSACTION_BUF_SIZE = 65535;
    static final byte TRANS2_FIND_FIRST2 = 1;
    static final byte TRANS2_FIND_NEXT2 = 2;
    static final byte TRANS2_QUERY_FS_INFORMATION = 3;
    static final byte TRANS2_QUERY_PATH_INFORMATION = 5;
    static final byte TRANS2_GET_DFS_REFERRAL = 16;
    static final byte TRANS2_SET_FILE_INFORMATION = 8;
    static final int NET_SHARE_ENUM = 0;
    static final int NET_SERVER_ENUM2 = 104;
    static final int NET_SERVER_ENUM3 = 215;
    static final byte TRANS_PEEK_NAMED_PIPE = 35;
    static final byte TRANS_WAIT_NAMED_PIPE = 83;
    static final byte TRANS_CALL_NAMED_PIPE = 84;
    static final byte TRANS_TRANSACT_NAMED_PIPE = 38;
    protected int primarySetupOffset;
    protected int secondaryParameterOffset;
    protected int parameterCount;
    protected int parameterOffset;
    protected int parameterDisplacement;
    protected int dataCount;
    protected int dataOffset;
    protected int dataDisplacement;
    int totalParameterCount;
    int totalDataCount;
    int maxParameterCount;
    int maxDataCount;
    byte maxSetupCount;
    int timeout;
    int setupCount;
    byte subCommand;
    String name;
    int maxBufferSize;
    byte[] txn_buf;
    
    SmbComTransaction() {
        this.flags = 0;
        this.pad = 0;
        this.pad1 = 0;
        this.hasMore = true;
        this.isPrimary = true;
        this.maxDataCount = SmbComTransaction.DEFAULT_MAX_DATA_COUNT;
        this.timeout = 0;
        this.setupCount = 1;
        this.name = "";
        this.maxParameterCount = 1024;
        this.primarySetupOffset = 61;
        this.secondaryParameterOffset = 51;
    }
    
    void reset() {
        super.reset();
        final boolean b = true;
        this.hasMore = b;
        this.isPrimary = b;
    }
    
    void reset(final int key, final String lastName) {
        this.reset();
    }
    
    public boolean hasMoreElements() {
        return this.hasMore;
    }
    
    public Object nextElement() {
        if (this.isPrimary) {
            this.isPrimary = false;
            this.parameterOffset = this.primarySetupOffset + this.setupCount * 2 + 2;
            if (this.command != -96) {
                if (this.command == 37 && !this.isResponse()) {
                    this.parameterOffset += this.stringWireLength(this.name, this.parameterOffset);
                }
            }
            else if (this.command == -96) {
                this.parameterOffset += 2;
            }
            this.pad = this.parameterOffset % 2;
            this.pad = ((this.pad == 0) ? 0 : (2 - this.pad));
            this.parameterOffset += this.pad;
            this.totalParameterCount = this.writeParametersWireFormat(this.txn_buf, this.bufParameterOffset);
            this.bufDataOffset = this.totalParameterCount;
            int available = this.maxBufferSize - this.parameterOffset;
            this.parameterCount = Math.min(this.totalParameterCount, available);
            available -= this.parameterCount;
            this.dataOffset = this.parameterOffset + this.parameterCount;
            this.pad1 = this.dataOffset % 2;
            this.pad1 = ((this.pad1 == 0) ? 0 : (2 - this.pad1));
            this.dataOffset += this.pad1;
            this.totalDataCount = this.writeDataWireFormat(this.txn_buf, this.bufDataOffset);
            this.dataCount = Math.min(this.totalDataCount, available);
        }
        else {
            if (this.command != -96) {
                this.command = 38;
            }
            else {
                this.command = -95;
            }
            this.parameterOffset = 51;
            if (this.totalParameterCount - this.parameterDisplacement > 0) {
                this.pad = this.parameterOffset % 2;
                this.pad = ((this.pad == 0) ? 0 : (2 - this.pad));
                this.parameterOffset += this.pad;
            }
            this.parameterDisplacement += this.parameterCount;
            int available = this.maxBufferSize - this.parameterOffset - this.pad;
            this.parameterCount = Math.min(this.totalParameterCount - this.parameterDisplacement, available);
            available -= this.parameterCount;
            this.dataOffset = this.parameterOffset + this.parameterCount;
            this.pad1 = this.dataOffset % 2;
            this.pad1 = ((this.pad1 == 0) ? 0 : (2 - this.pad1));
            this.dataOffset += this.pad1;
            this.dataDisplacement += this.dataCount;
            available -= this.pad1;
            this.dataCount = Math.min(this.totalDataCount - this.dataDisplacement, available);
        }
        if (this.parameterDisplacement + this.parameterCount >= this.totalParameterCount && this.dataDisplacement + this.dataCount >= this.totalDataCount) {
            this.hasMore = false;
        }
        return this;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.totalParameterCount, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.totalDataCount, dst, dstIndex);
        dstIndex += 2;
        if (this.command != 38) {
            ServerMessageBlock.writeInt2(this.maxParameterCount, dst, dstIndex);
            dstIndex += 2;
            ServerMessageBlock.writeInt2(this.maxDataCount, dst, dstIndex);
            dstIndex += 2;
            dst[dstIndex++] = this.maxSetupCount;
            dst[dstIndex++] = 0;
            ServerMessageBlock.writeInt2(this.flags, dst, dstIndex);
            dstIndex += 2;
            ServerMessageBlock.writeInt4(this.timeout, dst, dstIndex);
            dstIndex += 4;
            dst[dstIndex++] = 0;
            dst[dstIndex++] = 0;
        }
        ServerMessageBlock.writeInt2(this.parameterCount, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2((this.parameterCount == 0) ? 0 : this.parameterOffset, dst, dstIndex);
        dstIndex += 2;
        if (this.command == 38) {
            ServerMessageBlock.writeInt2(this.parameterDisplacement, dst, dstIndex);
            dstIndex += 2;
        }
        ServerMessageBlock.writeInt2(this.dataCount, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2((this.dataCount == 0) ? 0 : this.dataOffset, dst, dstIndex);
        dstIndex += 2;
        if (this.command == 38) {
            ServerMessageBlock.writeInt2(this.dataDisplacement, dst, dstIndex);
            dstIndex += 2;
        }
        else {
            dst[dstIndex++] = (byte)this.setupCount;
            dst[dstIndex++] = 0;
            dstIndex += this.writeSetupWireFormat(dst, dstIndex);
        }
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        int p = this.pad;
        if (this.command == 37 && !this.isResponse()) {
            dstIndex += this.writeString(this.name, dst, dstIndex);
        }
        if (this.parameterCount > 0) {
            while (p-- > 0) {
                dst[dstIndex++] = 0;
            }
            System.arraycopy(this.txn_buf, this.bufParameterOffset, dst, dstIndex, this.parameterCount);
            dstIndex += this.parameterCount;
        }
        if (this.dataCount > 0) {
            p = this.pad1;
            while (p-- > 0) {
                dst[dstIndex++] = 0;
            }
            System.arraycopy(this.txn_buf, this.bufDataOffset, dst, dstIndex, this.dataCount);
            this.bufDataOffset += this.dataCount;
            dstIndex += this.dataCount;
        }
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    abstract int writeSetupWireFormat(final byte[] p0, final int p1);
    
    abstract int writeParametersWireFormat(final byte[] p0, final int p1);
    
    abstract int writeDataWireFormat(final byte[] p0, final int p1);
    
    abstract int readSetupWireFormat(final byte[] p0, final int p1, final int p2);
    
    abstract int readParametersWireFormat(final byte[] p0, final int p1, final int p2);
    
    abstract int readDataWireFormat(final byte[] p0, final int p1, final int p2);
    
    public String toString() {
        return new String(super.toString() + ",totalParameterCount=" + this.totalParameterCount + ",totalDataCount=" + this.totalDataCount + ",maxParameterCount=" + this.maxParameterCount + ",maxDataCount=" + this.maxDataCount + ",maxSetupCount=" + this.maxSetupCount + ",flags=0x" + Hexdump.toHexString(this.flags, 2) + ",timeout=" + this.timeout + ",parameterCount=" + this.parameterCount + ",parameterOffset=" + this.parameterOffset + ",parameterDisplacement=" + this.parameterDisplacement + ",dataCount=" + this.dataCount + ",dataOffset=" + this.dataOffset + ",dataDisplacement=" + this.dataDisplacement + ",setupCount=" + this.setupCount + ",pad=" + this.pad + ",pad1=" + this.pad1);
    }
    
    static {
        DEFAULT_MAX_DATA_COUNT = Config.getInt("jcifs.smb.client.transaction_buf_size", 65535) - 512;
    }
}

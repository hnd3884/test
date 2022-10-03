package jcifs.smb;

import jcifs.Config;
import java.util.Date;
import jcifs.util.Hexdump;

class SmbComOpenAndX extends AndXServerMessageBlock
{
    private static final int FLAGS_RETURN_ADDITIONAL_INFO = 1;
    private static final int FLAGS_REQUEST_OPLOCK = 2;
    private static final int FLAGS_REQUEST_BATCH_OPLOCK = 4;
    private static final int SHARING_COMPATIBILITY = 0;
    private static final int SHARING_DENY_READ_WRITE_EXECUTE = 16;
    private static final int SHARING_DENY_WRITE = 32;
    private static final int SHARING_DENY_READ_EXECUTE = 48;
    private static final int SHARING_DENY_NONE = 64;
    private static final int DO_NOT_CACHE = 4096;
    private static final int WRITE_THROUGH = 16384;
    private static final int OPEN_FN_CREATE = 16;
    private static final int OPEN_FN_FAIL_IF_EXISTS = 0;
    private static final int OPEN_FN_OPEN = 1;
    private static final int OPEN_FN_TRUNC = 2;
    private static final int BATCH_LIMIT;
    int flags;
    int desiredAccess;
    int searchAttributes;
    int fileAttributes;
    int creationTime;
    int openFunction;
    int allocationSize;
    
    SmbComOpenAndX(final String fileName, final int access, final int flags, final ServerMessageBlock andx) {
        super(andx);
        this.path = fileName;
        this.command = 45;
        this.desiredAccess = (access & 0x3);
        if (this.desiredAccess == 3) {
            this.desiredAccess = 2;
        }
        this.desiredAccess |= 0x40;
        this.desiredAccess &= 0xFFFFFFFE;
        this.searchAttributes = 22;
        this.fileAttributes = 0;
        if ((flags & 0x40) == 0x40) {
            if ((flags & 0x10) == 0x10) {
                this.openFunction = 18;
            }
            else {
                this.openFunction = 2;
            }
        }
        else if ((flags & 0x10) == 0x10) {
            if ((flags & 0x20) == 0x20) {
                this.openFunction = 16;
            }
            else {
                this.openFunction = 17;
            }
        }
        else {
            this.openFunction = 1;
        }
    }
    
    int getBatchLimit(final byte command) {
        return (command == 46) ? SmbComOpenAndX.BATCH_LIMIT : 0;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.flags, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.desiredAccess, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.searchAttributes, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.fileAttributes, dst, dstIndex);
        dstIndex += 2;
        this.creationTime = 0;
        ServerMessageBlock.writeInt4(this.creationTime, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.openFunction, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.allocationSize, dst, dstIndex);
        dstIndex += 4;
        for (int i = 0; i < 8; ++i) {
            dst[dstIndex++] = 0;
        }
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        if (this.useUnicode) {
            dst[dstIndex++] = 0;
        }
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
        return new String("SmbComOpenAndX[" + super.toString() + ",flags=0x" + Hexdump.toHexString(this.flags, 2) + ",desiredAccess=0x" + Hexdump.toHexString(this.desiredAccess, 4) + ",searchAttributes=0x" + Hexdump.toHexString(this.searchAttributes, 4) + ",fileAttributes=0x" + Hexdump.toHexString(this.fileAttributes, 4) + ",creationTime=" + new Date(this.creationTime) + ",openFunction=0x" + Hexdump.toHexString(this.openFunction, 2) + ",allocationSize=" + this.allocationSize + ",fileName=" + this.path + "]");
    }
    
    static {
        BATCH_LIMIT = Config.getInt("jcifs.smb.client.OpenAndX.ReadAndX", 1);
    }
}

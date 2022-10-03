package jcifs.smb;

import jcifs.util.Hexdump;

class SmbComNTCreateAndX extends AndXServerMessageBlock
{
    static final int FILE_SUPERSEDE = 0;
    static final int FILE_OPEN = 1;
    static final int FILE_CREATE = 2;
    static final int FILE_OPEN_IF = 3;
    static final int FILE_OVERWRITE = 4;
    static final int FILE_OVERWRITE_IF = 5;
    static final int FILE_WRITE_THROUGH = 2;
    static final int FILE_SEQUENTIAL_ONLY = 4;
    static final int FILE_SYNCHRONOUS_IO_ALERT = 16;
    static final int FILE_SYNCHRONOUS_IO_NONALERT = 32;
    static final int SECURITY_CONTEXT_TRACKING = 1;
    static final int SECURITY_EFFECTIVE_ONLY = 2;
    private int flags;
    private int rootDirectoryFid;
    private int desiredAccess;
    private int extFileAttributes;
    private int shareAccess;
    private int createDisposition;
    private int createOptions;
    private int impersonationLevel;
    private long allocationSize;
    private byte securityFlags;
    private int namelen_index;
    
    SmbComNTCreateAndX(final String name, final int flags, final int access, final int shareAccess, final int extFileAttributes, final int createOptions, final ServerMessageBlock andx) {
        super(andx);
        this.path = name;
        this.command = -94;
        this.desiredAccess = access;
        this.desiredAccess |= 0x89;
        this.extFileAttributes = extFileAttributes;
        this.shareAccess = shareAccess;
        if ((flags & 0x40) == 0x40) {
            if ((flags & 0x10) == 0x10) {
                this.createDisposition = 5;
            }
            else {
                this.createDisposition = 4;
            }
        }
        else if ((flags & 0x10) == 0x10) {
            if ((flags & 0x20) == 0x20) {
                this.createDisposition = 2;
            }
            else {
                this.createDisposition = 3;
            }
        }
        else {
            this.createDisposition = 1;
        }
        if ((createOptions & 0x1) == 0x0) {
            this.createOptions = (createOptions | 0x40);
        }
        else {
            this.createOptions = createOptions;
        }
        this.impersonationLevel = 2;
        this.securityFlags = 3;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dst[dstIndex++] = 0;
        this.namelen_index = dstIndex;
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.flags, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.rootDirectoryFid, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.desiredAccess, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt8(this.allocationSize, dst, dstIndex);
        dstIndex += 8;
        ServerMessageBlock.writeInt4(this.extFileAttributes, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.shareAccess, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.createDisposition, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.createOptions, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt4(this.impersonationLevel, dst, dstIndex);
        dstIndex += 4;
        dst[dstIndex++] = this.securityFlags;
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        final int n = this.writeString(this.path, dst, dstIndex);
        ServerMessageBlock.writeInt2(this.useUnicode ? (this.path.length() * 2) : n, dst, this.namelen_index);
        return n;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComNTCreateAndX[" + super.toString() + ",flags=0x" + Hexdump.toHexString(this.flags, 2) + ",rootDirectoryFid=" + this.rootDirectoryFid + ",desiredAccess=0x" + Hexdump.toHexString(this.desiredAccess, 4) + ",allocationSize=" + this.allocationSize + ",extFileAttributes=0x" + Hexdump.toHexString(this.extFileAttributes, 4) + ",shareAccess=0x" + Hexdump.toHexString(this.shareAccess, 4) + ",createDisposition=0x" + Hexdump.toHexString(this.createDisposition, 4) + ",createOptions=0x" + Hexdump.toHexString(this.createOptions, 8) + ",impersonationLevel=0x" + Hexdump.toHexString(this.impersonationLevel, 4) + ",securityFlags=0x" + Hexdump.toHexString(this.securityFlags, 2) + ",name=" + this.path + "]");
    }
}

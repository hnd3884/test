package jcifs.smb;

import jcifs.util.Hexdump;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.util.Date;
import jcifs.util.LogStream;
import jcifs.util.transport.Request;
import jcifs.util.transport.Response;

abstract class ServerMessageBlock extends Response implements Request, SmbConstants
{
    static LogStream log;
    static final byte[] header;
    static final byte SMB_COM_CREATE_DIRECTORY = 0;
    static final byte SMB_COM_DELETE_DIRECTORY = 1;
    static final byte SMB_COM_CLOSE = 4;
    static final byte SMB_COM_DELETE = 6;
    static final byte SMB_COM_RENAME = 7;
    static final byte SMB_COM_QUERY_INFORMATION = 8;
    static final byte SMB_COM_WRITE = 11;
    static final byte SMB_COM_CHECK_DIRECTORY = 16;
    static final byte SMB_COM_TRANSACTION = 37;
    static final byte SMB_COM_TRANSACTION_SECONDARY = 38;
    static final byte SMB_COM_MOVE = 42;
    static final byte SMB_COM_ECHO = 43;
    static final byte SMB_COM_OPEN_ANDX = 45;
    static final byte SMB_COM_READ_ANDX = 46;
    static final byte SMB_COM_WRITE_ANDX = 47;
    static final byte SMB_COM_TRANSACTION2 = 50;
    static final byte SMB_COM_FIND_CLOSE2 = 52;
    static final byte SMB_COM_TREE_DISCONNECT = 113;
    static final byte SMB_COM_NEGOTIATE = 114;
    static final byte SMB_COM_SESSION_SETUP_ANDX = 115;
    static final byte SMB_COM_LOGOFF_ANDX = 116;
    static final byte SMB_COM_TREE_CONNECT_ANDX = 117;
    static final byte SMB_COM_NT_TRANSACT = -96;
    static final byte SMB_COM_NT_TRANSACT_SECONDARY = -95;
    static final byte SMB_COM_NT_CREATE_ANDX = -94;
    byte command;
    byte flags;
    int headerStart;
    int length;
    int batchLevel;
    int errorCode;
    int flags2;
    int tid;
    int pid;
    int uid;
    int mid;
    int wordCount;
    int byteCount;
    boolean useUnicode;
    boolean received;
    long responseTimeout;
    int signSeq;
    boolean verifyFailed;
    NtlmPasswordAuthentication auth;
    SmbExtendedAuthenticator authenticator;
    String path;
    SigningDigest digest;
    ServerMessageBlock response;
    
    static void writeInt2(final long val, final byte[] dst, int dstIndex) {
        dst[dstIndex] = (byte)val;
        dst[++dstIndex] = (byte)(val >> 8);
    }
    
    static void writeInt4(long val, final byte[] dst, int dstIndex) {
        dst[dstIndex] = (byte)val;
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >> 8);
    }
    
    static int readInt2(final byte[] src, final int srcIndex) {
        return (src[srcIndex] & 0xFF) + ((src[srcIndex + 1] & 0xFF) << 8);
    }
    
    static int readInt4(final byte[] src, final int srcIndex) {
        return (src[srcIndex] & 0xFF) + ((src[srcIndex + 1] & 0xFF) << 8) + ((src[srcIndex + 2] & 0xFF) << 16) + ((src[srcIndex + 3] & 0xFF) << 24);
    }
    
    static long readInt8(final byte[] src, final int srcIndex) {
        return ((long)readInt4(src, srcIndex) & 0xFFFFFFFFL) + ((long)readInt4(src, srcIndex + 4) << 32);
    }
    
    static void writeInt8(long val, final byte[] dst, int dstIndex) {
        dst[dstIndex] = (byte)val;
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >>= 8);
        dst[++dstIndex] = (byte)(val >> 8);
    }
    
    static long readTime(final byte[] src, final int srcIndex) {
        final int low = readInt4(src, srcIndex);
        final int hi = readInt4(src, srcIndex + 4);
        long t = (long)hi << 32 | ((long)low & 0xFFFFFFFFL);
        t = t / 10000L - 11644473600000L;
        return t;
    }
    
    static void writeTime(long t, final byte[] dst, final int dstIndex) {
        if (t != 0L) {
            t = (t + 11644473600000L) * 10000L;
        }
        writeInt8(t, dst, dstIndex);
    }
    
    static long readUTime(final byte[] buffer, final int bufferIndex) {
        return readInt4(buffer, bufferIndex) * 1000L;
    }
    
    static void writeUTime(long t, final byte[] dst, final int dstIndex) {
        if (t == 0L || t == -1L) {
            writeInt4(-1L, dst, dstIndex);
            return;
        }
        synchronized (SmbConstants.TZ) {
            if (SmbConstants.TZ.inDaylightTime(new Date())) {
                if (!SmbConstants.TZ.inDaylightTime(new Date(t))) {
                    t -= 3600000L;
                }
            }
            else if (SmbConstants.TZ.inDaylightTime(new Date(t))) {
                t += 3600000L;
            }
        }
        writeInt4((int)(t / 1000L), dst, dstIndex);
    }
    
    ServerMessageBlock() {
        this.responseTimeout = 1L;
        this.auth = null;
        this.authenticator = null;
        this.digest = null;
        this.flags = 24;
        this.pid = SmbConstants.PID;
        this.batchLevel = 0;
    }
    
    void reset() {
        this.flags = 24;
        this.flags2 = 0;
        this.errorCode = 0;
        this.received = false;
    }
    
    int writeString(final String str, final byte[] dst, final int dstIndex) {
        return this.writeString(str, dst, dstIndex, this.useUnicode);
    }
    
    int writeString(final String str, final byte[] dst, int dstIndex, final boolean useUnicode) {
        final int start = dstIndex;
        try {
            if (useUnicode) {
                if ((dstIndex - this.headerStart) % 2 != 0) {
                    dst[dstIndex++] = 0;
                }
                System.arraycopy(str.getBytes("UnicodeLittleUnmarked"), 0, dst, dstIndex, str.length() * 2);
                dstIndex += str.length() * 2;
                dst[dstIndex++] = 0;
                dst[dstIndex++] = 0;
            }
            else {
                final byte[] b = str.getBytes(SmbConstants.OEM_ENCODING);
                System.arraycopy(b, 0, dst, dstIndex, b.length);
                dstIndex += b.length;
                dst[dstIndex++] = 0;
            }
        }
        catch (final UnsupportedEncodingException uee) {
            final LogStream log = ServerMessageBlock.log;
            if (LogStream.level > 1) {
                uee.printStackTrace(ServerMessageBlock.log);
            }
        }
        return dstIndex - start;
    }
    
    String readString(final byte[] src, final int srcIndex) {
        return this.readString(src, srcIndex, 256, this.useUnicode);
    }
    
    String readString(final byte[] src, int srcIndex, final int maxLen, final boolean useUnicode) {
        int len = 0;
        String str = null;
        try {
            if (useUnicode) {
                if ((srcIndex - this.headerStart) % 2 != 0) {
                    ++srcIndex;
                }
                while (src[srcIndex + len] != 0 || src[srcIndex + len + 1] != 0) {
                    len += 2;
                    if (len > maxLen) {
                        final LogStream log = ServerMessageBlock.log;
                        if (LogStream.level > 0) {
                            Hexdump.hexdump(System.err, src, srcIndex, (maxLen < 128) ? (maxLen + 8) : 128);
                        }
                        throw new RuntimeException("zero termination not found");
                    }
                }
                str = new String(src, srcIndex, len, "UnicodeLittleUnmarked");
            }
            else {
                while (src[srcIndex + len] != 0) {
                    if (++len > maxLen) {
                        final LogStream log2 = ServerMessageBlock.log;
                        if (LogStream.level > 0) {
                            Hexdump.hexdump(System.err, src, srcIndex, (maxLen < 128) ? (maxLen + 8) : 128);
                        }
                        throw new RuntimeException("zero termination not found");
                    }
                }
                str = new String(src, srcIndex, len, SmbConstants.OEM_ENCODING);
            }
        }
        catch (final UnsupportedEncodingException uee) {
            final LogStream log3 = ServerMessageBlock.log;
            if (LogStream.level > 1) {
                uee.printStackTrace(ServerMessageBlock.log);
            }
        }
        return str;
    }
    
    int stringWireLength(final String str, final int offset) {
        int len = str.length() + 1;
        if (this.useUnicode) {
            len = str.length() * 2 + 2;
            len = ((offset % 2 != 0) ? (len + 1) : len);
        }
        return len;
    }
    
    int readStringLength(final byte[] src, final int srcIndex, final int max) {
        int len = 0;
        while (src[srcIndex + len] != 0) {
            if (len++ > max) {
                throw new RuntimeException("zero termination not found: " + this);
            }
        }
        return len;
    }
    
    int encode(final byte[] dst, int dstIndex) {
        final int headerStart = dstIndex;
        this.headerStart = headerStart;
        final int start = headerStart;
        dstIndex += this.writeHeaderWireFormat(dst, dstIndex);
        this.wordCount = this.writeParameterWordsWireFormat(dst, dstIndex + 1);
        dst[dstIndex++] = (byte)(this.wordCount / 2 & 0xFF);
        dstIndex += this.wordCount;
        this.wordCount /= 2;
        this.byteCount = this.writeBytesWireFormat(dst, dstIndex + 2);
        dst[dstIndex++] = (byte)(this.byteCount & 0xFF);
        dst[dstIndex++] = (byte)(this.byteCount >> 8 & 0xFF);
        dstIndex += this.byteCount;
        this.length = dstIndex - start;
        if (this.digest != null) {
            this.digest.sign(dst, this.headerStart, this.length, this, this.response);
        }
        return this.length;
    }
    
    int decode(final byte[] buffer, int bufferIndex) {
        final int headerStart = bufferIndex;
        this.headerStart = headerStart;
        final int start = headerStart;
        bufferIndex += this.readHeaderWireFormat(buffer, bufferIndex);
        this.wordCount = buffer[bufferIndex++];
        if (this.wordCount != 0) {
            final int n;
            if ((n = this.readParameterWordsWireFormat(buffer, bufferIndex)) != this.wordCount * 2) {
                final LogStream log = ServerMessageBlock.log;
                if (LogStream.level >= 5) {
                    ServerMessageBlock.log.println("wordCount * 2=" + this.wordCount * 2 + " but readParameterWordsWireFormat returned " + n);
                }
            }
            bufferIndex += this.wordCount * 2;
        }
        this.byteCount = readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        if (this.byteCount != 0) {
            final int n;
            if ((n = this.readBytesWireFormat(buffer, bufferIndex)) != this.byteCount) {
                final LogStream log2 = ServerMessageBlock.log;
                if (LogStream.level >= 5) {
                    ServerMessageBlock.log.println("byteCount=" + this.byteCount + " but readBytesWireFormat returned " + n);
                }
            }
            bufferIndex += this.byteCount;
        }
        return this.length = bufferIndex - start;
    }
    
    int writeHeaderWireFormat(final byte[] dst, int dstIndex) {
        System.arraycopy(ServerMessageBlock.header, 0, dst, dstIndex, ServerMessageBlock.header.length);
        dst[dstIndex + 4] = this.command;
        dst[dstIndex + 9] = this.flags;
        writeInt2(this.flags2, dst, dstIndex + 9 + 1);
        dstIndex += 24;
        writeInt2(this.tid, dst, dstIndex);
        writeInt2(this.pid, dst, dstIndex + 2);
        writeInt2(this.uid, dst, dstIndex + 4);
        writeInt2(this.mid, dst, dstIndex + 6);
        return 32;
    }
    
    int readHeaderWireFormat(final byte[] buffer, final int bufferIndex) {
        this.command = buffer[bufferIndex + 4];
        this.errorCode = readInt4(buffer, bufferIndex + 5);
        this.flags = buffer[bufferIndex + 9];
        this.flags2 = readInt2(buffer, bufferIndex + 9 + 1);
        this.tid = readInt2(buffer, bufferIndex + 24);
        this.pid = readInt2(buffer, bufferIndex + 24 + 2);
        this.uid = readInt2(buffer, bufferIndex + 24 + 4);
        this.mid = readInt2(buffer, bufferIndex + 24 + 6);
        return 32;
    }
    
    boolean isResponse() {
        return (this.flags & 0x80) == 0x80;
    }
    
    abstract int writeParameterWordsWireFormat(final byte[] p0, final int p1);
    
    abstract int writeBytesWireFormat(final byte[] p0, final int p1);
    
    abstract int readParameterWordsWireFormat(final byte[] p0, final int p1);
    
    abstract int readBytesWireFormat(final byte[] p0, final int p1);
    
    public int hashCode() {
        return this.mid;
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof ServerMessageBlock && ((ServerMessageBlock)obj).mid == this.mid;
    }
    
    public String toString() {
        String c = null;
        switch (this.command) {
            case 114: {
                c = "SMB_COM_NEGOTIATE";
                break;
            }
            case 115: {
                c = "SMB_COM_SESSION_SETUP_ANDX";
                break;
            }
            case 117: {
                c = "SMB_COM_TREE_CONNECT_ANDX";
                break;
            }
            case 8: {
                c = "SMB_COM_QUERY_INFORMATION";
                break;
            }
            case 16: {
                c = "SMB_COM_CHECK_DIRECTORY";
                break;
            }
            case 37: {
                c = "SMB_COM_TRANSACTION";
                break;
            }
            case 50: {
                c = "SMB_COM_TRANSACTION2";
                break;
            }
            case 38: {
                c = "SMB_COM_TRANSACTION_SECONDARY";
                break;
            }
            case 52: {
                c = "SMB_COM_FIND_CLOSE2";
                break;
            }
            case 113: {
                c = "SMB_COM_TREE_DISCONNECT";
                break;
            }
            case 116: {
                c = "SMB_COM_LOGOFF_ANDX";
                break;
            }
            case 43: {
                c = "SMB_COM_ECHO";
                break;
            }
            case 42: {
                c = "SMB_COM_MOVE";
                break;
            }
            case 7: {
                c = "SMB_COM_RENAME";
                break;
            }
            case 6: {
                c = "SMB_COM_DELETE";
                break;
            }
            case 1: {
                c = "SMB_COM_DELETE_DIRECTORY";
                break;
            }
            case -94: {
                c = "SMB_COM_NT_CREATE_ANDX";
                break;
            }
            case 45: {
                c = "SMB_COM_OPEN_ANDX";
                break;
            }
            case 46: {
                c = "SMB_COM_READ_ANDX";
                break;
            }
            case 4: {
                c = "SMB_COM_CLOSE";
                break;
            }
            case 47: {
                c = "SMB_COM_WRITE_ANDX";
                break;
            }
            case 0: {
                c = "SMB_COM_CREATE_DIRECTORY";
                break;
            }
            case -96: {
                c = "SMB_COM_NT_TRANSACT";
                break;
            }
            case -95: {
                c = "SMB_COM_NT_TRANSACT_SECONDARY";
                break;
            }
            default: {
                c = "UNKNOWN";
                break;
            }
        }
        final String str = (this.errorCode == 0) ? "0" : SmbException.getMessageByCode(this.errorCode);
        return new String("command=" + c + ",received=" + this.received + ",errorCode=" + str + ",flags=0x" + Hexdump.toHexString(this.flags & 0xFF, 4) + ",flags2=0x" + Hexdump.toHexString(this.flags2, 4) + ",signSeq=" + this.signSeq + ",tid=" + this.tid + ",pid=" + this.pid + ",uid=" + this.uid + ",mid=" + this.mid + ",wordCount=" + this.wordCount + ",byteCount=" + this.byteCount);
    }
    
    static {
        ServerMessageBlock.log = LogStream.getInstance();
        header = new byte[] { -1, 83, 77, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}

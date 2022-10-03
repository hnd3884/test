package jcifs.ntlmssp;

import jcifs.Config;

public abstract class NtlmMessage implements NtlmFlags
{
    protected static final byte[] NTLMSSP_SIGNATURE;
    private static final String OEM_ENCODING;
    private int flags;
    
    public int getFlags() {
        return this.flags;
    }
    
    public void setFlags(final int flags) {
        this.flags = flags;
    }
    
    public boolean getFlag(final int flag) {
        return (this.getFlags() & flag) != 0x0;
    }
    
    public void setFlag(final int flag, final boolean value) {
        this.setFlags(value ? (this.getFlags() | flag) : (this.getFlags() & (-1 ^ flag)));
    }
    
    static int readULong(final byte[] src, final int index) {
        return (src[index] & 0xFF) | (src[index + 1] & 0xFF) << 8 | (src[index + 2] & 0xFF) << 16 | (src[index + 3] & 0xFF) << 24;
    }
    
    static int readUShort(final byte[] src, final int index) {
        return (src[index] & 0xFF) | (src[index + 1] & 0xFF) << 8;
    }
    
    static byte[] readSecurityBuffer(final byte[] src, final int index) {
        final int length = readUShort(src, index);
        final int offset = readULong(src, index + 4);
        final byte[] buffer = new byte[length];
        System.arraycopy(src, offset, buffer, 0, length);
        return buffer;
    }
    
    static void writeULong(final byte[] dest, final int offset, final int ulong) {
        dest[offset] = (byte)(ulong & 0xFF);
        dest[offset + 1] = (byte)(ulong >> 8 & 0xFF);
        dest[offset + 2] = (byte)(ulong >> 16 & 0xFF);
        dest[offset + 3] = (byte)(ulong >> 24 & 0xFF);
    }
    
    static void writeUShort(final byte[] dest, final int offset, final int ushort) {
        dest[offset] = (byte)(ushort & 0xFF);
        dest[offset + 1] = (byte)(ushort >> 8 & 0xFF);
    }
    
    static void writeSecurityBuffer(final byte[] dest, final int offset, final int bodyOffset, final byte[] src) {
        final int length = (src != null) ? src.length : 0;
        if (length == 0) {
            return;
        }
        writeUShort(dest, offset, length);
        writeUShort(dest, offset + 2, length);
        writeULong(dest, offset + 4, bodyOffset);
        System.arraycopy(src, 0, dest, bodyOffset, length);
    }
    
    static String getOEMEncoding() {
        return NtlmMessage.OEM_ENCODING;
    }
    
    public abstract byte[] toByteArray();
    
    static {
        NTLMSSP_SIGNATURE = new byte[] { 78, 84, 76, 77, 83, 83, 80, 0 };
        OEM_ENCODING = Config.getProperty("jcifs.encoding", "Cp850");
    }
}

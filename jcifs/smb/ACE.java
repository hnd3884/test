package jcifs.smb;

import jcifs.util.Hexdump;

public class ACE
{
    public static final int FILE_READ_DATA = 1;
    public static final int FILE_WRITE_DATA = 2;
    public static final int FILE_APPEND_DATA = 4;
    public static final int FILE_READ_EA = 8;
    public static final int FILE_WRITE_EA = 16;
    public static final int FILE_EXECUTE = 32;
    public static final int FILE_DELETE = 64;
    public static final int FILE_READ_ATTRIBUTES = 128;
    public static final int FILE_WRITE_ATTRIBUTES = 256;
    public static final int DELETE = 65536;
    public static final int READ_CONTROL = 131072;
    public static final int WRITE_DAC = 262144;
    public static final int WRITE_OWNER = 524288;
    public static final int SYNCHRONIZE = 1048576;
    public static final int GENERIC_ALL = 268435456;
    public static final int GENERIC_EXECUTE = 536870912;
    public static final int GENERIC_WRITE = 1073741824;
    public static final int GENERIC_READ = Integer.MIN_VALUE;
    public static final int FLAGS_OBJECT_INHERIT = 1;
    public static final int FLAGS_CONTAINER_INHERIT = 2;
    public static final int FLAGS_NO_PROPAGATE = 4;
    public static final int FLAGS_INHERIT_ONLY = 8;
    public static final int FLAGS_INHERITED = 16;
    boolean allow;
    int flags;
    int access;
    SID sid;
    
    public boolean isAllow() {
        return this.allow;
    }
    
    public boolean isInherited() {
        return (this.flags & 0x10) != 0x0;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public String getApplyToText() {
        switch (this.flags & 0xB) {
            case 0: {
                return "This folder only";
            }
            case 3: {
                return "This folder, subfolders and files";
            }
            case 11: {
                return "Subfolders and files only";
            }
            case 2: {
                return "This folder and subfolders";
            }
            case 10: {
                return "Subfolders only";
            }
            case 1: {
                return "This folder and files";
            }
            case 9: {
                return "Files only";
            }
            default: {
                return "Invalid";
            }
        }
    }
    
    public int getAccessMask() {
        return this.access;
    }
    
    public SID getSID() {
        return this.sid;
    }
    
    int decode(final byte[] buf, int bi) {
        this.allow = (buf[bi++] == 0);
        this.flags = (buf[bi++] & 0xFF);
        final int size = ServerMessageBlock.readInt2(buf, bi);
        bi += 2;
        this.access = ServerMessageBlock.readInt4(buf, bi);
        bi += 4;
        this.sid = new SID(buf, bi);
        return size;
    }
    
    void appendCol(final StringBuffer sb, final String str, final int width) {
        sb.append(str);
        for (int count = width - str.length(), i = 0; i < count; ++i) {
            sb.append(' ');
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.isAllow() ? "Allow " : "Deny  ");
        this.appendCol(sb, this.sid.toDisplayString(), 25);
        sb.append(" 0x").append(Hexdump.toHexString(this.access, 8)).append(' ');
        sb.append(this.isInherited() ? "Inherited " : "Direct    ");
        this.appendCol(sb, this.getApplyToText(), 34);
        return sb.toString();
    }
}

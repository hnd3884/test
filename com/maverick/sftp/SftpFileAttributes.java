package com.maverick.sftp;

import java.util.Enumeration;
import com.maverick.util.ByteArrayWriter;
import java.util.Date;
import java.io.IOException;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.UnsignedInteger32;
import com.maverick.util.UnsignedInteger64;
import java.util.Hashtable;
import java.util.Vector;

public class SftpFileAttributes
{
    public static final int SSH_FILEXFER_TYPE_REGULAR = 1;
    public static final int SSH_FILEXFER_TYPE_DIRECTORY = 2;
    public static final int SSH_FILEXFER_TYPE_SYMLINK = 3;
    public static final int SSH_FILEXFER_TYPE_SPECIAL = 4;
    public static final int SSH_FILEXFER_TYPE_UNKNOWN = 5;
    private Vector q;
    private Hashtable m;
    public static final int S_IFMT = 61440;
    public static final int S_IFSOCK = 49152;
    public static final int S_IFLNK = 40960;
    public static final int S_IFREG = 32768;
    public static final int S_IFBLK = 24576;
    public static final int S_IFDIR = 16384;
    public static final int S_IFCHR = 8192;
    public static final int S_IFIFO = 4096;
    public static final int S_ISUID = 2048;
    public static final int S_ISGID = 1024;
    public static final int S_IRUSR = 256;
    public static final int S_IWUSR = 128;
    public static final int S_IXUSR = 64;
    public static final int S_IRGRP = 32;
    public static final int S_IWGRP = 16;
    public static final int S_IXGRP = 8;
    public static final int S_IROTH = 4;
    public static final int S_IWOTH = 2;
    public static final int S_IXOTH = 1;
    int r;
    long j;
    int t;
    UnsignedInteger64 d;
    String c;
    String o;
    UnsignedInteger32 e;
    UnsignedInteger64 f;
    UnsignedInteger32 b;
    UnsignedInteger64 h;
    UnsignedInteger32 g;
    UnsignedInteger64 s;
    UnsignedInteger32 l;
    String n;
    String k;
    char[] p;
    SftpSubsystemChannel i;
    
    public SftpFileAttributes(final SftpSubsystemChannel i, final int t) {
        this.q = new Vector();
        this.m = new Hashtable();
        this.r = 3;
        this.j = 0L;
        this.d = null;
        this.c = null;
        this.o = null;
        this.e = null;
        this.f = null;
        this.b = null;
        this.h = null;
        this.g = null;
        this.s = null;
        this.l = null;
        this.p = new char[] { 'p', 'c', 'd', 'b', '-', 'l', 's' };
        this.i = i;
        this.r = i.getVersion();
        this.t = t;
    }
    
    public int getType() {
        return this.t;
    }
    
    public SftpFileAttributes(final SftpSubsystemChannel i, final ByteArrayReader byteArrayReader) throws IOException {
        this.q = new Vector();
        this.m = new Hashtable();
        this.r = 3;
        this.j = 0L;
        this.d = null;
        this.c = null;
        this.o = null;
        this.e = null;
        this.f = null;
        this.b = null;
        this.h = null;
        this.g = null;
        this.s = null;
        this.l = null;
        this.p = new char[] { 'p', 'c', 'd', 'b', '-', 'l', 's' };
        this.i = i;
        this.r = i.getVersion();
        if (byteArrayReader.available() >= 4) {
            this.j = byteArrayReader.readInt();
        }
        if (this.r > 3 && byteArrayReader.available() > 0) {
            this.t = byteArrayReader.read();
        }
        if (this.isFlagSet(1L) && byteArrayReader.available() >= 8) {
            final byte[] array = new byte[8];
            byteArrayReader.read(array);
            this.d = new UnsignedInteger64(array);
        }
        if (this.r <= 3 && this.isFlagSet(2L) && byteArrayReader.available() >= 8) {
            this.c = String.valueOf(byteArrayReader.readInt());
            this.o = String.valueOf(byteArrayReader.readInt());
        }
        else if (this.r > 3 && this.isFlagSet(128L) && byteArrayReader.available() >= 8) {
            this.c = byteArrayReader.readString(i.getCharsetEncoding());
            this.o = byteArrayReader.readString(i.getCharsetEncoding());
        }
        if (this.isFlagSet(4L) && byteArrayReader.available() >= 4) {
            this.e = new UnsignedInteger32(byteArrayReader.readInt());
        }
        if (this.r <= 3 && this.isFlagSet(8L) && byteArrayReader.available() >= 8) {
            this.f = new UnsignedInteger64(byteArrayReader.readInt());
            this.s = new UnsignedInteger64(byteArrayReader.readInt());
        }
        else if (this.r > 3 && byteArrayReader.available() > 0) {
            if (this.isFlagSet(8L) && byteArrayReader.available() >= 8) {
                this.f = byteArrayReader.readUINT64();
            }
            if (this.isFlagSet(256L) && byteArrayReader.available() >= 4) {
                this.b = byteArrayReader.readUINT32();
            }
        }
        if (this.r > 3 && byteArrayReader.available() > 0) {
            if (this.isFlagSet(16L) && byteArrayReader.available() >= 8) {
                this.h = byteArrayReader.readUINT64();
            }
            if (this.isFlagSet(256L) && byteArrayReader.available() >= 4) {
                this.g = byteArrayReader.readUINT32();
            }
        }
        if (this.r > 3 && byteArrayReader.available() > 0) {
            if (this.isFlagSet(32L) && byteArrayReader.available() >= 8) {
                this.s = byteArrayReader.readUINT64();
            }
            if (this.isFlagSet(256L) && byteArrayReader.available() >= 4) {
                this.l = byteArrayReader.readUINT32();
            }
        }
        if (this.r > 3 && this.isFlagSet(64L) && byteArrayReader.available() >= 4) {
            final int n = (int)byteArrayReader.readInt();
            if (n > 0 && byteArrayReader.available() >= n) {
                for (int n2 = (int)byteArrayReader.readInt(), j = 0; j < n2; ++j) {
                    this.q.addElement(new ACL((int)byteArrayReader.readInt(), (int)byteArrayReader.readInt(), (int)byteArrayReader.readInt(), byteArrayReader.readString()));
                }
            }
        }
        if (this.r >= 3 && this.isFlagSet(-2147483648L) && byteArrayReader.available() >= 4) {
            for (int n3 = (int)byteArrayReader.readInt(), k = 0; k < n3; ++k) {
                if (byteArrayReader.available() >= 8) {
                    this.m.put(byteArrayReader.readString(), byteArrayReader.readBinaryString());
                }
            }
        }
    }
    
    public String getUID() {
        if (this.n != null) {
            return this.n;
        }
        if (this.c != null) {
            return this.c;
        }
        return "";
    }
    
    public void setUID(final String c) {
        if (this.r > 3) {
            this.j |= 0x80L;
        }
        else {
            this.j |= 0x2L;
        }
        this.c = c;
    }
    
    public void setGID(final String o) {
        if (this.r > 3) {
            this.j |= 0x80L;
        }
        else {
            this.j |= 0x2L;
        }
        this.o = o;
    }
    
    public String getGID() {
        if (this.k != null) {
            return this.k;
        }
        if (this.o != null) {
            return this.o;
        }
        return "";
    }
    
    public boolean hasUID() {
        return this.c != null;
    }
    
    public boolean hasGID() {
        return this.o != null;
    }
    
    public void setSize(final UnsignedInteger64 d) {
        this.d = d;
        if (d != null) {
            this.j |= 0x1L;
        }
        else {
            this.j ^= 0x1L;
        }
    }
    
    public UnsignedInteger64 getSize() {
        if (this.d != null) {
            return this.d;
        }
        return new UnsignedInteger64("0");
    }
    
    public boolean hasSize() {
        return this.d != null;
    }
    
    public void setPermissions(final UnsignedInteger32 e) {
        this.e = e;
        if (e != null) {
            this.j |= 0x4L;
        }
        else {
            this.j ^= 0x4L;
        }
    }
    
    public void setPermissionsFromMaskString(final String s) {
        if (s.length() != 4) {
            throw new IllegalArgumentException("Mask length must be 4");
        }
        try {
            this.setPermissions(new UnsignedInteger32(String.valueOf(Integer.parseInt(s, 8))));
        }
        catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("Mask must be 4 digit octal number.");
        }
    }
    
    public void setPermissionsFromUmaskString(final String s) {
        if (s.length() != 4) {
            throw new IllegalArgumentException("umask length must be 4");
        }
        try {
            this.setPermissions(new UnsignedInteger32(String.valueOf(Integer.parseInt(s, 8) ^ 0x1FF)));
        }
        catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("umask must be 4 digit octal number");
        }
    }
    
    public void setPermissions(final String s) {
        int n = 0;
        if (this.e != null) {
            n = (n | (((this.e.longValue() & 0xF000L) == 0xF000L) ? 61440 : 0) | (((this.e.longValue() & 0xC000L) == 0xC000L) ? 49152 : 0) | (((this.e.longValue() & 0xA000L) == 0xA000L) ? 40960 : 0) | (((this.e.longValue() & 0x8000L) == 0x8000L) ? 32768 : 0) | (((this.e.longValue() & 0x6000L) == 0x6000L) ? 24576 : 0) | (((this.e.longValue() & 0x4000L) == 0x4000L) ? 16384 : 0) | (((this.e.longValue() & 0x2000L) == 0x2000L) ? 8192 : 0) | (((this.e.longValue() & 0x1000L) == 0x1000L) ? 4096 : 0) | (((this.e.longValue() & 0x800L) == 0x800L) ? 2048 : 0) | (((this.e.longValue() & 0x400L) == 0x400L) ? 1024 : 0));
        }
        final int length = s.length();
        if (length >= 1) {
            n |= ((s.charAt(0) == 'r') ? 256 : 0);
        }
        if (length >= 2) {
            n |= ((s.charAt(1) == 'w') ? 128 : 0);
        }
        if (length >= 3) {
            n |= ((s.charAt(2) == 'x') ? 64 : 0);
        }
        if (length >= 4) {
            n |= ((s.charAt(3) == 'r') ? 32 : 0);
        }
        if (length >= 5) {
            n |= ((s.charAt(4) == 'w') ? 16 : 0);
        }
        if (length >= 6) {
            n |= ((s.charAt(5) == 'x') ? 8 : 0);
        }
        if (length >= 7) {
            n |= ((s.charAt(6) == 'r') ? 4 : 0);
        }
        if (length >= 8) {
            n |= ((s.charAt(7) == 'w') ? 2 : 0);
        }
        if (length >= 9) {
            n |= ((s.charAt(8) == 'x') ? 1 : 0);
        }
        this.setPermissions(new UnsignedInteger32(n));
    }
    
    public UnsignedInteger32 getPermissions() {
        if (this.e != null) {
            return this.e;
        }
        return new UnsignedInteger32(0L);
    }
    
    public void setTimes(final UnsignedInteger64 f, final UnsignedInteger64 s) {
        this.f = f;
        this.s = s;
        if (f != null) {
            this.j |= 0x8L;
        }
        else {
            this.j ^= 0x8L;
        }
    }
    
    public UnsignedInteger64 getAccessedTime() {
        return this.f;
    }
    
    public UnsignedInteger64 getModifiedTime() {
        if (this.s != null) {
            return this.s;
        }
        return new UnsignedInteger64(0L);
    }
    
    public Date getModifiedDateTime() {
        long n = 0L;
        if (this.s != null) {
            n = this.s.longValue() * 1000L;
        }
        if (this.l != null) {
            n += this.l.longValue() / 1000000L;
        }
        return new Date(n);
    }
    
    public Date getCreationDateTime() {
        long n = 0L;
        if (this.h != null) {
            n = this.h.longValue() * 1000L;
        }
        if (this.g != null) {
            n += this.g.longValue() / 1000000L;
        }
        return new Date(n);
    }
    
    public Date getAccessedDateTime() {
        long n = 0L;
        if (this.f != null) {
            n = this.f.longValue() * 1000L;
        }
        if (this.f != null) {
            n += this.b.longValue() / 1000000L;
        }
        return new Date(n);
    }
    
    public UnsignedInteger64 getCreationTime() {
        if (this.h != null) {
            return this.h;
        }
        return new UnsignedInteger64(0L);
    }
    
    public boolean isFlagSet(final long n) {
        return (this.j & (n & 0xFFFFFFFFL)) == (n & 0xFFFFFFFFL);
    }
    
    public byte[] toByteArray() throws IOException {
        final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
        byteArrayWriter.writeInt(this.j);
        if (this.r > 3) {
            byteArrayWriter.write(this.t);
        }
        if (this.isFlagSet(1L)) {
            byteArrayWriter.write(this.d.toByteArray());
        }
        if (this.r <= 3 && this.isFlagSet(2L)) {
            if (this.c != null) {
                try {
                    byteArrayWriter.writeInt(Long.parseLong(this.c));
                }
                catch (final NumberFormatException ex) {
                    byteArrayWriter.writeInt(0);
                }
            }
            else {
                byteArrayWriter.writeInt(0);
            }
            if (this.o != null) {
                try {
                    byteArrayWriter.writeInt(Long.parseLong(this.o));
                }
                catch (final NumberFormatException ex2) {
                    byteArrayWriter.writeInt(0);
                }
            }
            else {
                byteArrayWriter.writeInt(0);
            }
        }
        else if (this.r > 3 && this.isFlagSet(128L)) {
            if (this.c != null) {
                byteArrayWriter.writeString(this.c, this.i.getCharsetEncoding());
            }
            else {
                byteArrayWriter.writeString("");
            }
            if (this.o != null) {
                byteArrayWriter.writeString(this.o, this.i.getCharsetEncoding());
            }
            else {
                byteArrayWriter.writeString("");
            }
        }
        if (this.isFlagSet(4L)) {
            byteArrayWriter.writeInt(this.e.longValue());
        }
        if (this.r <= 3 && this.isFlagSet(8L)) {
            byteArrayWriter.writeInt(this.f.longValue());
            byteArrayWriter.writeInt(this.s.longValue());
        }
        else if (this.r > 3) {
            if (this.isFlagSet(8L)) {
                byteArrayWriter.writeUINT64(this.f);
            }
            if (this.isFlagSet(256L)) {
                byteArrayWriter.writeUINT32(this.b);
            }
            if (this.isFlagSet(16L)) {
                byteArrayWriter.writeUINT64(this.h);
            }
            if (this.isFlagSet(256L)) {
                byteArrayWriter.writeUINT32(this.g);
            }
            if (this.isFlagSet(32L)) {
                byteArrayWriter.writeUINT64(this.s);
            }
            if (this.isFlagSet(256L)) {
                byteArrayWriter.writeUINT32(this.l);
            }
        }
        if (this.isFlagSet(64L)) {
            final ByteArrayWriter byteArrayWriter2 = new ByteArrayWriter();
            final Enumeration elements = this.q.elements();
            byteArrayWriter2.writeInt(this.q.size());
            while (elements.hasMoreElements()) {
                final ACL acl = (ACL)elements.nextElement();
                byteArrayWriter2.writeInt(acl.getType());
                byteArrayWriter2.writeInt(acl.getFlags());
                byteArrayWriter2.writeInt(acl.getMask());
                byteArrayWriter2.writeString(acl.getWho());
            }
            byteArrayWriter.writeBinaryString(byteArrayWriter2.toByteArray());
        }
        if (this.isFlagSet(-2147483648L)) {
            byteArrayWriter.writeInt(this.m.size());
            final Enumeration keys = this.m.keys();
            while (keys.hasMoreElements()) {
                final String s = (String)keys.nextElement();
                byteArrayWriter.writeString(s);
                byteArrayWriter.writeBinaryString((byte[])this.m.get(s));
            }
        }
        return byteArrayWriter.toByteArray();
    }
    
    private int c(int n, final int n2) {
        n >>>= n2;
        return (((n & 0x4) != 0x0) ? 4 : 0) + (((n & 0x2) != 0x0) ? 2 : 0) + (((n & 0x1) != 0x0) ? 1 : 0);
    }
    
    private String b(int n, final int n2) {
        n >>>= n2;
        final String string = (((n & 0x4) != 0x0) ? "r" : "-") + (((n & 0x2) != 0x0) ? "w" : "-");
        String s;
        if ((n2 == 6 && (this.e.longValue() & 0x800L) == 0x800L) || (n2 == 3 && (this.e.longValue() & 0x400L) == 0x400L)) {
            s = string + (((n & 0x1) != 0x0) ? "s" : "S");
        }
        else {
            s = string + (((n & 0x1) != 0x0) ? "x" : "-");
        }
        return s;
    }
    
    public String getPermissionsString() {
        if (this.e != null) {
            final StringBuffer sb = new StringBuffer();
            if (((int)this.e.longValue() & 0xF000) > 0) {
                sb.append(this.p[(int)(this.e.longValue() & 0xF000L) >>> 13]);
            }
            else {
                sb.append('-');
            }
            sb.append(this.b((int)this.e.longValue(), 6));
            sb.append(this.b((int)this.e.longValue(), 3));
            sb.append(this.b((int)this.e.longValue(), 0));
            return sb.toString();
        }
        return "";
    }
    
    public String getMaskString() {
        final StringBuffer sb = new StringBuffer();
        if (this.e != null) {
            final int n = (int)this.e.longValue();
            sb.append('0');
            sb.append(this.c(n, 6));
            sb.append(this.c(n, 3));
            sb.append(this.c(n, 0));
        }
        else {
            sb.append("----");
        }
        return sb.toString();
    }
    
    public boolean isDirectory() {
        if (this.i.getVersion() > 3) {
            return this.t == 2;
        }
        return this.e != null && (this.e.longValue() & 0x4000L) == 0x4000L;
    }
    
    public boolean isFile() {
        if (this.i.getVersion() > 3) {
            return this.t == 1;
        }
        return this.e != null && (this.e.longValue() & 0x8000L) == 0x8000L;
    }
    
    public boolean isLink() {
        if (this.i.getVersion() > 3) {
            return this.t == 3;
        }
        return this.e != null && (this.e.longValue() & 0xA000L) == 0xA000L;
    }
    
    public boolean isFifo() {
        return this.e != null && (this.e.longValue() & 0x1000L) == 0x1000L;
    }
    
    public boolean isBlock() {
        return this.e != null && (this.e.longValue() & 0x6000L) == 0x6000L;
    }
    
    public boolean isCharacter() {
        return this.e != null && (this.e.longValue() & 0x2000L) == 0x2000L;
    }
    
    public boolean isSocket() {
        return this.e != null && (this.e.longValue() & 0xC000L) == 0xC000L;
    }
    
    void b(final String n) {
        this.n = n;
    }
    
    void c(final String k) {
        this.k = k;
    }
}

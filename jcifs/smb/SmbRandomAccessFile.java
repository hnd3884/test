package jcifs.smb;

import java.io.IOException;
import jcifs.util.Encdec;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.io.DataInput;
import java.io.DataOutput;

public class SmbRandomAccessFile implements DataOutput, DataInput
{
    private static final int WRITE_OPTIONS = 2114;
    private SmbFile file;
    private long fp;
    private int openFlags;
    private int access;
    private int readSize;
    private int writeSize;
    private int ch;
    private int options;
    private byte[] tmp;
    private SmbComWriteAndXResponse write_andx_resp;
    
    public SmbRandomAccessFile(final String url, final String mode, final int shareAccess) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url, "", null, shareAccess), mode);
    }
    
    public SmbRandomAccessFile(final SmbFile file, final String mode) throws SmbException, MalformedURLException, UnknownHostException {
        this.access = 0;
        this.options = 0;
        this.tmp = new byte[8];
        this.write_andx_resp = null;
        this.file = file;
        if (mode.equals("r")) {
            this.openFlags = 17;
        }
        else {
            if (!mode.equals("rw")) {
                throw new IllegalArgumentException("Invalid mode");
            }
            this.openFlags = 23;
            this.write_andx_resp = new SmbComWriteAndXResponse();
            this.options = 2114;
            this.access = 3;
        }
        file.open(this.openFlags, this.access, 128, this.options);
        this.readSize = file.tree.session.transport.rcv_buf_size - 70;
        this.writeSize = file.tree.session.transport.snd_buf_size - 70;
        this.fp = 0L;
    }
    
    public int read() throws SmbException {
        if (this.read(this.tmp, 0, 1) == -1) {
            return -1;
        }
        return this.tmp[0] & 0xFF;
    }
    
    public int read(final byte[] b) throws SmbException {
        return this.read(b, 0, b.length);
    }
    
    public int read(final byte[] b, final int off, int len) throws SmbException {
        if (len <= 0) {
            return 0;
        }
        final long start = this.fp;
        if (!this.file.isOpen()) {
            this.file.open(this.openFlags, 0, 128, this.options);
        }
        final SmbComReadAndXResponse response = new SmbComReadAndXResponse(b, off);
        int n;
        int r;
        do {
            r = ((len > this.readSize) ? this.readSize : len);
            this.file.send(new SmbComReadAndX(this.file.fid, this.fp, r, null), response);
            if ((n = response.dataLength) <= 0) {
                return (int)((this.fp - start > 0L) ? (this.fp - start) : -1L);
            }
            this.fp += n;
            len -= n;
            final SmbComReadAndXResponse smbComReadAndXResponse = response;
            smbComReadAndXResponse.off += n;
        } while (len > 0 && n == r);
        return (int)(this.fp - start);
    }
    
    public final void readFully(final byte[] b) throws SmbException {
        this.readFully(b, 0, b.length);
    }
    
    public final void readFully(final byte[] b, final int off, final int len) throws SmbException {
        int n = 0;
        do {
            final int count = this.read(b, off + n, len - n);
            if (count < 0) {
                throw new SmbException("EOF");
            }
            n += count;
            this.fp += count;
        } while (n < len);
    }
    
    public int skipBytes(final int n) throws SmbException {
        if (n > 0) {
            this.fp += n;
            return n;
        }
        return 0;
    }
    
    public void write(final int b) throws SmbException {
        this.tmp[0] = (byte)b;
        this.write(this.tmp, 0, 1);
    }
    
    public void write(final byte[] b) throws SmbException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] b, int off, int len) throws SmbException {
        if (len <= 0) {
            return;
        }
        if (!this.file.isOpen()) {
            this.file.open(this.openFlags, 0, 128, this.options);
        }
        do {
            final int w = (len > this.writeSize) ? this.writeSize : len;
            this.file.send(new SmbComWriteAndX(this.file.fid, this.fp, len - w, b, off, w, null), this.write_andx_resp);
            this.fp += this.write_andx_resp.count;
            len -= (int)this.write_andx_resp.count;
            off += (int)this.write_andx_resp.count;
        } while (len > 0);
    }
    
    public long getFilePointer() throws SmbException {
        return this.fp;
    }
    
    public void seek(final long pos) throws SmbException {
        this.fp = pos;
    }
    
    public long length() throws SmbException {
        return this.file.length();
    }
    
    public void setLength(final long newLength) throws SmbException {
        if (!this.file.isOpen()) {
            this.file.open(this.openFlags, 0, 128, this.options);
        }
        final SmbComWriteResponse rsp = new SmbComWriteResponse();
        this.file.send(new SmbComWrite(this.file.fid, (int)(newLength & 0xFFFFFFFFL), 0, this.tmp, 0, 0), rsp);
    }
    
    public void close() throws SmbException {
        this.file.close();
    }
    
    public final boolean readBoolean() throws SmbException {
        if (this.read(this.tmp, 0, 1) < 0) {
            throw new SmbException("EOF");
        }
        return this.tmp[0] != 0;
    }
    
    public final byte readByte() throws SmbException {
        if (this.read(this.tmp, 0, 1) < 0) {
            throw new SmbException("EOF");
        }
        return this.tmp[0];
    }
    
    public final int readUnsignedByte() throws SmbException {
        if (this.read(this.tmp, 0, 1) < 0) {
            throw new SmbException("EOF");
        }
        return this.tmp[0] & 0xFF;
    }
    
    public final short readShort() throws SmbException {
        if (this.read(this.tmp, 0, 2) < 0) {
            throw new SmbException("EOF");
        }
        return Encdec.dec_uint16be(this.tmp, 0);
    }
    
    public final int readUnsignedShort() throws SmbException {
        if (this.read(this.tmp, 0, 2) < 0) {
            throw new SmbException("EOF");
        }
        return Encdec.dec_uint16be(this.tmp, 0) & 0xFFFF;
    }
    
    public final char readChar() throws SmbException {
        if (this.read(this.tmp, 0, 2) < 0) {
            throw new SmbException("EOF");
        }
        return (char)Encdec.dec_uint16be(this.tmp, 0);
    }
    
    public final int readInt() throws SmbException {
        if (this.read(this.tmp, 0, 4) < 0) {
            throw new SmbException("EOF");
        }
        return Encdec.dec_uint32be(this.tmp, 0);
    }
    
    public final long readLong() throws SmbException {
        if (this.read(this.tmp, 0, 8) < 0) {
            throw new SmbException("EOF");
        }
        return Encdec.dec_uint64be(this.tmp, 0);
    }
    
    public final float readFloat() throws SmbException {
        if (this.read(this.tmp, 0, 4) < 0) {
            throw new SmbException("EOF");
        }
        return Encdec.dec_floatbe(this.tmp, 0);
    }
    
    public final double readDouble() throws SmbException {
        if (this.read(this.tmp, 0, 8) < 0) {
            throw new SmbException("EOF");
        }
        return Encdec.dec_doublebe(this.tmp, 0);
    }
    
    public final String readLine() throws SmbException {
        final StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;
        while (!eol) {
            switch (c = this.read()) {
                case -1:
                case 10: {
                    eol = true;
                    continue;
                }
                case 13: {
                    eol = true;
                    final long cur = this.fp;
                    if (this.read() != 10) {
                        this.fp = cur;
                        continue;
                    }
                    continue;
                }
                default: {
                    input.append((char)c);
                    continue;
                }
            }
        }
        if (c == -1 && input.length() == 0) {
            return null;
        }
        return input.toString();
    }
    
    public final String readUTF() throws SmbException {
        final int size = this.readUnsignedShort();
        final byte[] b = new byte[size];
        this.read(b, 0, size);
        try {
            return Encdec.dec_utf8(b, 0, size);
        }
        catch (final IOException ioe) {
            throw new SmbException("", ioe);
        }
    }
    
    public final void writeBoolean(final boolean v) throws SmbException {
        this.tmp[0] = (byte)(v ? 1 : 0);
        this.write(this.tmp, 0, 1);
    }
    
    public final void writeByte(final int v) throws SmbException {
        this.tmp[0] = (byte)v;
        this.write(this.tmp, 0, 1);
    }
    
    public final void writeShort(final int v) throws SmbException {
        Encdec.enc_uint16be((short)v, this.tmp, 0);
        this.write(this.tmp, 0, 2);
    }
    
    public final void writeChar(final int v) throws SmbException {
        Encdec.enc_uint16be((short)v, this.tmp, 0);
        this.write(this.tmp, 0, 2);
    }
    
    public final void writeInt(final int v) throws SmbException {
        Encdec.enc_uint32be(v, this.tmp, 0);
        this.write(this.tmp, 0, 4);
    }
    
    public final void writeLong(final long v) throws SmbException {
        Encdec.enc_uint64be(v, this.tmp, 0);
        this.write(this.tmp, 0, 8);
    }
    
    public final void writeFloat(final float v) throws SmbException {
        Encdec.enc_floatbe(v, this.tmp, 0);
        this.write(this.tmp, 0, 4);
    }
    
    public final void writeDouble(final double v) throws SmbException {
        Encdec.enc_doublebe(v, this.tmp, 0);
        this.write(this.tmp, 0, 8);
    }
    
    public final void writeBytes(final String s) throws SmbException {
        final byte[] b = s.getBytes();
        this.write(b, 0, b.length);
    }
    
    public final void writeChars(final String s) throws SmbException {
        final int clen = s.length();
        final int blen = 2 * clen;
        final byte[] b = new byte[blen];
        final char[] c = new char[clen];
        s.getChars(0, clen, c, 0);
        int i = 0;
        int j = 0;
        while (i < clen) {
            b[j++] = (byte)(c[i] >>> 8);
            b[j++] = (byte)(c[i] >>> 0);
            ++i;
        }
        this.write(b, 0, blen);
    }
    
    public final void writeUTF(final String str) throws SmbException {
        final int len = str.length();
        int size = 0;
        for (int i = 0; i < len; ++i) {
            final int ch = str.charAt(i);
            size += ((ch > 127) ? ((ch > 2047) ? 3 : 2) : 1);
        }
        final byte[] dst = new byte[size];
        this.writeShort(size);
        try {
            Encdec.enc_utf8(str, dst, 0, size);
        }
        catch (final IOException ioe) {
            throw new SmbException("", ioe);
        }
        this.write(dst, 0, size);
    }
    
    public SmbRandomAccessFile(final String url, final String mode, final int shareAccess, final SmbExtendedAuthenticator authenticator) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url, authenticator, shareAccess), mode);
    }
}

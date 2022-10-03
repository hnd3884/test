package jcifs.smb;

import jcifs.util.LogStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.io.OutputStream;

public class SmbFileOutputStream extends OutputStream
{
    private SmbFile file;
    private boolean append;
    private boolean useNTSmbs;
    private int openFlags;
    private int access;
    private int writeSize;
    private long fp;
    private byte[] tmp;
    private SmbComWriteAndX reqx;
    private SmbComWriteAndXResponse rspx;
    private SmbComWrite req;
    private SmbComWriteResponse rsp;
    
    public SmbFileOutputStream(final String url) throws SmbException, MalformedURLException, UnknownHostException {
        this(url, false);
    }
    
    public SmbFileOutputStream(final SmbFile file) throws SmbException, MalformedURLException, UnknownHostException {
        this(file, false);
    }
    
    public SmbFileOutputStream(final String url, final boolean append) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url), append);
    }
    
    public SmbFileOutputStream(final SmbFile file, final boolean append) throws SmbException, MalformedURLException, UnknownHostException {
        this(file, append, append ? 22 : 82);
    }
    
    public SmbFileOutputStream(final String url, final int shareAccess) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url, "", null, shareAccess), false);
    }
    
    SmbFileOutputStream(final SmbFile file, final boolean append, final int openFlags) throws SmbException, MalformedURLException, UnknownHostException {
        this.tmp = new byte[1];
        this.file = file;
        this.append = append;
        this.openFlags = openFlags;
        this.access = (openFlags >>> 16 & 0xFFFF);
        if (append) {
            try {
                this.fp = file.length();
            }
            catch (final SmbAuthException sae) {
                throw sae;
            }
            catch (final SmbException se) {
                this.fp = 0L;
            }
        }
        if (file instanceof SmbNamedPipe && file.unc.startsWith("\\pipe\\")) {
            file.unc = file.unc.substring(5);
            file.send(new TransWaitNamedPipe("\\pipe" + file.unc), new TransWaitNamedPipeResponse());
        }
        file.open(openFlags, this.access | 0x2, 128, 0);
        this.openFlags &= 0xFFFFFFAF;
        this.writeSize = file.tree.session.transport.snd_buf_size - 70;
        this.useNTSmbs = file.tree.session.transport.hasCapability(16);
        if (this.useNTSmbs) {
            this.reqx = new SmbComWriteAndX();
            this.rspx = new SmbComWriteAndXResponse();
        }
        else {
            this.req = new SmbComWrite();
            this.rsp = new SmbComWriteResponse();
        }
    }
    
    public void close() throws IOException {
        this.file.close();
        this.tmp = null;
    }
    
    public void write(final int b) throws IOException {
        this.tmp[0] = (byte)b;
        this.write(this.tmp, 0, 1);
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] b, int off, int len) throws IOException {
        if (len <= 0) {
            return;
        }
        if (this.tmp == null) {
            throw new IOException("Bad file descriptor");
        }
        if (!this.file.isOpen()) {
            if (this.file instanceof SmbNamedPipe) {
                this.file.send(new TransWaitNamedPipe("\\pipe" + this.file.unc), new TransWaitNamedPipeResponse());
            }
            this.file.open(this.openFlags, this.access | 0x2, 128, 0);
            if (this.append) {
                this.fp = this.file.length();
            }
        }
        final SmbFile file = this.file;
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 4) {
            final SmbFile file2 = this.file;
            SmbFile.log.println("write: fid=" + this.file.fid + ",off=" + off + ",len=" + len);
        }
        do {
            final int w = (len > this.writeSize) ? this.writeSize : len;
            if (this.useNTSmbs) {
                this.reqx.setParam(this.file.fid, this.fp, len - w, b, off, w);
                this.file.send(this.reqx, this.rspx);
                this.fp += this.rspx.count;
                len -= (int)this.rspx.count;
                off += (int)this.rspx.count;
            }
            else {
                this.req.setParam(this.file.fid, this.fp, len - w, b, off, w);
                this.fp += this.rsp.count;
                len -= (int)this.rsp.count;
                off += (int)this.rsp.count;
                this.file.send(this.req, this.rsp);
            }
        } while (len > 0);
    }
    
    public SmbFileOutputStream(final String url, final SmbExtendedAuthenticator authenticator) throws SmbException, MalformedURLException, UnknownHostException {
        this(url, authenticator, false);
    }
    
    public SmbFileOutputStream(final String url, final SmbExtendedAuthenticator authenticator, final boolean append) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url, authenticator), append);
    }
}

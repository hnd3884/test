package jcifs.smb;

import jcifs.util.LogStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.io.InputStream;

public class SmbFileInputStream extends InputStream
{
    private long fp;
    private int readSize;
    private int openFlags;
    private int access;
    private byte[] tmp;
    SmbFile file;
    
    public SmbFileInputStream(final String url) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url));
    }
    
    public SmbFileInputStream(final SmbFile file) throws SmbException, MalformedURLException, UnknownHostException {
        this(file, 1);
    }
    
    SmbFileInputStream(final SmbFile file, final int openFlags) throws SmbException, MalformedURLException, UnknownHostException {
        this.tmp = new byte[1];
        this.file = file;
        this.openFlags = (openFlags & 0xFFFF);
        this.access = (openFlags >>> 16 & 0xFFFF);
        if (file.type != 16) {
            file.open(openFlags, this.access, 128, 0);
            this.openFlags &= 0xFFFFFFAF;
        }
        else {
            file.connect0();
        }
        this.readSize = Math.min(file.tree.session.transport.rcv_buf_size - 70, file.tree.session.transport.server.maxBufferSize - 70);
    }
    
    public void close() throws IOException {
        this.file.close();
        this.tmp = null;
    }
    
    public int read() throws IOException {
        if (this.read(this.tmp, 0, 1) == -1) {
            return -1;
        }
        return this.tmp[0] & 0xFF;
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.readDirect(b, off, len);
    }
    
    public int readDirect(final byte[] b, final int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }
        final long start = this.fp;
        if (this.tmp == null) {
            throw new IOException("Bad file descriptor");
        }
        this.file.open(this.openFlags, this.access, 128, 0);
        final SmbFile file = this.file;
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 4) {
            final SmbFile file2 = this.file;
            SmbFile.log.println("read: fid=" + this.file.fid + ",off=" + off + ",len=" + len);
        }
        final SmbComReadAndXResponse response = new SmbComReadAndXResponse(b, off);
        if (this.file.type == 16) {
            response.responseTimeout = 0L;
        }
        int n;
        int r;
        do {
            r = ((len > this.readSize) ? this.readSize : len);
            final SmbFile file3 = this.file;
            final LogStream log2 = SmbFile.log;
            if (LogStream.level >= 4) {
                final SmbFile file4 = this.file;
                SmbFile.log.println("read: len=" + len + ",r=" + r + ",fp=" + this.fp);
            }
            try {
                this.file.send(new SmbComReadAndX(this.file.fid, this.fp, r, null), response);
            }
            catch (final SmbException se) {
                if (this.file.type == 16 && se.getNtStatus() == -1073741493) {
                    return -1;
                }
                throw se;
            }
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
    
    public int available() throws IOException {
        if (this.file.type != 16) {
            return 0;
        }
        final SmbNamedPipe pipe = (SmbNamedPipe)this.file;
        this.file.open(32, pipe.pipeType & 0xFF0000, 128, 0);
        final TransPeekNamedPipe req = new TransPeekNamedPipe(this.file.unc, this.file.fid);
        final TransPeekNamedPipeResponse resp = new TransPeekNamedPipeResponse(pipe);
        pipe.send(req, resp);
        if (resp.status == 1 || resp.status == 4) {
            this.file.opened = false;
            return 0;
        }
        return resp.available;
    }
    
    public long skip(final long n) throws IOException {
        if (n > 0L) {
            this.fp += n;
            return n;
        }
        return 0L;
    }
    
    public SmbFileInputStream(final String url, final SmbExtendedAuthenticator authenticator) throws SmbException, MalformedURLException, UnknownHostException {
        this(new SmbFile(url, authenticator));
    }
}

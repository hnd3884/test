package jcifs.smb;

import jcifs.util.LogStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

class TransactNamedPipeInputStream extends SmbFileInputStream
{
    private static final int INIT_PIPE_SIZE = 4096;
    private byte[] pipe_buf;
    private int beg_idx;
    private int nxt_idx;
    private int used;
    private boolean dcePipe;
    Object lock;
    
    TransactNamedPipeInputStream(final SmbNamedPipe pipe) throws SmbException, MalformedURLException, UnknownHostException {
        super(pipe, (pipe.pipeType & 0xFFFF00FF) | 0x20);
        this.pipe_buf = new byte[4096];
        this.dcePipe = ((pipe.pipeType & 0x600) != 0x600);
        this.lock = new Object();
    }
    
    public int read() throws IOException {
        int result = -1;
        synchronized (this.lock) {
            try {
                while (this.used == 0) {
                    this.lock.wait();
                }
            }
            catch (final InterruptedException ie) {
                throw new IOException(ie.getMessage());
            }
            result = (this.pipe_buf[this.beg_idx] & 0xFF);
            this.beg_idx = (this.beg_idx + 1) % this.pipe_buf.length;
        }
        return result;
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public int read(final byte[] b, int off, final int len) throws IOException {
        int result = -1;
        if (len <= 0) {
            return 0;
        }
        synchronized (this.lock) {
            try {
                while (this.used == 0) {
                    this.lock.wait();
                }
            }
            catch (final InterruptedException ie) {
                throw new IOException(ie.getMessage());
            }
            final int i = this.pipe_buf.length - this.beg_idx;
            result = ((len > this.used) ? this.used : len);
            if (this.used > i && result > i) {
                System.arraycopy(this.pipe_buf, this.beg_idx, b, off, i);
                off += i;
                System.arraycopy(this.pipe_buf, 0, b, off, result - i);
            }
            else {
                System.arraycopy(this.pipe_buf, this.beg_idx, b, off, result);
            }
            this.used -= result;
            this.beg_idx = (this.beg_idx + result) % this.pipe_buf.length;
        }
        return result;
    }
    
    public int available() throws IOException {
        final SmbFile file = this.file;
        final LogStream log = SmbFile.log;
        if (LogStream.level >= 3) {
            final SmbFile file2 = this.file;
            SmbFile.log.println("Named Pipe available() does not apply to TRANSACT Named Pipes");
        }
        return 0;
    }
    
    int receive(final byte[] b, int off, final int len) {
        if (len > this.pipe_buf.length - this.used) {
            int new_size = this.pipe_buf.length * 2;
            if (len > new_size - this.used) {
                new_size = len + this.used;
            }
            byte[] tmp = this.pipe_buf;
            this.pipe_buf = new byte[new_size];
            final int i = tmp.length - this.beg_idx;
            if (this.used > i) {
                System.arraycopy(tmp, this.beg_idx, this.pipe_buf, 0, i);
                System.arraycopy(tmp, 0, this.pipe_buf, i, this.used - i);
            }
            else {
                System.arraycopy(tmp, this.beg_idx, this.pipe_buf, 0, this.used);
            }
            this.beg_idx = 0;
            this.nxt_idx = this.used;
            tmp = null;
        }
        final int i = this.pipe_buf.length - this.nxt_idx;
        if (len > i) {
            System.arraycopy(b, off, this.pipe_buf, this.nxt_idx, i);
            off += i;
            System.arraycopy(b, off, this.pipe_buf, 0, len - i);
        }
        else {
            System.arraycopy(b, off, this.pipe_buf, this.nxt_idx, len);
        }
        this.nxt_idx = (this.nxt_idx + len) % this.pipe_buf.length;
        this.used += len;
        return len;
    }
    
    public int dce_read(final byte[] b, final int off, final int len) throws IOException {
        return super.read(b, off, len);
    }
}

package HTTPClient;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.io.InputStream;

final class RespInputStream extends InputStream implements GlobalConstants
{
    private static boolean dontTimeoutBody;
    private StreamDemultiplexor demux;
    private ResponseHandler resph;
    boolean closed;
    private boolean dont_truncate;
    private byte[] buffer;
    private boolean interrupted;
    private int offset;
    private int end;
    int count;
    private byte[] ch;
    
    RespInputStream(final StreamDemultiplexor demux, final ResponseHandler resph) {
        this.closed = false;
        this.dont_truncate = false;
        this.interrupted = false;
        this.ch = new byte[1];
        this.demux = demux;
        this.resph = resph;
    }
    
    public synchronized int read() throws IOException {
        final int rcvd = this.read(this.ch, 0, 1);
        if (rcvd == 1) {
            return this.ch[0] & 0xFF;
        }
        return -1;
    }
    
    public synchronized int read(final byte[] b, final int off, int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        final int left = this.end - this.offset;
        if (this.buffer == null || (left == 0 && this.interrupted)) {
            if (this.resph.resp.cd_type != 1) {
                Log.write(4, "RspIS: Reading stream " + this.hashCode());
            }
            int rcvd;
            if (RespInputStream.dontTimeoutBody && this.resph.resp.cd_type != 1) {
                rcvd = this.demux.read(b, off, len, this.resph, 0);
            }
            else {
                rcvd = this.demux.read(b, off, len, this.resph, this.resph.resp.timeout);
            }
            if (rcvd != -1 && this.resph.resp.got_headers) {
                this.count += rcvd;
            }
            return rcvd;
        }
        if (left == 0) {
            return -1;
        }
        len = ((len > left) ? left : len);
        System.arraycopy(this.buffer, this.offset, b, off, len);
        this.offset += len;
        return len;
    }
    
    public synchronized long skip(long num) throws IOException {
        if (this.closed) {
            return 0L;
        }
        final int left = this.end - this.offset;
        if (this.buffer != null && (left != 0 || !this.interrupted)) {
            num = ((num > left) ? left : num);
            this.offset += (int)num;
            return num;
        }
        final long skpd = this.demux.skip(num, this.resph);
        if (this.resph.resp.got_headers) {
            this.count += (int)skpd;
        }
        return skpd;
    }
    
    public synchronized int available() throws IOException {
        if (this.closed) {
            return 0;
        }
        if (this.buffer != null && (this.end - this.offset != 0 || !this.interrupted)) {
            return this.end - this.offset;
        }
        return this.demux.available(this.resph);
    }
    
    public synchronized void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (this.dont_truncate && (this.buffer == null || this.interrupted)) {
                this.readAll(this.resph.resp.timeout);
            }
            Log.write(4, "RspIS: User closed stream " + this.hashCode());
            this.demux.closeSocketIfAllStreamsClosed();
            if (this.dont_truncate) {
                try {
                    this.resph.resp.http_resp.invokeTrailerHandlers(false);
                }
                catch (final ModuleException me) {
                    throw new IOException(me.toString());
                }
            }
        }
    }
    
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }
    
    void readAll(final int timeout) throws IOException {
        Log.write(4, "RspIS: Read-all on stream " + this.hashCode());
        synchronized (this.resph.resp) {
            if (!this.resph.resp.got_headers) {
                final int sav_to = this.resph.resp.timeout;
                this.resph.resp.timeout = timeout;
                this.resph.resp.getStatusCode();
                this.resph.resp.timeout = sav_to;
            }
            monitorexit(this.resph.resp);
        }
        synchronized (this) {
            if (this.buffer != null && !this.interrupted) {
                monitorexit(this);
                return;
            }
            int rcvd = 0;
            try {
                if (this.closed) {
                    this.buffer = new byte[10000];
                    do {
                        this.count += rcvd;
                        rcvd = this.demux.read(this.buffer, 0, this.buffer.length, this.resph, timeout);
                    } while (rcvd != -1);
                    this.buffer = null;
                }
                else {
                    if (this.buffer == null) {
                        this.buffer = new byte[10000];
                        this.offset = 0;
                        this.end = 0;
                    }
                    while (true) {
                        rcvd = this.demux.read(this.buffer, this.end, this.buffer.length - this.end, this.resph, timeout);
                        if (rcvd < 0) {
                            break;
                        }
                        this.count += rcvd;
                        this.end += rcvd;
                        this.buffer = Util.resizeArray(this.buffer, this.end + 10000);
                    }
                }
            }
            catch (final InterruptedIOException iioe) {
                this.interrupted = true;
                throw iioe;
            }
            catch (final IOException ex) {
                this.buffer = null;
            }
            this.interrupted = false;
        }
    }
    
    synchronized void dontTruncate() {
        this.dont_truncate = true;
    }
    
    static {
        try {
            RespInputStream.dontTimeoutBody = Boolean.getBoolean("HTTPClient.dontTimeoutRespBody");
            if (RespInputStream.dontTimeoutBody) {
                Log.write(4, "RspIS: disabling timeouts when reading response body");
            }
        }
        catch (final Exception ex) {}
    }
}

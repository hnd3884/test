package HTTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class BufferedInputStream extends FilterInputStream
{
    private byte[] buffer;
    private int pos;
    private int end;
    private int mark_pos;
    private int lr_thrshld;
    
    BufferedInputStream(final InputStream stream) {
        super(stream);
        this.buffer = new byte[2000];
        this.mark_pos = -1;
        this.lr_thrshld = 1500;
    }
    
    public int read() throws IOException {
        if (this.pos >= this.end) {
            this.fillBuff();
        }
        return (this.end > this.pos) ? (this.buffer[this.pos++] & 0xFF) : -1;
    }
    
    public int read(final byte[] buf, final int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }
        if (this.pos >= this.end && len >= this.lr_thrshld && this.mark_pos < 0) {
            return super.in.read(buf, off, len);
        }
        if (this.pos >= this.end) {
            this.fillBuff();
        }
        if (this.pos >= this.end) {
            return -1;
        }
        final int left = this.end - this.pos;
        if (len > left) {
            len = left;
        }
        System.arraycopy(this.buffer, this.pos, buf, off, len);
        this.pos += len;
        return len;
    }
    
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        final int left = this.end - this.pos;
        if (n <= left) {
            this.pos += (int)n;
            return n;
        }
        this.pos = this.end;
        return left + super.in.skip(n - left);
    }
    
    private final void fillBuff() throws IOException {
        if (this.mark_pos > 0) {
            if (this.end >= this.buffer.length) {
                System.arraycopy(this.buffer, this.mark_pos, this.buffer, 0, this.end - this.mark_pos);
                this.pos = this.end - this.mark_pos;
            }
        }
        else if (this.mark_pos != 0 || this.end >= this.buffer.length) {
            this.pos = 0;
        }
        this.end = this.pos;
        final int got = super.in.read(this.buffer, this.pos, this.buffer.length - this.pos);
        if (got > 0) {
            this.end = this.pos + got;
        }
    }
    
    public int available() throws IOException {
        int avail = this.end - this.pos;
        if (avail == 0) {
            return super.in.available();
        }
        try {
            avail += super.in.available();
        }
        catch (final IOException ex) {}
        return avail;
    }
    
    void markForSearch() {
        this.mark_pos = this.pos;
    }
    
    int pastEnd(final byte[] search, final int[] search_cmp) {
        int idx = Util.findStr(search, search_cmp, this.buffer, this.mark_pos, this.pos);
        if (idx == -1) {
            this.mark_pos = ((this.pos > search.length) ? (this.pos - search.length) : 0);
        }
        else {
            final int eos = idx + search.length;
            idx = this.pos - eos;
            this.pos = eos;
            this.mark_pos = -1;
        }
        return idx;
    }
}

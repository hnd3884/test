package HTTPClient;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.io.FilterInputStream;

class MD5InputStream extends FilterInputStream
{
    private HashVerifier verifier;
    private MessageDigest md5;
    private long rcvd;
    private boolean closed;
    
    public MD5InputStream(final InputStream is, final HashVerifier verifier) {
        super(is);
        this.closed = false;
        this.verifier = verifier;
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException nsae) {
            throw new Error(nsae.toString());
        }
    }
    
    public synchronized int read() throws IOException {
        final int b = super.in.read();
        if (b != -1) {
            this.md5.update((byte)b);
        }
        else {
            this.real_close();
        }
        ++this.rcvd;
        return b;
    }
    
    public synchronized int read(final byte[] buf, final int off, final int len) throws IOException {
        final int num = super.in.read(buf, off, len);
        if (num > 0) {
            this.md5.update(buf, off, num);
        }
        else {
            this.real_close();
        }
        this.rcvd += num;
        return num;
    }
    
    public synchronized long skip(final long num) throws IOException {
        final byte[] tmp = new byte[(int)num];
        final int got = this.read(tmp, 0, (int)num);
        if (got > 0) {
            return got;
        }
        return 0L;
    }
    
    public synchronized void close() throws IOException {
        while (this.skip(10000L) > 0L) {}
        this.real_close();
    }
    
    private void real_close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        super.in.close();
        this.verifier.verifyHash(this.md5.digest(), this.rcvd);
    }
}

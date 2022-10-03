package cryptix.jce.provider.asn;

import java.io.IOException;
import java.io.InputStream;

public final class SubInputStream extends InputStream
{
    private int len;
    private final InputStream is;
    
    public int available() throws IOException {
        return (this.len > 0) ? this.is.available() : 0;
    }
    
    public int read() throws IOException {
        return (this.len-- <= 0) ? -1 : this.is.read();
    }
    
    public SubInputStream(final InputStream is, final int len) {
        if (len < 0) {
            throw new IllegalArgumentException("len: < 0");
        }
        this.is = is;
        this.len = len;
    }
}

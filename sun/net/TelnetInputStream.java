package sun.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class TelnetInputStream extends FilterInputStream
{
    boolean stickyCRLF;
    boolean seenCR;
    public boolean binaryMode;
    
    public TelnetInputStream(final InputStream inputStream, final boolean binaryMode) {
        super(inputStream);
        this.stickyCRLF = false;
        this.seenCR = false;
        this.binaryMode = false;
        this.binaryMode = binaryMode;
    }
    
    public void setStickyCRLF(final boolean stickyCRLF) {
        this.stickyCRLF = stickyCRLF;
    }
    
    @Override
    public int read() throws IOException {
        if (this.binaryMode) {
            return super.read();
        }
        if (this.seenCR) {
            this.seenCR = false;
            return 10;
        }
        final int read;
        if ((read = super.read()) != 13) {
            return read;
        }
        switch (super.read()) {
            default: {
                throw new TelnetProtocolException("misplaced CR in input");
            }
            case 0: {
                return 13;
            }
            case 10: {
                if (this.stickyCRLF) {
                    this.seenCR = true;
                    return 13;
                }
                return 10;
            }
        }
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, int n, int n2) throws IOException {
        if (this.binaryMode) {
            return super.read(array, n, n2);
        }
        final int n3 = n;
        while (--n2 >= 0) {
            final int read = this.read();
            if (read == -1) {
                break;
            }
            array[n++] = (byte)read;
        }
        return (n > n3) ? (n - n3) : -1;
    }
}

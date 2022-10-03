package sun.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

public class TelnetOutputStream extends BufferedOutputStream
{
    boolean stickyCRLF;
    boolean seenCR;
    public boolean binaryMode;
    
    public TelnetOutputStream(final OutputStream outputStream, final boolean binaryMode) {
        super(outputStream);
        this.stickyCRLF = false;
        this.seenCR = false;
        this.binaryMode = false;
        this.binaryMode = binaryMode;
    }
    
    public void setStickyCRLF(final boolean stickyCRLF) {
        this.stickyCRLF = stickyCRLF;
    }
    
    @Override
    public void write(int n) throws IOException {
        if (this.binaryMode) {
            super.write(n);
            return;
        }
        if (this.seenCR) {
            if (n != 10) {
                super.write(0);
            }
            super.write(n);
            if (n != 13) {
                this.seenCR = false;
            }
        }
        else {
            if (n == 10) {
                super.write(13);
                super.write(10);
                return;
            }
            if (n == 13) {
                if (this.stickyCRLF) {
                    this.seenCR = true;
                }
                else {
                    super.write(13);
                    n = 0;
                }
            }
            super.write(n);
        }
    }
    
    @Override
    public void write(final byte[] array, int n, int n2) throws IOException {
        if (this.binaryMode) {
            super.write(array, n, n2);
            return;
        }
        while (--n2 >= 0) {
            this.write(array[n++]);
        }
    }
}

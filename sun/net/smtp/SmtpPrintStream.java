package sun.net.smtp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;

class SmtpPrintStream extends PrintStream
{
    private SmtpClient target;
    private int lastc;
    
    SmtpPrintStream(final OutputStream outputStream, final SmtpClient target) throws UnsupportedEncodingException {
        super(outputStream, false, target.getEncoding());
        this.lastc = 10;
        this.target = target;
    }
    
    @Override
    public void close() {
        if (this.target == null) {
            return;
        }
        if (this.lastc != 10) {
            this.write(10);
        }
        try {
            this.target.issueCommand(".\r\n", 250);
            this.target.message = null;
            this.out = null;
            this.target = null;
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void write(final int lastc) {
        try {
            if (this.lastc == 10 && lastc == 46) {
                this.out.write(46);
            }
            if (lastc == 10 && this.lastc != 13) {
                this.out.write(13);
            }
            this.out.write(lastc);
            this.lastc = lastc;
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void write(final byte[] array, int n, int n2) {
        try {
            int lastc = this.lastc;
            while (--n2 >= 0) {
                final byte b = array[n++];
                if (lastc == 10 && b == 46) {
                    this.out.write(46);
                }
                if (b == 10 && lastc != 13) {
                    this.out.write(13);
                }
                this.out.write(b);
                lastc = b;
            }
            this.lastc = lastc;
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void print(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.write(s.charAt(i));
        }
    }
}

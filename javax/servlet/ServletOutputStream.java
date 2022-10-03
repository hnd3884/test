package javax.servlet;

import java.io.IOException;
import java.io.CharConversionException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.io.OutputStream;

public abstract class ServletOutputStream extends OutputStream
{
    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
    private static final ResourceBundle lStrings;
    
    protected ServletOutputStream() {
    }
    
    public void print(String s) throws IOException {
        if (s == null) {
            s = "null";
        }
        final int len = s.length();
        final byte[] buffer = new byte[len];
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if ((c & '\uff00') != 0x0) {
                String errMsg = ServletOutputStream.lStrings.getString("err.not_iso8859_1");
                final Object[] errArgs = { c };
                errMsg = MessageFormat.format(errMsg, errArgs);
                throw new CharConversionException(errMsg);
            }
            buffer[i] = (byte)(c & '\u00ff');
        }
        this.write(buffer);
    }
    
    public void print(final boolean b) throws IOException {
        String msg;
        if (b) {
            msg = ServletOutputStream.lStrings.getString("value.true");
        }
        else {
            msg = ServletOutputStream.lStrings.getString("value.false");
        }
        this.print(msg);
    }
    
    public void print(final char c) throws IOException {
        this.print(String.valueOf(c));
    }
    
    public void print(final int i) throws IOException {
        this.print(String.valueOf(i));
    }
    
    public void print(final long l) throws IOException {
        this.print(String.valueOf(l));
    }
    
    public void print(final float f) throws IOException {
        this.print(String.valueOf(f));
    }
    
    public void print(final double d) throws IOException {
        this.print(String.valueOf(d));
    }
    
    public void println() throws IOException {
        this.print("\r\n");
    }
    
    public void println(final String s) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("\r\n");
        this.print(sb.toString());
    }
    
    public void println(final boolean b) throws IOException {
        final StringBuilder sb = new StringBuilder();
        if (b) {
            sb.append(ServletOutputStream.lStrings.getString("value.true"));
        }
        else {
            sb.append(ServletOutputStream.lStrings.getString("value.false"));
        }
        sb.append("\r\n");
        this.print(sb.toString());
    }
    
    public void println(final char c) throws IOException {
        this.println(String.valueOf(c));
    }
    
    public void println(final int i) throws IOException {
        this.println(String.valueOf(i));
    }
    
    public void println(final long l) throws IOException {
        this.println(String.valueOf(l));
    }
    
    public void println(final float f) throws IOException {
        this.println(String.valueOf(f));
    }
    
    public void println(final double d) throws IOException {
        this.println(String.valueOf(d));
    }
    
    public abstract boolean isReady();
    
    public abstract void setWriteListener(final WriteListener p0);
    
    static {
        lStrings = ResourceBundle.getBundle("javax.servlet.LocalStrings");
    }
}

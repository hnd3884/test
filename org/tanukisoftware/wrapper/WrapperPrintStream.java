package org.tanukisoftware.wrapper;

import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;

final class WrapperPrintStream extends PrintStream
{
    private String m_header;
    
    WrapperPrintStream(final PrintStream parent, final String header) {
        super(parent);
        this.m_header = header;
    }
    
    WrapperPrintStream(final PrintStream parent, final boolean autoFlush, final String encoding, final String header) throws UnsupportedEncodingException {
        super(parent, autoFlush, encoding);
        this.m_header = header;
    }
    
    public void println() {
        super.println(this.m_header);
    }
    
    public void println(final String x) {
        if (x.indexOf("\n") >= 0) {
            final String[] lines = x.split("[\n]", -1);
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < lines.length; ++i) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(this.m_header);
                sb.append(lines[i]);
            }
            super.println(sb.toString());
        }
        else {
            super.println(this.m_header + x);
        }
    }
    
    public void println(final Object x) {
        if (x == null) {
            this.println("null");
        }
        else {
            this.println(x.toString());
        }
    }
}

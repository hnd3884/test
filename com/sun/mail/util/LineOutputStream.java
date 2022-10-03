package com.sun.mail.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class LineOutputStream extends FilterOutputStream
{
    private boolean allowutf8;
    private static byte[] newline;
    
    public LineOutputStream(final OutputStream out) {
        this(out, false);
    }
    
    public LineOutputStream(final OutputStream out, final boolean allowutf8) {
        super(out);
        this.allowutf8 = allowutf8;
    }
    
    public void writeln(final String s) throws IOException {
        byte[] bytes;
        if (this.allowutf8) {
            bytes = s.getBytes(StandardCharsets.UTF_8);
        }
        else {
            bytes = ASCIIUtility.getBytes(s);
        }
        this.out.write(bytes);
        this.out.write(LineOutputStream.newline);
    }
    
    public void writeln() throws IOException {
        this.out.write(LineOutputStream.newline);
    }
    
    static {
        (LineOutputStream.newline = new byte[2])[0] = 13;
        LineOutputStream.newline[1] = 10;
    }
}

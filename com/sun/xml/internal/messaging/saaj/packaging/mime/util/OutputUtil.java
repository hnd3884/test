package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract class OutputUtil
{
    private static byte[] newline;
    
    public static void writeln(final String s, final OutputStream out) throws IOException {
        writeAsAscii(s, out);
        writeln(out);
    }
    
    public static void writeAsAscii(final String s, final OutputStream out) throws IOException {
        for (int len = s.length(), i = 0; i < len; ++i) {
            out.write((byte)s.charAt(i));
        }
    }
    
    public static void writeln(final OutputStream out) throws IOException {
        out.write(OutputUtil.newline);
    }
    
    static {
        OutputUtil.newline = new byte[] { 13, 10 };
    }
}

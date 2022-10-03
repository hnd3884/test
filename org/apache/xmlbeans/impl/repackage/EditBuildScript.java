package org.apache.xmlbeans.impl.repackage;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.Writer;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;

public class EditBuildScript
{
    public static void main(final String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
        args[0] = args[0].replace('/', File.separatorChar);
        final File buildFile = new File(args[0]);
        final StringBuffer sb = readFile(buildFile);
        final String tokenStr = "<property name=\"" + args[1] + "\" value=\"";
        final int i = sb.indexOf(tokenStr);
        if (i < 0) {
            throw new IllegalArgumentException("Can't find token: " + tokenStr);
        }
        int j;
        for (j = i + tokenStr.length(); sb.charAt(j) != '\"'; ++j) {}
        sb.replace(i + tokenStr.length(), j, args[2]);
        writeFile(buildFile, sb);
    }
    
    static StringBuffer readFile(final File f) throws IOException {
        final InputStream in = new FileInputStream(f);
        final Reader r = new InputStreamReader(in);
        final StringWriter w = new StringWriter();
        copy(r, w);
        w.close();
        r.close();
        in.close();
        return w.getBuffer();
    }
    
    static void writeFile(final File f, final StringBuffer chars) throws IOException {
        final OutputStream out = new FileOutputStream(f);
        final Writer w = new OutputStreamWriter(out);
        final Reader r = new StringReader(chars.toString());
        copy(r, w);
        r.close();
        w.close();
        out.close();
    }
    
    static void copy(final Reader r, final Writer w) throws IOException {
        final char[] buffer = new char[16384];
        while (true) {
            final int n = r.read(buffer, 0, buffer.length);
            if (n < 0) {
                break;
            }
            w.write(buffer, 0, n);
        }
    }
}

package com.sshtools.publickey;

import java.util.Enumeration;
import java.io.ByteArrayOutputStream;
import com.maverick.util.Base64;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;

public abstract class Base64EncodedFileFormat
{
    protected String begin;
    protected String end;
    private Hashtable d;
    private int c;
    
    protected Base64EncodedFileFormat(final String begin, final String end) {
        this.d = new Hashtable();
        this.c = 70;
        this.begin = begin;
        this.end = end;
    }
    
    public static boolean isFormatted(final byte[] array, final String s, final String s2) {
        final String s3 = new String(array);
        return s3.indexOf(s) >= 0 && s3.indexOf(s2) > 0;
    }
    
    public void setHeaderValue(final String s, final String s2) {
        this.d.put(s, s2);
    }
    
    public String getHeaderValue(final String s) {
        return this.d.get(s);
    }
    
    protected byte[] getKeyBlob(final byte[] array) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(array)));
        final StringBuffer sb = new StringBuffer("");
        String line;
        do {
            line = bufferedReader.readLine();
            if (line == null) {
                throw new IOException("Incorrect file format!");
            }
        } while (!line.trim().endsWith(this.begin));
        String s;
        while (true) {
            final String line2 = bufferedReader.readLine();
            if (line2 == null) {
                throw new IOException("Incorrect file format!");
            }
            s = line2.trim();
            final int index = s.indexOf(": ");
            if (index <= 0) {
                break;
            }
            while (s.endsWith("\\")) {
                final String substring = s.substring(0, s.length() - 1);
                final String line3 = bufferedReader.readLine();
                if (line3 == null) {
                    throw new IOException("Incorrect file format!");
                }
                s = substring + line3.trim();
            }
            this.d.put(s.substring(0, index), s.substring(index + 2));
        }
        do {
            sb.append(s);
            final String line4 = bufferedReader.readLine();
            if (line4 == null) {
                throw new IOException("Invalid file format!");
            }
            s = line4.trim();
        } while (!s.endsWith(this.end));
        return Base64.decode(sb.toString());
    }
    
    protected byte[] formatKey(final byte[] array) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(this.begin.getBytes());
        byteArrayOutputStream.write(10);
        final Enumeration keys = this.d.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final String string = s + ": " + this.d.get(s);
            for (int i = 0; i < string.length(); i += this.c) {
                byteArrayOutputStream.write((string.substring(i, (i + this.c < string.length()) ? (i + this.c) : string.length()) + ((i + this.c < string.length()) ? "\\" : "")).getBytes());
                byteArrayOutputStream.write(10);
            }
        }
        byteArrayOutputStream.write(Base64.encodeBytes(array, false).getBytes());
        byteArrayOutputStream.write(10);
        byteArrayOutputStream.write(this.end.getBytes());
        byteArrayOutputStream.write(10);
        return byteArrayOutputStream.toByteArray();
    }
}

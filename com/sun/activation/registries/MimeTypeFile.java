package com.sun.activation.registries;

import java.util.StringTokenizer;
import java.io.StringReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Hashtable;

public class MimeTypeFile
{
    private String fname;
    private Hashtable type_hash;
    private static boolean DEBUG;
    
    static {
        MimeTypeFile.DEBUG = false;
    }
    
    public MimeTypeFile() {
        this.fname = null;
        this.type_hash = new Hashtable();
    }
    
    public MimeTypeFile(final InputStream inputStream) throws IOException {
        this.fname = null;
        this.type_hash = new Hashtable();
        this.parse(new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1")));
    }
    
    public MimeTypeFile(final String fname) throws IOException {
        this.fname = null;
        this.type_hash = new Hashtable();
        this.fname = fname;
        final FileReader fileReader = new FileReader(new File(this.fname));
        try {
            this.parse(new BufferedReader(fileReader));
        }
        finally {
            try {
                fileReader.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public void appendToRegistry(final String s) {
        try {
            this.parse(new BufferedReader(new StringReader(s)));
        }
        catch (final IOException ex) {}
    }
    
    public String getMIMETypeString(final String s) {
        final MimeTypeEntry mimeTypeEntry = this.getMimeTypeEntry(s);
        if (mimeTypeEntry != null) {
            return mimeTypeEntry.getMIMEType();
        }
        return null;
    }
    
    public MimeTypeEntry getMimeTypeEntry(final String s) {
        return this.type_hash.get(s);
    }
    
    public static void main(final String[] array) throws Exception {
        MimeTypeFile.DEBUG = true;
        System.out.println("ext " + array[1] + " type " + new MimeTypeFile(array[0]).getMIMETypeString(array[1]));
        System.exit(0);
    }
    
    private void parse(final BufferedReader bufferedReader) throws IOException {
        String substring = null;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String string;
            if (substring == null) {
                string = line;
            }
            else {
                string = String.valueOf(substring) + line;
            }
            final int length = string.length();
            if (string.length() > 0 && string.charAt(length - 1) == '\\') {
                substring = string.substring(0, length - 1);
            }
            else {
                this.parseEntry(string);
                substring = null;
            }
        }
        if (substring != null) {
            this.parseEntry(substring);
        }
    }
    
    private void parseEntry(String trim) {
        String s = null;
        trim = trim.trim();
        if (trim.length() == 0) {
            return;
        }
        if (trim.charAt(0) == '#') {
            return;
        }
        if (trim.indexOf(61) > 0) {
            final LineTokenizer lineTokenizer = new LineTokenizer(trim);
            while (lineTokenizer.hasMoreTokens()) {
                final String nextToken = lineTokenizer.nextToken();
                String nextToken2 = null;
                if (lineTokenizer.hasMoreTokens() && lineTokenizer.nextToken().equals("=") && lineTokenizer.hasMoreTokens()) {
                    nextToken2 = lineTokenizer.nextToken();
                }
                if (nextToken2 == null) {
                    System.err.println("Bad .mime.types entry: " + trim);
                    return;
                }
                if (nextToken.equals("type")) {
                    s = nextToken2;
                }
                else {
                    if (!nextToken.equals("exts")) {
                        continue;
                    }
                    final StringTokenizer stringTokenizer = new StringTokenizer(nextToken2, ",");
                    while (stringTokenizer.hasMoreTokens()) {
                        final String nextToken3 = stringTokenizer.nextToken();
                        final MimeTypeEntry mimeTypeEntry = new MimeTypeEntry(s, nextToken3);
                        this.type_hash.put(nextToken3, mimeTypeEntry);
                        if (MimeTypeFile.DEBUG) {
                            System.out.println("Added: " + mimeTypeEntry.toString());
                        }
                    }
                }
            }
        }
        else {
            final StringTokenizer stringTokenizer2 = new StringTokenizer(trim);
            if (stringTokenizer2.countTokens() == 0) {
                return;
            }
            final String nextToken4 = stringTokenizer2.nextToken();
            while (stringTokenizer2.hasMoreTokens()) {
                final String nextToken5 = stringTokenizer2.nextToken();
                final MimeTypeEntry mimeTypeEntry2 = new MimeTypeEntry(nextToken4, nextToken5);
                this.type_hash.put(nextToken5, mimeTypeEntry2);
                if (MimeTypeFile.DEBUG) {
                    System.out.println("Added: " + mimeTypeEntry2.toString());
                }
            }
        }
    }
}

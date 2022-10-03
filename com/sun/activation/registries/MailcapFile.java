package com.sun.activation.registries;

import java.util.Enumeration;
import java.util.Vector;
import java.io.StringReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Hashtable;

public class MailcapFile
{
    private Hashtable type_hash;
    private static boolean debug;
    
    static {
        MailcapFile.debug = false;
        try {
            MailcapFile.debug = Boolean.getBoolean("javax.activation.debug");
        }
        catch (final Throwable t) {}
    }
    
    public MailcapFile() {
        this.type_hash = null;
        if (MailcapFile.debug) {
            System.out.println("new MailcapFile: default");
        }
        this.type_hash = new Hashtable();
    }
    
    public MailcapFile(final InputStream inputStream) throws IOException {
        this.type_hash = null;
        if (MailcapFile.debug) {
            System.out.println("new MailcapFile: InputStream");
        }
        this.type_hash = this.createMailcapHash(new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1")));
    }
    
    public MailcapFile(final String s) throws IOException {
        this.type_hash = null;
        if (MailcapFile.debug) {
            System.out.println("new MailcapFile: file " + s);
        }
        Reader reader = null;
        try {
            reader = new FileReader(s);
            this.type_hash = this.createMailcapHash(new BufferedReader(reader));
        }
        finally {
            if (reader != null) {
                try {
                    ((InputStreamReader)reader).close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    public void appendToMailcap(final String s) {
        if (MailcapFile.debug) {
            System.out.println("appendToMailcap: " + s);
        }
        try {
            this.parse(new StringReader(s), this.type_hash);
        }
        catch (final IOException ex) {}
    }
    
    private Hashtable createMailcapHash(final Reader reader) throws IOException {
        final Hashtable hashtable = new Hashtable();
        this.parse(reader, hashtable);
        return hashtable;
    }
    
    public Hashtable getMailcapList(final String s) {
        Hashtable mergeResults = this.type_hash.get(s);
        final Hashtable hashtable = this.type_hash.get(String.valueOf(s.substring(0, s.indexOf(47) + 1)) + "*");
        if (hashtable != null) {
            if (mergeResults != null) {
                mergeResults = this.mergeResults(mergeResults, hashtable);
            }
            else {
                mergeResults = hashtable;
            }
        }
        return mergeResults;
    }
    
    private Hashtable mergeResults(final Hashtable hashtable, final Hashtable hashtable2) {
        final Enumeration keys = hashtable2.keys();
        final Hashtable hashtable3 = (Hashtable)hashtable.clone();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final Vector vector = hashtable3.get(s);
            if (vector == null) {
                hashtable3.put(s, hashtable2.get(s));
            }
            else {
                final Enumeration elements = hashtable2.get(s).elements();
                final Vector vector2 = (Vector)vector.clone();
                hashtable3.put(s, vector2);
                while (elements.hasMoreElements()) {
                    vector2.addElement(elements.nextElement());
                }
            }
        }
        return hashtable3;
    }
    
    private void parse(final Reader reader, final Hashtable hashtable) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        String s = null;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            final String trim = line.trim();
            try {
                if (trim.charAt(0) == '#') {
                    continue;
                }
                if (trim.charAt(trim.length() - 1) == '\\') {
                    if (s != null) {
                        s = String.valueOf(s) + trim.substring(0, trim.length() - 1);
                    }
                    else {
                        s = trim.substring(0, trim.length() - 1);
                    }
                }
                else if (s != null) {
                    s = String.valueOf(s) + trim;
                    try {
                        parseLine(s, hashtable);
                    }
                    catch (final MailcapParseException ex) {}
                    s = null;
                }
                else {
                    try {
                        parseLine(trim, hashtable);
                    }
                    catch (final MailcapParseException ex2) {}
                }
            }
            catch (final StringIndexOutOfBoundsException ex3) {}
        }
    }
    
    protected static void parseLine(final String s, final Hashtable hashtable) throws MailcapParseException, IOException {
        final MailcapTokenizer mailcapTokenizer = new MailcapTokenizer(s);
        mailcapTokenizer.setIsAutoquoting(false);
        String lowerCase = "*";
        final String s2 = "";
        if (MailcapFile.debug) {
            System.out.println("parse: " + s);
        }
        final int nextToken = mailcapTokenizer.nextToken();
        final String concat = s2.concat(mailcapTokenizer.getCurrentTokenValue());
        if (nextToken != 2) {
            reportParseError(2, nextToken, mailcapTokenizer.getCurrentTokenValue());
        }
        final String lowerCase2 = mailcapTokenizer.getCurrentTokenValue().toLowerCase();
        int n = mailcapTokenizer.nextToken();
        String s3 = concat.concat(mailcapTokenizer.getCurrentTokenValue());
        if (n != 47 && n != 59) {
            reportParseError(47, 59, n, mailcapTokenizer.getCurrentTokenValue());
        }
        if (n == 47) {
            final int nextToken2 = mailcapTokenizer.nextToken();
            final String concat2 = s3.concat(mailcapTokenizer.getCurrentTokenValue());
            if (nextToken2 != 2) {
                reportParseError(2, nextToken2, mailcapTokenizer.getCurrentTokenValue());
            }
            lowerCase = mailcapTokenizer.getCurrentTokenValue().toLowerCase();
            n = mailcapTokenizer.nextToken();
            s3 = concat2.concat(mailcapTokenizer.getCurrentTokenValue());
        }
        if (MailcapFile.debug) {
            System.out.println("  Type: " + lowerCase2 + "/" + lowerCase);
        }
        Hashtable hashtable2 = hashtable.get(String.valueOf(lowerCase2) + "/" + lowerCase);
        if (hashtable2 == null) {
            hashtable2 = new Hashtable();
            hashtable.put(String.valueOf(lowerCase2) + "/" + lowerCase, hashtable2);
        }
        if (n != 59) {
            reportParseError(59, n, mailcapTokenizer.getCurrentTokenValue());
        }
        mailcapTokenizer.setIsAutoquoting(true);
        int n2 = mailcapTokenizer.nextToken();
        mailcapTokenizer.setIsAutoquoting(false);
        s3.concat(mailcapTokenizer.getCurrentTokenValue());
        if (n2 != 2 && n2 != 59) {
            reportParseError(2, 59, n2, mailcapTokenizer.getCurrentTokenValue());
        }
        if (n2 != 59) {
            n2 = mailcapTokenizer.nextToken();
        }
        if (n2 == 59) {
            int i;
            do {
                final int nextToken3 = mailcapTokenizer.nextToken();
                if (nextToken3 != 2) {
                    reportParseError(2, nextToken3, mailcapTokenizer.getCurrentTokenValue());
                }
                final String lowerCase3 = mailcapTokenizer.getCurrentTokenValue().toLowerCase();
                i = mailcapTokenizer.nextToken();
                if (i != 61 && i != 59 && i != 5) {
                    reportParseError(61, 59, 5, i, mailcapTokenizer.getCurrentTokenValue());
                }
                if (i == 61) {
                    mailcapTokenizer.setIsAutoquoting(true);
                    final int nextToken4 = mailcapTokenizer.nextToken();
                    mailcapTokenizer.setIsAutoquoting(false);
                    if (nextToken4 != 2) {
                        reportParseError(2, nextToken4, mailcapTokenizer.getCurrentTokenValue());
                    }
                    final String currentTokenValue = mailcapTokenizer.getCurrentTokenValue();
                    if (lowerCase3.startsWith("x-java-")) {
                        final String substring = lowerCase3.substring(7);
                        if (MailcapFile.debug) {
                            System.out.println("    Command: " + substring + ", Class: " + currentTokenValue);
                        }
                        Vector<?> vector = (Vector<?>)hashtable2.get(substring);
                        if (vector == null) {
                            vector = new Vector<Object>();
                            hashtable2.put(substring, vector);
                        }
                        vector.insertElementAt(currentTokenValue, 0);
                    }
                    i = mailcapTokenizer.nextToken();
                }
            } while (i == 59);
        }
        else if (n2 != 5) {
            reportParseError(5, 59, n2, mailcapTokenizer.getCurrentTokenValue());
        }
    }
    
    protected static void reportParseError(final int n, final int n2, final int n3, final int n4, final String s) throws MailcapParseException {
        if (MailcapFile.debug) {
            System.out.println("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(n4) + " token (" + s + ") while expecting a " + MailcapTokenizer.nameForToken(n) + ", a " + MailcapTokenizer.nameForToken(n2) + ", or a " + MailcapTokenizer.nameForToken(n3) + " token.");
        }
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(n4) + " token (" + s + ") while expecting a " + MailcapTokenizer.nameForToken(n) + ", a " + MailcapTokenizer.nameForToken(n2) + ", or a " + MailcapTokenizer.nameForToken(n3) + " token.");
    }
    
    protected static void reportParseError(final int n, final int n2, final int n3, final String s) throws MailcapParseException {
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(n3) + " token (" + s + ") while expecting a " + MailcapTokenizer.nameForToken(n) + " or a " + MailcapTokenizer.nameForToken(n2) + " token.");
    }
    
    protected static void reportParseError(final int n, final int n2, final String s) throws MailcapParseException {
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(n2) + " token (" + s + ") while expecting a " + MailcapTokenizer.nameForToken(n) + " token.");
    }
}

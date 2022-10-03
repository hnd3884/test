package com.theorem.radius3.dictionary;

import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.Arrays;
import java.io.File;
import java.util.Stack;
import java.util.ArrayList;

public final class FreeRadius extends RADIUSDictionary
{
    public static final int ZERO = 1;
    public static final int ZEROPLUS = 2;
    public static final int ZEROONE = 3;
    public static final int ONE = 4;
    public static final int UNKNOWN = 5;
    private String a;
    ArrayList b;
    private boolean c;
    private String d;
    private int e;
    private Stack f;
    int[][] g;
    
    public FreeRadius(final String s) {
        super(s);
        this.c = true;
        this.d = "";
        this.e = 0;
        this.g = new int[][] { { 1, 3, 3, 1, 1, 5, 1 }, { 2, 3, 1, 1, 1, 5, 1 }, { 3, 3, 1, 1, 1, 5, 1 }, { 4, 3, 1, 1, 1, 3, 1 }, { 5, 3, 1, 1, 1, 3, 1 }, { 6, 3, 3, 1, 1, 3, 1 }, { 7, 3, 3, 1, 1, 3, 1 }, { 8, 3, 3, 1, 1, 3, 1 }, { 9, 3, 3, 1, 1, 3, 1 }, { 10, 1, 3, 1, 1, 3, 1 }, { 11, 1, 2, 1, 1, 2, 1 }, { 12, 3, 3, 1, 1, 3, 1 }, { 13, 2, 2, 1, 1, 2, 1 }, { 14, 2, 2, 1, 1, 2, 1 }, { 15, 1, 3, 1, 1, 3, 1 }, { 16, 1, 3, 1, 1, 3, 1 }, { 18, 1, 2, 2, 2, 1, 1 }, { 19, 3, 3, 1, 1, 3, 1 }, { 20, 1, 3, 1, 1, 3, 1 }, { 22, 1, 2, 1, 1, 2, 1 }, { 23, 1, 3, 1, 1, 3, 1 }, { 24, 3, 3, 1, 3, 1, 1 }, { 25, 1, 2, 1, 1, 2, 1 }, { 26, 2, 2, 1, 2, 2, 2 }, { 27, 1, 3, 1, 3, 3, 1 }, { 28, 1, 3, 1, 3, 3, 1 }, { 29, 1, 3, 1, 1, 3, 1 }, { 30, 3, 1, 1, 1, 3, 1 }, { 31, 3, 1, 1, 1, 3, 1 }, { 32, 3, 1, 1, 1, 3, 1 }, { 33, 2, 2, 2, 2, 2, 2 }, { 34, 3, 3, 1, 1, 3, 1 }, { 35, 1, 1, 1, 1, 3, 1 }, { 36, 3, 3, 1, 1, 3, 1 }, { 37, 1, 3, 1, 1, 3, 1 }, { 38, 1, 2, 1, 1, 3, 1 }, { 39, 1, 3, 1, 1, 3, 1 }, { 40, 1, 1, 1, 1, 4, 1 }, { 41, 1, 1, 1, 1, 3, 1 }, { 42, 1, 1, 1, 1, 3, 1 }, { 43, 1, 1, 1, 1, 3, 1 }, { 44, 1, 1, 1, 1, 4, 1 }, { 45, 1, 1, 1, 1, 3, 1 }, { 46, 1, 1, 1, 1, 3, 1 }, { 47, 1, 1, 1, 1, 3, 1 }, { 48, 1, 1, 1, 1, 3, 1 }, { 49, 1, 1, 1, 1, 3, 1 }, { 50, 1, 1, 1, 1, 2, 1 }, { 51, 1, 1, 1, 1, 2, 1 }, { 52, 1, 1, 1, 1, 3, 1 }, { 53, 1, 1, 1, 1, 3, 1 }, { 55, 1, 1, 1, 1, 3, 1 }, { 60, 3, 1, 1, 1, 1, 1 }, { 61, 3, 1, 1, 1, 3, 1 }, { 62, 3, 3, 1, 1, 3, 1 }, { 63, 3, 3, 1, 1, 3, 1 }, { 64, 2, 2, 1, 1, 3, 1 }, { 65, 2, 2, 1, 1, 3, 1 }, { 66, 2, 2, 1, 1, 3, 1 }, { 67, 2, 2, 1, 1, 3, 1 }, { 68, 1, 1, 1, 1, 3, 1 }, { 69, 1, 2, 1, 1, 1, 1 }, { 70, 3, 1, 1, 1, 5, 1 }, { 71, 1, 3, 1, 3, 5, 1 }, { 72, 1, 3, 1, 1, 5, 1 }, { 73, 3, 1, 1, 3, 5, 1 }, { 74, 2, 1, 1, 2, 5, 1 }, { 75, 1, 1, 3, 1, 5, 1 }, { 76, 1, 1, 1, 3, 5, 1 }, { 77, 3, 1, 1, 1, 2, 1 }, { 78, 1, 2, 1, 1, 5, 1 }, { 79, 2, 2, 2, 2, 5, 1 }, { 80, 3, 3, 3, 3, 5, 1 }, { 81, 2, 2, 1, 1, 3, 1 }, { 82, 1, 2, 1, 1, 3, 1 }, { 83, 2, 2, 1, 1, 1, 1 }, { 84, 1, 3, 1, 3, 5, 1 }, { 85, 1, 3, 1, 1, 5, 1 }, { 86, 1, 1, 1, 1, 3, 1 }, { 87, 3, 1, 1, 1, 3, 1 }, { 88, 1, 3, 1, 1, 5, 1 }, { 90, 2, 2, 1, 1, 3, 1 }, { 91, 2, 2, 1, 1, 3, 1 }, { 95, 3, 1, 1, 1, 3, 1 }, { 96, 3, 3, 1, 1, 3, 1 }, { 97, 2, 2, 1, 1, 2, 1 }, { 98, 2, 2, 1, 1, 2, 1 }, { 99, 1, 2, 1, 1, 2, 1 }, { 100, 1, 3, 1, 1, 3, 1 } };
        this.a = new File(s).getParent();
    }
    
    public final int getOccurrence(final int n, final int n2) {
        if (n != 1 && n != 2 && n != 3 && n != 11 && n != 4 && n != 5) {
            return 5;
        }
        final int binarySearch = Arrays.binarySearch(this.g, new int[] { n2 }, new CompOccur());
        if (binarySearch < 0) {
            return 5;
        }
        switch (n) {
            case 2: {
                return this.g[binarySearch][2];
            }
            default: {
                return 5;
            }
            case 4: {
                return this.g[binarySearch][5];
            }
            case 11: {
                return this.g[binarySearch][4];
            }
            case 3: {
                return this.g[binarySearch][3];
            }
            case 1: {
                return this.g[binarySearch][1];
            }
            case 5: {
                return this.g[binarySearch][6];
            }
        }
    }
    
    public final String getOccurrenceName(final int n) {
        String s = "ILLEGAL VALUE";
        switch (n) {
            case 3: {
                s = "ZEROONE";
                break;
            }
            case 5: {
                s = "UNKNOWN";
                break;
            }
            case 2: {
                s = "ZEROPLUS";
                break;
            }
            case 4: {
                s = "ONE";
                break;
            }
            case 1: {
                s = "ZERO";
                break;
            }
        }
        return s;
    }
    
    public final void enableIncludes(final boolean c) {
        this.c = c;
    }
    
    public final synchronized void read() throws IOException {
        final LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(super.a));
        this.f();
        this.b.add(super.a);
        this.a(super.a, lineNumberReader);
        this.c(super.a);
        this.a(lineNumberReader);
        lineNumberReader.close();
        this.d(super.a);
        this.a();
        this.c();
    }
    
    public final String[] getFileList() {
        return this.b.toArray(new String[this.b.size()]);
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("List of Vendors:\n");
        final String[] vendorNames = this.getVendorNames();
        for (int i = 0; i < vendorNames.length; ++i) {
            sb.append('\t').append(vendorNames[i]).append(" [").append(this.getVendorId(vendorNames[i])).append("]\n");
        }
        sb.append("\nFiles read:\n");
        final Iterator iterator = this.b.iterator();
        while (iterator.hasNext()) {
            sb.append("\t").append((String)iterator.next()).append('\n');
        }
        return sb.toString();
    }
    
    protected final void a(final LineNumberReader lineNumberReader) throws IOException {
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            final String b = this.b(line);
            if (b.length() == 0) {
                continue;
            }
            if (b.startsWith("VENDOR")) {
                this.d();
                final StringTokenizer stringTokenizer = new StringTokenizer(b);
                final int countTokens = stringTokenizer.countTokens();
                if (countTokens != 3 && countTokens != 4) {
                    throw new IOException(this.b() + " '" + b + "' has the wrong number of  tokens (3 or 4 required)");
                }
                stringTokenizer.nextToken();
                this.d = stringTokenizer.nextToken();
                this.e = this.e(stringTokenizer.nextToken());
                this.addVendor(this.d, this.e);
                if (countTokens != 4) {
                    continue;
                }
                try {
                    final String nextToken = stringTokenizer.nextToken();
                    if (!nextToken.startsWith("format")) {
                        continue;
                    }
                    final String[] split = nextToken.split("=");
                    if (split.length != 2) {
                        continue;
                    }
                    this.setVendorTagLength(this.e, split[1].split(",")[0].equals("2"));
                    continue;
                }
                catch (final Exception ex) {
                    throw new IOException(this.b() + " '" + b + "' Vendor 'format' token is unreadable");
                }
            }
            if (b.startsWith("ATTRIB_NMC")) {
                this.d();
                this.addVendor("USR", 429);
                final StringTokenizer stringTokenizer2 = new StringTokenizer(b);
                if (stringTokenizer2.countTokens() != 4) {
                    throw new IOException(this.b() + " '" + b + "' has insufficient tokens (4 required)");
                }
                stringTokenizer2.nextToken();
                final String nextToken2 = stringTokenizer2.nextToken();
                final String nextToken3 = stringTokenizer2.nextToken();
                final String nextToken4 = stringTokenizer2.nextToken();
                try {
                    super.addAttribute(nextToken2, nextToken3, nextToken4, "USR", false);
                }
                catch (final IOException ex2) {
                    throw new IOException(this.b() + " '" + b + "' " + ex2.getMessage());
                }
                this.e();
            }
            else if (b.startsWith("ATTRIBUTE")) {
                final StringTokenizer stringTokenizer3 = new StringTokenizer(b);
                final int countTokens2 = stringTokenizer3.countTokens();
                if (countTokens2 != 4 && countTokens2 != 5) {
                    throw new IOException(this.b() + " '" + b + "' has insufficient tokens (4 required, the vendor name is optional)");
                }
                boolean b2 = false;
                String nextToken5;
                String nextToken6;
                String nextToken7;
                boolean b3;
                int n;
                try {
                    stringTokenizer3.nextToken();
                    nextToken5 = stringTokenizer3.nextToken();
                    nextToken6 = stringTokenizer3.nextToken();
                    nextToken7 = stringTokenizer3.nextToken();
                    String s = "";
                    if (countTokens2 >= 5) {
                        final String nextToken8 = stringTokenizer3.nextToken();
                        if (this.getVendorId(nextToken8) != -1) {
                            this.d();
                            this.d = nextToken8;
                            this.e = this.getVendorId(this.d);
                            b2 = true;
                            s = "";
                        }
                        else {
                            s = nextToken8;
                        }
                    }
                    final int[] a = this.a(s);
                    b3 = (a[0] != 0);
                    n = a[1];
                }
                catch (final Exception ex3) {
                    ex3.printStackTrace();
                    String message = ex3.getMessage();
                    if (message == null) {
                        message = "Parse error";
                    }
                    throw new IOException(this.b() + " '" + b + "' " + message);
                }
                try {
                    super.addAttribute(nextToken5, nextToken6, nextToken7, this.d, b3);
                    if (n > 0) {
                        super.setEncryptionType(super.getTag(nextToken5), n);
                    }
                }
                catch (final IOException ex4) {
                    throw new IOException(this.b() + " '" + b + "' " + ex4.getMessage());
                }
                if (!b2) {
                    continue;
                }
                this.e();
            }
            else if (b.toUpperCase().startsWith("VALUE")) {
                final StringTokenizer stringTokenizer4 = new StringTokenizer(b);
                if (stringTokenizer4.countTokens() != 4) {
                    throw new IOException(this.b() + " '" + b + "' has insufficient tokens (4 required)");
                }
                stringTokenizer4.nextToken();
                final String nextToken9 = stringTokenizer4.nextToken();
                final String nextToken10 = stringTokenizer4.nextToken();
                final String nextToken11 = stringTokenizer4.nextToken();
                Integer n2;
                try {
                    n2 = new Integer((int)Long.parseLong(nextToken11));
                }
                catch (final NumberFormatException ex5) {
                    try {
                        n2 = new Integer((int)Long.parseLong(nextToken11.substring(2), 16));
                    }
                    catch (final NumberFormatException ex6) {
                        throw new IOException(this.b() + " Expecting a number, found '" + nextToken11 + "'");
                    }
                }
                super.setSymbolicIntValue(this.e, nextToken9, nextToken10, n2);
            }
            else if (b.toUpperCase().startsWith("BEGIN-VENDOR")) {
                final StringTokenizer stringTokenizer5 = new StringTokenizer(b);
                if (stringTokenizer5.countTokens() != 2) {
                    throw new IOException(this.b() + " '" + b + "' has insufficient tokens (2 required)");
                }
                this.d();
                stringTokenizer5.nextToken();
                this.d = stringTokenizer5.nextToken();
                this.e = this.getVendorId(this.d);
            }
            else if (b.toUpperCase().startsWith("END-VENDOR")) {
                final StringTokenizer stringTokenizer6 = new StringTokenizer(b);
                if (stringTokenizer6.countTokens() != 2) {
                    throw new IOException(this.b() + " '" + b + "' has insufficient tokens (2 required)");
                }
                stringTokenizer6.nextToken();
                final String nextToken12 = stringTokenizer6.nextToken();
                if (!nextToken12.equals(this.d)) {
                    throw new IOException(this.b() + " END-VENDOR of '" + nextToken12 + "' doesn't match the BEGIN-VENDOR of '" + this.d + "'");
                }
                this.e();
            }
            else {
                if (!b.toUpperCase().startsWith("$INCLUDE")) {
                    throw new IOException(this.b() + " Unknown keyword in line '" + b + "'");
                }
                if (!this.c) {
                    continue;
                }
                final StringTokenizer stringTokenizer7 = new StringTokenizer(b);
                if (stringTokenizer7.countTokens() != 2) {
                    throw new IOException(lineNumberReader.getLineNumber() + " '" + b + "' has insufficient tokens (2 required)");
                }
                stringTokenizer7.nextToken();
                final String nextToken13 = stringTokenizer7.nextToken();
                this.b.add(nextToken13);
                final String s2 = (this.a != null) ? (this.a + File.separator + nextToken13) : nextToken13;
                LineNumberReader lineNumberReader2;
                try {
                    lineNumberReader2 = new LineNumberReader(new FileReader(s2));
                }
                catch (final FileNotFoundException ex7) {
                    lineNumberReader2 = new LineNumberReader(new FileReader(nextToken13));
                }
                this.a(nextToken13, lineNumberReader2);
                this.c(super.a);
                this.a(lineNumberReader2);
                lineNumberReader2.close();
                this.d(super.a);
                this.a();
            }
        }
    }
    
    private final int[] a(final String s) {
        final int[] array = new int[2];
        final String[] split = s.trim().split(",");
        for (int i = 0; i < split.length; ++i) {
            final String s2 = split[i];
            if (s2.startsWith("#")) {
                return array;
            }
            if (s2.startsWith("encrypt")) {
                final String[] split2 = s2.split("=");
                if (split2.length == 1) {
                    array[1] = 1;
                }
                else if (split2[1].equals("1")) {
                    array[1] = 64;
                }
                else if (split2[1].equals("2")) {
                    array[1] = 128;
                }
            }
            else if (s2.startsWith("has_tag")) {
                array[0] = 1;
            }
        }
        return array;
    }
    
    private final String b(String s) {
        s = s.trim();
        final int index = s.indexOf("#");
        if (index < 0) {
            return s;
        }
        s = s.substring(0, index);
        s = s.trim();
        return s;
    }
    
    private final void c(final String s) {
        this.d();
        this.f.push(s);
    }
    
    private final void d(final String s) {
        while (!this.f.pop().equals(s)) {}
        this.e();
    }
    
    private final void d() {
        this.f.push(this.d);
    }
    
    private final void e() {
        this.d = this.f.pop();
        this.e = this.getVendorId(this.d);
    }
    
    private final int e(final String s) throws IOException {
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            throw new IOException(this.b() + " '" + s + "' Expecting a number.");
        }
    }
    
    private final void f() {
        this.d = "";
        this.e = 0;
        this.f = new Stack();
        this.b = new ArrayList();
    }
    
    final void c() {
        this.d = null;
        this.f = null;
    }
    
    private static class CompOccur implements Comparator
    {
        public final int compare(final Object o, final Object o2) {
            return ((int[])o)[0] - ((int[])o2)[0];
        }
    }
}

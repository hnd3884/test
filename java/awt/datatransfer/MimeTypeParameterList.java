package java.awt.datatransfer;

import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;
import java.util.Hashtable;

class MimeTypeParameterList implements Cloneable
{
    private Hashtable<String, String> parameters;
    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
    
    public MimeTypeParameterList() {
        this.parameters = new Hashtable<String, String>();
    }
    
    public MimeTypeParameterList(final String s) throws MimeTypeParseException {
        this.parameters = new Hashtable<String, String>();
        this.parse(s);
    }
    
    @Override
    public int hashCode() {
        int n = 47721858;
        final Enumeration<String> names = this.getNames();
        while (names.hasMoreElements()) {
            final String s = names.nextElement();
            n = n + s.hashCode() + this.get(s).hashCode();
        }
        return n;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MimeTypeParameterList)) {
            return false;
        }
        final MimeTypeParameterList list = (MimeTypeParameterList)o;
        if (this.size() != list.size()) {
            return false;
        }
        for (final Map.Entry entry : this.parameters.entrySet()) {
            final String s = (String)entry.getKey();
            final String s2 = (String)entry.getValue();
            final String s3 = list.parameters.get(s);
            if (s2 == null || s3 == null) {
                if (s2 != s3) {
                    return false;
                }
                continue;
            }
            else {
                if (!s2.equals(s3)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    protected void parse(final String s) throws MimeTypeParseException {
        final int length = s.length();
        if (length > 0) {
            int n = skipWhiteSpace(s, 0);
            if (n < length) {
                char c = s.charAt(n);
                while (n < length && c == ';') {
                    ++n;
                    int skipWhiteSpace = skipWhiteSpace(s, n);
                    if (skipWhiteSpace >= length) {
                        throw new MimeTypeParseException("Couldn't find parameter name");
                    }
                    final int n2 = skipWhiteSpace;
                    for (char c2 = s.charAt(skipWhiteSpace); skipWhiteSpace < length && isTokenChar(c2); ++skipWhiteSpace, c2 = s.charAt(skipWhiteSpace)) {}
                    final String lowerCase = s.substring(n2, skipWhiteSpace).toLowerCase();
                    int skipWhiteSpace2 = skipWhiteSpace(s, skipWhiteSpace);
                    if (skipWhiteSpace2 >= length || s.charAt(skipWhiteSpace2) != '=') {
                        throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
                    }
                    ++skipWhiteSpace2;
                    int skipWhiteSpace3 = skipWhiteSpace(s, skipWhiteSpace2);
                    if (skipWhiteSpace3 >= length) {
                        throw new MimeTypeParseException("Couldn't find a value for parameter named " + lowerCase);
                    }
                    c = s.charAt(skipWhiteSpace3);
                    String s2;
                    if (c == '\"') {
                        final int n3 = ++skipWhiteSpace3;
                        if (skipWhiteSpace3 >= length) {
                            throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                        }
                        int n4 = 0;
                        while (skipWhiteSpace3 < length && n4 == 0) {
                            c = s.charAt(skipWhiteSpace3);
                            if (c == '\\') {
                                skipWhiteSpace3 += 2;
                            }
                            else if (c == '\"') {
                                n4 = 1;
                            }
                            else {
                                ++skipWhiteSpace3;
                            }
                        }
                        if (c != '\"') {
                            throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                        }
                        s2 = unquote(s.substring(n3, skipWhiteSpace3));
                        ++skipWhiteSpace3;
                    }
                    else {
                        if (!isTokenChar(c)) {
                            throw new MimeTypeParseException("Unexpected character encountered at index " + skipWhiteSpace3);
                        }
                        final int n5 = skipWhiteSpace3;
                        int n6 = 0;
                        while (skipWhiteSpace3 < length && n6 == 0) {
                            c = s.charAt(skipWhiteSpace3);
                            if (isTokenChar(c)) {
                                ++skipWhiteSpace3;
                            }
                            else {
                                n6 = 1;
                            }
                        }
                        s2 = s.substring(n5, skipWhiteSpace3);
                    }
                    this.parameters.put(lowerCase, s2);
                    n = skipWhiteSpace(s, skipWhiteSpace3);
                    if (n >= length) {
                        continue;
                    }
                    c = s.charAt(n);
                }
                if (n < length) {
                    throw new MimeTypeParseException("More characters encountered in input than expected.");
                }
            }
        }
    }
    
    public int size() {
        return this.parameters.size();
    }
    
    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }
    
    public String get(final String s) {
        return this.parameters.get(s.trim().toLowerCase());
    }
    
    public void set(final String s, final String s2) {
        this.parameters.put(s.trim().toLowerCase(), s2);
    }
    
    public void remove(final String s) {
        this.parameters.remove(s.trim().toLowerCase());
    }
    
    public Enumeration<String> getNames() {
        return this.parameters.keys();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.parameters.size() * 16);
        final Enumeration<String> keys = this.parameters.keys();
        while (keys.hasMoreElements()) {
            sb.append("; ");
            final String s = keys.nextElement();
            sb.append(s);
            sb.append('=');
            sb.append(quote(this.parameters.get(s)));
        }
        return sb.toString();
    }
    
    public Object clone() {
        MimeTypeParameterList list = null;
        try {
            list = (MimeTypeParameterList)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        list.parameters = (Hashtable)this.parameters.clone();
        return list;
    }
    
    private static boolean isTokenChar(final char c) {
        return c > ' ' && c < '\u007f' && "()<>@,;:\\\"/[]?=".indexOf(c) < 0;
    }
    
    private static int skipWhiteSpace(final String s, int n) {
        final int length = s.length();
        if (n < length) {
            for (char c = s.charAt(n); n < length && Character.isWhitespace(c); ++n, c = s.charAt(n)) {}
        }
        return n;
    }
    
    private static String quote(final String s) {
        boolean b = false;
        final int length = s.length();
        for (int n = 0; n < length && !b; b = !isTokenChar(s.charAt(n)), ++n) {}
        if (b) {
            final StringBuilder sb = new StringBuilder((int)(length * 1.5));
            sb.append('\"');
            for (int i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                if (char1 == '\\' || char1 == '\"') {
                    sb.append('\\');
                }
                sb.append(char1);
            }
            sb.append('\"');
            return sb.toString();
        }
        return s;
    }
    
    private static String unquote(final String s) {
        final int length = s.length();
        final StringBuilder sb = new StringBuilder(length);
        int n = 0;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (n == 0 && char1 != '\\') {
                sb.append(char1);
            }
            else if (n != 0) {
                sb.append(char1);
                n = 0;
            }
            else {
                n = 1;
            }
        }
        return sb.toString();
    }
}

package sun.net.www;

import java.util.Iterator;

public class HeaderParser
{
    String raw;
    String[][] tab;
    int nkeys;
    int asize;
    
    public HeaderParser(final String raw) {
        this.asize = 10;
        this.raw = raw;
        this.tab = new String[this.asize][2];
        this.parse();
    }
    
    private HeaderParser() {
        this.asize = 10;
    }
    
    public HeaderParser subsequence(final int n, final int n2) {
        if (n == 0 && n2 == this.nkeys) {
            return this;
        }
        if (n < 0 || n >= n2 || n2 > this.nkeys) {
            throw new IllegalArgumentException("invalid start or end");
        }
        final HeaderParser headerParser = new HeaderParser();
        headerParser.tab = new String[this.asize][2];
        headerParser.asize = this.asize;
        System.arraycopy(this.tab, n, headerParser.tab, 0, n2 - n);
        headerParser.nkeys = n2 - n;
        return headerParser;
    }
    
    private void parse() {
        if (this.raw != null) {
            this.raw = this.raw.trim();
            final char[] charArray = this.raw.toCharArray();
            int n = 0;
            int i = 0;
            int nkeys = 0;
            int n2 = 1;
            int n3 = 0;
            final int length = charArray.length;
            while (i < length) {
                final char c = charArray[i];
                if (c == '=' && n3 == 0) {
                    this.tab[nkeys][0] = new String(charArray, n, i - n).toLowerCase();
                    n2 = 0;
                    n = ++i;
                }
                else if (c == '\"') {
                    if (n3 != 0) {
                        this.tab[nkeys++][1] = new String(charArray, n, i - n);
                        n3 = 0;
                        while (++i < length && (charArray[i] == ' ' || charArray[i] == ',')) {}
                        n2 = 1;
                        n = i;
                    }
                    else {
                        n3 = 1;
                        n = ++i;
                    }
                }
                else if (c == ' ' || c == ',') {
                    if (n3 != 0) {
                        ++i;
                        continue;
                    }
                    if (n2 != 0) {
                        this.tab[nkeys++][0] = new String(charArray, n, i - n).toLowerCase();
                    }
                    else {
                        this.tab[nkeys++][1] = new String(charArray, n, i - n);
                    }
                    while (i < length && (charArray[i] == ' ' || charArray[i] == ',')) {
                        ++i;
                    }
                    n2 = 1;
                    n = i;
                }
                else {
                    ++i;
                }
                if (nkeys == this.asize) {
                    this.asize *= 2;
                    final String[][] tab = new String[this.asize][2];
                    System.arraycopy(this.tab, 0, tab, 0, this.tab.length);
                    this.tab = tab;
                }
            }
            if (--i > n) {
                if (n2 == 0) {
                    if (charArray[i] == '\"') {
                        this.tab[nkeys++][1] = new String(charArray, n, i - n);
                    }
                    else {
                        this.tab[nkeys++][1] = new String(charArray, n, i - n + 1);
                    }
                }
                else {
                    this.tab[nkeys++][0] = new String(charArray, n, i - n + 1).toLowerCase();
                }
            }
            else if (i == n) {
                if (n2 == 0) {
                    if (charArray[i] == '\"') {
                        this.tab[nkeys++][1] = String.valueOf(charArray[i - 1]);
                    }
                    else {
                        this.tab[nkeys++][1] = String.valueOf(charArray[i]);
                    }
                }
                else {
                    this.tab[nkeys++][0] = String.valueOf(charArray[i]).toLowerCase();
                }
            }
            this.nkeys = nkeys;
        }
    }
    
    public String findKey(final int n) {
        if (n < 0 || n > this.asize) {
            return null;
        }
        return this.tab[n][0];
    }
    
    public String findValue(final int n) {
        if (n < 0 || n > this.asize) {
            return null;
        }
        return this.tab[n][1];
    }
    
    public String findValue(final String s) {
        return this.findValue(s, null);
    }
    
    public String findValue(String lowerCase, final String s) {
        if (lowerCase == null) {
            return s;
        }
        lowerCase = lowerCase.toLowerCase();
        for (int i = 0; i < this.asize; ++i) {
            if (this.tab[i][0] == null) {
                return s;
            }
            if (lowerCase.equals(this.tab[i][0])) {
                return this.tab[i][1];
            }
        }
        return s;
    }
    
    public Iterator<String> keys() {
        return new ParserIterator(false);
    }
    
    public Iterator<String> values() {
        return new ParserIterator(true);
    }
    
    @Override
    public String toString() {
        final Iterator<String> keys = this.keys();
        final StringBuffer sb = new StringBuffer();
        sb.append("{size=" + this.asize + " nkeys=" + this.nkeys + " ");
        int n = 0;
        while (keys.hasNext()) {
            final String s = keys.next();
            String value = this.findValue(n);
            if (value != null && "".equals(value)) {
                value = null;
            }
            sb.append(" {" + s + ((value == null) ? "" : ("," + value)) + "}");
            if (keys.hasNext()) {
                sb.append(",");
            }
            ++n;
        }
        sb.append(" }");
        return new String(sb);
    }
    
    public int findInt(final String s, final int n) {
        try {
            return Integer.parseInt(this.findValue(s, String.valueOf(n)));
        }
        catch (final Throwable t) {
            return n;
        }
    }
    
    class ParserIterator implements Iterator<String>
    {
        int index;
        boolean returnsValue;
        
        ParserIterator(final boolean returnsValue) {
            this.returnsValue = returnsValue;
        }
        
        @Override
        public boolean hasNext() {
            return this.index < HeaderParser.this.nkeys;
        }
        
        @Override
        public String next() {
            return HeaderParser.this.tab[this.index++][this.returnsValue];
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }
}

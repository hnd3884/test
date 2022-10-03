package org.apache.lucene.analysis.compound.hyphenation;

import java.io.PrintStream;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.util.ArrayList;
import java.util.HashMap;

public class HyphenationTree extends TernaryTree implements PatternConsumer
{
    protected ByteVector vspace;
    protected HashMap<String, ArrayList<Object>> stoplist;
    protected TernaryTree classmap;
    private transient TernaryTree ivalues;
    
    public HyphenationTree() {
        this.stoplist = new HashMap<String, ArrayList<Object>>(23);
        this.classmap = new TernaryTree();
        (this.vspace = new ByteVector()).alloc(1);
    }
    
    protected int packValues(final String values) {
        final int n = values.length();
        final int m = ((n & 0x1) == 0x1) ? ((n >> 1) + 2) : ((n >> 1) + 1);
        final int offset = this.vspace.alloc(m);
        final byte[] va = this.vspace.getArray();
        for (int i = 0; i < n; ++i) {
            final int j = i >> 1;
            final byte v = (byte)(values.charAt(i) - '0' + 1 & 0xF);
            if ((i & 0x1) == 0x1) {
                va[j + offset] |= v;
            }
            else {
                va[j + offset] = (byte)(v << 4);
            }
        }
        va[m - 1 + offset] = 0;
        return offset;
    }
    
    protected String unpackValues(int k) {
        final StringBuilder buf = new StringBuilder();
        for (byte v = this.vspace.get(k++); v != 0; v = this.vspace.get(k++)) {
            char c = (char)((v >>> 4) - 1 + 48);
            buf.append(c);
            c = (char)(v & 0xF);
            if (c == '\0') {
                break;
            }
            c = (char)(c - '\u0001' + 48);
            buf.append(c);
        }
        return buf.toString();
    }
    
    public void loadPatterns(final InputSource source) throws IOException {
        final PatternParser pp = new PatternParser(this);
        this.ivalues = new TernaryTree();
        pp.parse(source);
        this.trimToSize();
        this.vspace.trimToSize();
        this.classmap.trimToSize();
        this.ivalues = null;
    }
    
    public String findPattern(final String pat) {
        final int k = super.find(pat);
        if (k >= 0) {
            return this.unpackValues(k);
        }
        return "";
    }
    
    protected int hstrcmp(final char[] s, int si, final char[] t, int ti) {
        while (s[si] == t[ti]) {
            if (s[si] == '\0') {
                return 0;
            }
            ++si;
            ++ti;
        }
        if (t[ti] == '\0') {
            return 0;
        }
        return s[si] - t[ti];
    }
    
    protected byte[] getValues(int k) {
        final StringBuilder buf = new StringBuilder();
        for (byte v = this.vspace.get(k++); v != 0; v = this.vspace.get(k++)) {
            char c = (char)((v >>> 4) - 1);
            buf.append(c);
            c = (char)(v & 0xF);
            if (c == '\0') {
                break;
            }
            --c;
            buf.append(c);
        }
        final byte[] res = new byte[buf.length()];
        for (int i = 0; i < res.length; ++i) {
            res[i] = (byte)buf.charAt(i);
        }
        return res;
    }
    
    protected void searchPatterns(final char[] word, final int index, final byte[] il) {
        int i = index;
        char sp = word[i];
        char p = this.root;
        while (p > '\0' && p < this.sc.length) {
            if (this.sc[p] == '\uffff') {
                if (this.hstrcmp(word, i, this.kv.getArray(), this.lo[p]) == 0) {
                    final byte[] values = this.getValues(this.eq[p]);
                    int j = index;
                    for (int k = 0; k < values.length; ++k) {
                        if (j < il.length && values[k] > il[j]) {
                            il[j] = values[k];
                        }
                        ++j;
                    }
                }
                return;
            }
            final int d = sp - this.sc[p];
            if (d == 0) {
                if (sp == '\0') {
                    break;
                }
                sp = word[++i];
                char q;
                for (p = (q = this.eq[p]); q > '\0' && q < this.sc.length; q = this.lo[q]) {
                    if (this.sc[q] == '\uffff') {
                        break;
                    }
                    if (this.sc[q] == '\0') {
                        final byte[] values = this.getValues(this.eq[q]);
                        int l = index;
                        for (int m = 0; m < values.length; ++m) {
                            if (l < il.length && values[m] > il[l]) {
                                il[l] = values[m];
                            }
                            ++l;
                        }
                        break;
                    }
                }
            }
            else {
                p = ((d < 0) ? this.lo[p] : this.hi[p]);
            }
        }
    }
    
    public Hyphenation hyphenate(final String word, final int remainCharCount, final int pushCharCount) {
        final char[] w = word.toCharArray();
        return this.hyphenate(w, 0, w.length, remainCharCount, pushCharCount);
    }
    
    public Hyphenation hyphenate(final char[] w, final int offset, int len, final int remainCharCount, final int pushCharCount) {
        final char[] word = new char[len + 3];
        final char[] c = new char[2];
        int iIgnoreAtBeginning = 0;
        int iLength = len;
        boolean bEndOfLetters = false;
        for (int i = 1; i <= len; ++i) {
            c[0] = w[offset + i - 1];
            final int nc = this.classmap.find(c, 0);
            if (nc < 0) {
                if (i == 1 + iIgnoreAtBeginning) {
                    ++iIgnoreAtBeginning;
                }
                else {
                    bEndOfLetters = true;
                }
                --iLength;
            }
            else {
                if (bEndOfLetters) {
                    return null;
                }
                word[i - iIgnoreAtBeginning] = (char)nc;
            }
        }
        len = iLength;
        if (len < remainCharCount + pushCharCount) {
            return null;
        }
        final int[] result = new int[len + 1];
        int k = 0;
        final String sw = new String(word, 1, len);
        if (this.stoplist.containsKey(sw)) {
            final ArrayList<Object> hw = this.stoplist.get(sw);
            int j = 0;
            for (int i = 0; i < hw.size(); ++i) {
                final Object o = hw.get(i);
                if (o instanceof String) {
                    j += ((String)o).length();
                    if (j >= remainCharCount && j < len - pushCharCount) {
                        result[k++] = j + iIgnoreAtBeginning;
                    }
                }
            }
        }
        else {
            word[len + 1] = (word[0] = '.');
            word[len + 2] = '\0';
            final byte[] il = new byte[len + 3];
            for (int i = 0; i < len + 1; ++i) {
                this.searchPatterns(word, i, il);
            }
            for (int i = 0; i < len; ++i) {
                if ((il[i + 1] & 0x1) == 0x1 && i >= remainCharCount && i <= len - pushCharCount) {
                    result[k++] = i + iIgnoreAtBeginning;
                }
            }
        }
        if (k > 0) {
            final int[] res = new int[k + 2];
            System.arraycopy(result, 0, res, 1, k);
            res[0] = 0;
            res[k + 1] = len;
            return new Hyphenation(res);
        }
        return null;
    }
    
    @Override
    public void addClass(final String chargroup) {
        if (chargroup.length() > 0) {
            final char equivChar = chargroup.charAt(0);
            final char[] key = { '\0', '\0' };
            for (int i = 0; i < chargroup.length(); ++i) {
                key[0] = chargroup.charAt(i);
                this.classmap.insert(key, 0, equivChar);
            }
        }
    }
    
    @Override
    public void addException(final String word, final ArrayList<Object> hyphenatedword) {
        this.stoplist.put(word, hyphenatedword);
    }
    
    @Override
    public void addPattern(final String pattern, final String ivalue) {
        int k = this.ivalues.find(ivalue);
        if (k <= 0) {
            k = this.packValues(ivalue);
            this.ivalues.insert(ivalue, (char)k);
        }
        this.insert(pattern, (char)k);
    }
    
    @Override
    public void printStats(final PrintStream out) {
        out.println("Value space size = " + Integer.toString(this.vspace.length()));
        super.printStats(out);
    }
}

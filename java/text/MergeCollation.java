package java.text;

import java.util.ArrayList;

final class MergeCollation
{
    ArrayList<PatternEntry> patterns;
    private transient PatternEntry saveEntry;
    private transient PatternEntry lastEntry;
    private transient StringBuffer excess;
    private transient byte[] statusArray;
    private final byte BITARRAYMASK = 1;
    private final int BYTEPOWER = 3;
    private final int BYTEMASK = 7;
    
    public MergeCollation(final String pattern) throws ParseException {
        this.patterns = new ArrayList<PatternEntry>();
        this.saveEntry = null;
        this.lastEntry = null;
        this.excess = new StringBuffer();
        this.statusArray = new byte[8192];
        for (int i = 0; i < this.statusArray.length; ++i) {
            this.statusArray[i] = 0;
        }
        this.setPattern(pattern);
    }
    
    public String getPattern() {
        return this.getPattern(true);
    }
    
    public String getPattern(final boolean b) {
        final StringBuffer sb = new StringBuffer();
        ArrayList<PatternEntry> list = null;
        int i;
        for (i = 0; i < this.patterns.size(); ++i) {
            final PatternEntry patternEntry = this.patterns.get(i);
            if (patternEntry.extension.length() != 0) {
                if (list == null) {
                    list = new ArrayList<PatternEntry>();
                }
                list.add(patternEntry);
            }
            else {
                if (list != null) {
                    final PatternEntry lastWithNoExtension = this.findLastWithNoExtension(i - 1);
                    for (int j = list.size() - 1; j >= 0; --j) {
                        list.get(j).addToBuffer(sb, false, b, lastWithNoExtension);
                    }
                    list = null;
                }
                patternEntry.addToBuffer(sb, false, b, null);
            }
        }
        if (list != null) {
            final PatternEntry lastWithNoExtension2 = this.findLastWithNoExtension(i - 1);
            for (int k = list.size() - 1; k >= 0; --k) {
                list.get(k).addToBuffer(sb, false, b, lastWithNoExtension2);
            }
        }
        return sb.toString();
    }
    
    private final PatternEntry findLastWithNoExtension(int i) {
        --i;
        while (i >= 0) {
            final PatternEntry patternEntry = this.patterns.get(i);
            if (patternEntry.extension.length() == 0) {
                return patternEntry;
            }
            --i;
        }
        return null;
    }
    
    public String emitPattern() {
        return this.emitPattern(true);
    }
    
    public String emitPattern(final boolean b) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.patterns.size(); ++i) {
            final PatternEntry patternEntry = this.patterns.get(i);
            if (patternEntry != null) {
                patternEntry.addToBuffer(sb, true, b, null);
            }
        }
        return sb.toString();
    }
    
    public void setPattern(final String s) throws ParseException {
        this.patterns.clear();
        this.addPattern(s);
    }
    
    public void addPattern(final String s) throws ParseException {
        if (s == null) {
            return;
        }
        final PatternEntry.Parser parser = new PatternEntry.Parser(s);
        for (PatternEntry patternEntry = parser.next(); patternEntry != null; patternEntry = parser.next()) {
            this.fixEntry(patternEntry);
        }
    }
    
    public int getCount() {
        return this.patterns.size();
    }
    
    public PatternEntry getItemAt(final int n) {
        return this.patterns.get(n);
    }
    
    private final void fixEntry(final PatternEntry patternEntry) throws ParseException {
        if (this.lastEntry == null || !patternEntry.chars.equals(this.lastEntry.chars) || !patternEntry.extension.equals(this.lastEntry.extension)) {
            boolean b = true;
            if (patternEntry.strength != -2) {
                int n = -1;
                if (patternEntry.chars.length() == 1) {
                    final char char1 = patternEntry.chars.charAt(0);
                    final int n2 = char1 >> 3;
                    final byte b2 = this.statusArray[n2];
                    final byte b3 = (byte)(1 << (char1 & '\u0007'));
                    if (b2 != 0 && (b2 & b3) != 0x0) {
                        n = this.patterns.lastIndexOf(patternEntry);
                    }
                    else {
                        this.statusArray[n2] = (byte)(b2 | b3);
                    }
                }
                else {
                    n = this.patterns.lastIndexOf(patternEntry);
                }
                if (n != -1) {
                    this.patterns.remove(n);
                }
                this.excess.setLength(0);
                final int lastEntry = this.findLastEntry(this.lastEntry, this.excess);
                if (this.excess.length() != 0) {
                    patternEntry.extension = (Object)this.excess + patternEntry.extension;
                    if (lastEntry != this.patterns.size()) {
                        this.lastEntry = this.saveEntry;
                        b = false;
                    }
                }
                if (lastEntry == this.patterns.size()) {
                    this.patterns.add(patternEntry);
                    this.saveEntry = patternEntry;
                }
                else {
                    this.patterns.add(lastEntry, patternEntry);
                }
            }
            if (b) {
                this.lastEntry = patternEntry;
            }
            return;
        }
        if (patternEntry.strength != 3 && patternEntry.strength != -2) {
            throw new ParseException("The entries " + this.lastEntry + " and " + patternEntry + " are adjacent in the rules, but have conflicting strengths: A character can't be unequal to itself.", -1);
        }
    }
    
    private final int findLastEntry(final PatternEntry patternEntry, final StringBuffer sb) throws ParseException {
        if (patternEntry == null) {
            return 0;
        }
        if (patternEntry.strength != -2) {
            int n = -1;
            if (patternEntry.chars.length() == 1) {
                if ((this.statusArray[patternEntry.chars.charAt(0) >> 3] & 1 << (patternEntry.chars.charAt(0) & '\u0007')) != 0x0) {
                    n = this.patterns.lastIndexOf(patternEntry);
                }
            }
            else {
                n = this.patterns.lastIndexOf(patternEntry);
            }
            if (n == -1) {
                throw new ParseException("couldn't find last entry: " + patternEntry, n);
            }
            return n + 1;
        }
        else {
            int i;
            for (i = this.patterns.size() - 1; i >= 0; --i) {
                final PatternEntry patternEntry2 = this.patterns.get(i);
                if (patternEntry2.chars.regionMatches(0, patternEntry.chars, 0, patternEntry2.chars.length())) {
                    sb.append(patternEntry.chars.substring(patternEntry2.chars.length(), patternEntry.chars.length()));
                    break;
                }
            }
            if (i == -1) {
                throw new ParseException("couldn't find: " + patternEntry, i);
            }
            return i + 1;
        }
    }
}

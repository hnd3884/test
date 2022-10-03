package org.apache.lucene.analysis.miscellaneous;

public final class WordDelimiterIterator
{
    public static final int DONE = -1;
    public static final byte[] DEFAULT_WORD_DELIM_TABLE;
    char[] text;
    int length;
    int startBounds;
    int endBounds;
    int current;
    int end;
    private boolean hasFinalPossessive;
    final boolean splitOnCaseChange;
    final boolean splitOnNumerics;
    final boolean stemEnglishPossessive;
    private final byte[] charTypeTable;
    private boolean skipPossessive;
    
    WordDelimiterIterator(final byte[] charTypeTable, final boolean splitOnCaseChange, final boolean splitOnNumerics, final boolean stemEnglishPossessive) {
        this.hasFinalPossessive = false;
        this.skipPossessive = false;
        this.charTypeTable = charTypeTable;
        this.splitOnCaseChange = splitOnCaseChange;
        this.splitOnNumerics = splitOnNumerics;
        this.stemEnglishPossessive = stemEnglishPossessive;
    }
    
    int next() {
        this.current = this.end;
        if (this.current == -1) {
            return -1;
        }
        if (this.skipPossessive) {
            this.current += 2;
            this.skipPossessive = false;
        }
        int lastType = 0;
        while (this.current < this.endBounds && WordDelimiterFilter.isSubwordDelim(lastType = this.charType(this.text[this.current]))) {
            ++this.current;
        }
        if (this.current >= this.endBounds) {
            return this.end = -1;
        }
        this.end = this.current + 1;
        while (this.end < this.endBounds) {
            final int type = this.charType(this.text[this.end]);
            if (this.isBreak(lastType, type)) {
                break;
            }
            lastType = type;
            ++this.end;
        }
        if (this.end < this.endBounds - 1 && this.endsWithPossessive(this.end + 2)) {
            this.skipPossessive = true;
        }
        return this.end;
    }
    
    int type() {
        if (this.end == -1) {
            return 0;
        }
        final int type = this.charType(this.text[this.current]);
        switch (type) {
            case 1:
            case 2: {
                return 3;
            }
            default: {
                return type;
            }
        }
    }
    
    void setText(final char[] text, final int length) {
        this.text = text;
        this.endBounds = length;
        this.length = length;
        final int current = 0;
        this.end = current;
        this.startBounds = current;
        this.current = current;
        final boolean b = false;
        this.hasFinalPossessive = b;
        this.skipPossessive = b;
        this.setBounds();
    }
    
    private boolean isBreak(final int lastType, final int type) {
        return (type & lastType) == 0x0 && (this.splitOnCaseChange || !WordDelimiterFilter.isAlpha(lastType) || !WordDelimiterFilter.isAlpha(type)) && (!WordDelimiterFilter.isUpper(lastType) || !WordDelimiterFilter.isAlpha(type)) && (this.splitOnNumerics || ((!WordDelimiterFilter.isAlpha(lastType) || !WordDelimiterFilter.isDigit(type)) && (!WordDelimiterFilter.isDigit(lastType) || !WordDelimiterFilter.isAlpha(type))));
    }
    
    boolean isSingleWord() {
        if (this.hasFinalPossessive) {
            return this.current == this.startBounds && this.end == this.endBounds - 2;
        }
        return this.current == this.startBounds && this.end == this.endBounds;
    }
    
    private void setBounds() {
        while (this.startBounds < this.length && WordDelimiterFilter.isSubwordDelim(this.charType(this.text[this.startBounds]))) {
            ++this.startBounds;
        }
        while (this.endBounds > this.startBounds && WordDelimiterFilter.isSubwordDelim(this.charType(this.text[this.endBounds - 1]))) {
            --this.endBounds;
        }
        if (this.endsWithPossessive(this.endBounds)) {
            this.hasFinalPossessive = true;
        }
        this.current = this.startBounds;
    }
    
    private boolean endsWithPossessive(final int pos) {
        return this.stemEnglishPossessive && pos > 2 && this.text[pos - 2] == '\'' && (this.text[pos - 1] == 's' || this.text[pos - 1] == 'S') && WordDelimiterFilter.isAlpha(this.charType(this.text[pos - 3])) && (pos == this.endBounds || WordDelimiterFilter.isSubwordDelim(this.charType(this.text[pos])));
    }
    
    private int charType(final int ch) {
        if (ch < this.charTypeTable.length) {
            return this.charTypeTable[ch];
        }
        return getType(ch);
    }
    
    public static byte getType(final int ch) {
        switch (Character.getType(ch)) {
            case 1: {
                return 2;
            }
            case 2: {
                return 1;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {
                return 3;
            }
            case 9:
            case 10:
            case 11: {
                return 4;
            }
            case 19: {
                return 7;
            }
            default: {
                return 8;
            }
        }
    }
    
    static {
        final byte[] tab = new byte[256];
        for (int i = 0; i < 256; ++i) {
            byte code = 0;
            if (Character.isLowerCase(i)) {
                code |= 0x1;
            }
            else if (Character.isUpperCase(i)) {
                code |= 0x2;
            }
            else if (Character.isDigit(i)) {
                code |= 0x4;
            }
            if (code == 0) {
                code = 8;
            }
            tab[i] = code;
        }
        DEFAULT_WORD_DELIM_TABLE = tab;
    }
}

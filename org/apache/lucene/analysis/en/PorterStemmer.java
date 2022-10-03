package org.apache.lucene.analysis.en;

import org.apache.lucene.util.ArrayUtil;

class PorterStemmer
{
    private char[] b;
    private int i;
    private int j;
    private int k;
    private int k0;
    private boolean dirty;
    private static final int INITIAL_SIZE = 50;
    
    public PorterStemmer() {
        this.dirty = false;
        this.b = new char[50];
        this.i = 0;
    }
    
    public void reset() {
        this.i = 0;
        this.dirty = false;
    }
    
    public void add(final char ch) {
        if (this.b.length <= this.i) {
            this.b = ArrayUtil.grow(this.b, this.i + 1);
        }
        this.b[this.i++] = ch;
    }
    
    @Override
    public String toString() {
        return new String(this.b, 0, this.i);
    }
    
    public int getResultLength() {
        return this.i;
    }
    
    public char[] getResultBuffer() {
        return this.b;
    }
    
    private final boolean cons(final int i) {
        switch (this.b[i]) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u': {
                return false;
            }
            case 'y': {
                return i == this.k0 || !this.cons(i - 1);
            }
            default: {
                return true;
            }
        }
    }
    
    private final int m() {
        int n = 0;
        for (int i = this.k0; i <= this.j; ++i) {
            if (!this.cons(i)) {
                ++i;
            Label_0037:
                while (i <= this.j) {
                    if (this.cons(i)) {
                        ++i;
                        ++n;
                        while (i <= this.j) {
                            if (!this.cons(i)) {
                                ++i;
                                continue Label_0037;
                            }
                            ++i;
                        }
                        return n;
                    }
                    ++i;
                }
                return n;
            }
        }
        return n;
    }
    
    private final boolean vowelinstem() {
        for (int i = this.k0; i <= this.j; ++i) {
            if (!this.cons(i)) {
                return true;
            }
        }
        return false;
    }
    
    private final boolean doublec(final int j) {
        return j >= this.k0 + 1 && this.b[j] == this.b[j - 1] && this.cons(j);
    }
    
    private final boolean cvc(final int i) {
        if (i < this.k0 + 2 || !this.cons(i) || this.cons(i - 1) || !this.cons(i - 2)) {
            return false;
        }
        final int ch = this.b[i];
        return ch != 119 && ch != 120 && ch != 121;
    }
    
    private final boolean ends(final String s) {
        final int l = s.length();
        final int o = this.k - l + 1;
        if (o < this.k0) {
            return false;
        }
        for (int i = 0; i < l; ++i) {
            if (this.b[o + i] != s.charAt(i)) {
                return false;
            }
        }
        this.j = this.k - l;
        return true;
    }
    
    void setto(final String s) {
        final int l = s.length();
        final int o = this.j + 1;
        for (int i = 0; i < l; ++i) {
            this.b[o + i] = s.charAt(i);
        }
        this.k = this.j + l;
        this.dirty = true;
    }
    
    void r(final String s) {
        if (this.m() > 0) {
            this.setto(s);
        }
    }
    
    private final void step1() {
        if (this.b[this.k] == 's') {
            if (this.ends("sses")) {
                this.k -= 2;
            }
            else if (this.ends("ies")) {
                this.setto("i");
            }
            else if (this.b[this.k - 1] != 's') {
                --this.k;
            }
        }
        if (this.ends("eed")) {
            if (this.m() > 0) {
                --this.k;
            }
        }
        else if ((this.ends("ed") || this.ends("ing")) && this.vowelinstem()) {
            this.k = this.j;
            if (this.ends("at")) {
                this.setto("ate");
            }
            else if (this.ends("bl")) {
                this.setto("ble");
            }
            else if (this.ends("iz")) {
                this.setto("ize");
            }
            else if (this.doublec(this.k)) {
                final int ch = this.b[this.k--];
                if (ch == 108 || ch == 115 || ch == 122) {
                    ++this.k;
                }
            }
            else if (this.m() == 1 && this.cvc(this.k)) {
                this.setto("e");
            }
        }
    }
    
    private final void step2() {
        if (this.ends("y") && this.vowelinstem()) {
            this.b[this.k] = 'i';
            this.dirty = true;
        }
    }
    
    private final void step3() {
        if (this.k == this.k0) {
            return;
        }
        switch (this.b[this.k - 1]) {
            case 'a': {
                if (this.ends("ational")) {
                    this.r("ate");
                    break;
                }
                if (this.ends("tional")) {
                    this.r("tion");
                    break;
                }
                break;
            }
            case 'c': {
                if (this.ends("enci")) {
                    this.r("ence");
                    break;
                }
                if (this.ends("anci")) {
                    this.r("ance");
                    break;
                }
                break;
            }
            case 'e': {
                if (this.ends("izer")) {
                    this.r("ize");
                    break;
                }
                break;
            }
            case 'l': {
                if (this.ends("bli")) {
                    this.r("ble");
                    break;
                }
                if (this.ends("alli")) {
                    this.r("al");
                    break;
                }
                if (this.ends("entli")) {
                    this.r("ent");
                    break;
                }
                if (this.ends("eli")) {
                    this.r("e");
                    break;
                }
                if (this.ends("ousli")) {
                    this.r("ous");
                    break;
                }
                break;
            }
            case 'o': {
                if (this.ends("ization")) {
                    this.r("ize");
                    break;
                }
                if (this.ends("ation")) {
                    this.r("ate");
                    break;
                }
                if (this.ends("ator")) {
                    this.r("ate");
                    break;
                }
                break;
            }
            case 's': {
                if (this.ends("alism")) {
                    this.r("al");
                    break;
                }
                if (this.ends("iveness")) {
                    this.r("ive");
                    break;
                }
                if (this.ends("fulness")) {
                    this.r("ful");
                    break;
                }
                if (this.ends("ousness")) {
                    this.r("ous");
                    break;
                }
                break;
            }
            case 't': {
                if (this.ends("aliti")) {
                    this.r("al");
                    break;
                }
                if (this.ends("iviti")) {
                    this.r("ive");
                    break;
                }
                if (this.ends("biliti")) {
                    this.r("ble");
                    break;
                }
                break;
            }
            case 'g': {
                if (this.ends("logi")) {
                    this.r("log");
                    break;
                }
                break;
            }
        }
    }
    
    private final void step4() {
        switch (this.b[this.k]) {
            case 'e': {
                if (this.ends("icate")) {
                    this.r("ic");
                    break;
                }
                if (this.ends("ative")) {
                    this.r("");
                    break;
                }
                if (this.ends("alize")) {
                    this.r("al");
                    break;
                }
                break;
            }
            case 'i': {
                if (this.ends("iciti")) {
                    this.r("ic");
                    break;
                }
                break;
            }
            case 'l': {
                if (this.ends("ical")) {
                    this.r("ic");
                    break;
                }
                if (this.ends("ful")) {
                    this.r("");
                    break;
                }
                break;
            }
            case 's': {
                if (this.ends("ness")) {
                    this.r("");
                    break;
                }
                break;
            }
        }
    }
    
    private final void step5() {
        if (this.k == this.k0) {
            return;
        }
        switch (this.b[this.k - 1]) {
            case 'a': {
                if (this.ends("al")) {
                    break;
                }
                return;
            }
            case 'c': {
                if (this.ends("ance")) {
                    break;
                }
                if (this.ends("ence")) {
                    break;
                }
                return;
            }
            case 'e': {
                if (this.ends("er")) {
                    break;
                }
                return;
            }
            case 'i': {
                if (this.ends("ic")) {
                    break;
                }
                return;
            }
            case 'l': {
                if (this.ends("able")) {
                    break;
                }
                if (this.ends("ible")) {
                    break;
                }
                return;
            }
            case 'n': {
                if (this.ends("ant")) {
                    break;
                }
                if (this.ends("ement")) {
                    break;
                }
                if (this.ends("ment")) {
                    break;
                }
                if (this.ends("ent")) {
                    break;
                }
                return;
            }
            case 'o': {
                if (this.ends("ion") && this.j >= 0) {
                    if (this.b[this.j] == 's') {
                        break;
                    }
                    if (this.b[this.j] == 't') {
                        break;
                    }
                }
                if (this.ends("ou")) {
                    break;
                }
                return;
            }
            case 's': {
                if (this.ends("ism")) {
                    break;
                }
                return;
            }
            case 't': {
                if (this.ends("ate")) {
                    break;
                }
                if (this.ends("iti")) {
                    break;
                }
                return;
            }
            case 'u': {
                if (this.ends("ous")) {
                    break;
                }
                return;
            }
            case 'v': {
                if (this.ends("ive")) {
                    break;
                }
                return;
            }
            case 'z': {
                if (this.ends("ize")) {
                    break;
                }
                return;
            }
            default: {
                return;
            }
        }
        if (this.m() > 1) {
            this.k = this.j;
        }
    }
    
    private final void step6() {
        this.j = this.k;
        if (this.b[this.k] == 'e') {
            final int a = this.m();
            if (a > 1 || (a == 1 && !this.cvc(this.k - 1))) {
                --this.k;
            }
        }
        if (this.b[this.k] == 'l' && this.doublec(this.k) && this.m() > 1) {
            --this.k;
        }
    }
    
    public String stem(final String s) {
        if (this.stem(s.toCharArray(), s.length())) {
            return this.toString();
        }
        return s;
    }
    
    public boolean stem(final char[] word) {
        return this.stem(word, word.length);
    }
    
    public boolean stem(final char[] wordBuffer, final int offset, final int wordLen) {
        this.reset();
        if (this.b.length < wordLen) {
            this.b = new char[ArrayUtil.oversize(wordLen, 2)];
        }
        System.arraycopy(wordBuffer, offset, this.b, 0, wordLen);
        this.i = wordLen;
        return this.stem(0);
    }
    
    public boolean stem(final char[] word, final int wordLen) {
        return this.stem(word, 0, wordLen);
    }
    
    public boolean stem() {
        return this.stem(0);
    }
    
    public boolean stem(final int i0) {
        this.k = this.i - 1;
        this.k0 = i0;
        if (this.k > this.k0 + 1) {
            this.step1();
            this.step2();
            this.step3();
            this.step4();
            this.step5();
            this.step6();
        }
        if (this.i != this.k + 1) {
            this.dirty = true;
        }
        this.i = this.k + 1;
        return this.dirty;
    }
}

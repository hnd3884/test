package org.apache.lucene.analysis.en;

import org.apache.lucene.analysis.util.OpenStringBuilder;
import org.apache.lucene.analysis.util.CharArrayMap;

public class KStemmer
{
    private static final int MaxWordLen = 50;
    private static final String[] exceptionWords;
    private static final String[][] directConflations;
    private static final String[][] countryNationality;
    private static final String[] supplementDict;
    private static final String[] properNouns;
    private static final CharArrayMap<DictEntry> dict_ht;
    private final OpenStringBuilder word;
    private int j;
    private int k;
    DictEntry matchedEntry;
    private static char[] ization;
    private static char[] ition;
    private static char[] ation;
    private static char[] ication;
    String result;
    
    private char finalChar() {
        return this.word.charAt(this.k);
    }
    
    private char penultChar() {
        return this.word.charAt(this.k - 1);
    }
    
    private boolean isVowel(final int index) {
        return !this.isCons(index);
    }
    
    private boolean isCons(final int index) {
        final char ch = this.word.charAt(index);
        return ch != 'a' && ch != 'e' && ch != 'i' && ch != 'o' && ch != 'u' && (ch != 'y' || index == 0 || !this.isCons(index - 1));
    }
    
    private static CharArrayMap<DictEntry> initializeDictHash() {
        final CharArrayMap<DictEntry> d = new CharArrayMap<DictEntry>(1000, false);
        for (int i = 0; i < KStemmer.exceptionWords.length; ++i) {
            if (d.containsKey(KStemmer.exceptionWords[i])) {
                throw new RuntimeException("Warning: Entry [" + KStemmer.exceptionWords[i] + "] already in dictionary 1");
            }
            final DictEntry entry = new DictEntry(KStemmer.exceptionWords[i], true);
            d.put(KStemmer.exceptionWords[i], entry);
        }
        for (int i = 0; i < KStemmer.directConflations.length; ++i) {
            if (d.containsKey(KStemmer.directConflations[i][0])) {
                throw new RuntimeException("Warning: Entry [" + KStemmer.directConflations[i][0] + "] already in dictionary 2");
            }
            final DictEntry entry = new DictEntry(KStemmer.directConflations[i][1], false);
            d.put(KStemmer.directConflations[i][0], entry);
        }
        for (int i = 0; i < KStemmer.countryNationality.length; ++i) {
            if (d.containsKey(KStemmer.countryNationality[i][0])) {
                throw new RuntimeException("Warning: Entry [" + KStemmer.countryNationality[i][0] + "] already in dictionary 3");
            }
            final DictEntry entry = new DictEntry(KStemmer.countryNationality[i][1], false);
            d.put(KStemmer.countryNationality[i][0], entry);
        }
        final DictEntry defaultEntry = new DictEntry(null, false);
        String[] array = KStemData1.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        array = KStemData2.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        array = KStemData3.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        array = KStemData4.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        array = KStemData5.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        array = KStemData6.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        array = KStemData7.data;
        for (int j = 0; j < array.length; ++j) {
            if (d.containsKey(array[j])) {
                throw new RuntimeException("Warning: Entry [" + array[j] + "] already in dictionary 4");
            }
            d.put(array[j], defaultEntry);
        }
        for (int j = 0; j < KStemData8.data.length; ++j) {
            if (d.containsKey(KStemData8.data[j])) {
                throw new RuntimeException("Warning: Entry [" + KStemData8.data[j] + "] already in dictionary 4");
            }
            d.put(KStemData8.data[j], defaultEntry);
        }
        for (int j = 0; j < KStemmer.supplementDict.length; ++j) {
            if (d.containsKey(KStemmer.supplementDict[j])) {
                throw new RuntimeException("Warning: Entry [" + KStemmer.supplementDict[j] + "] already in dictionary 5");
            }
            d.put(KStemmer.supplementDict[j], defaultEntry);
        }
        for (int j = 0; j < KStemmer.properNouns.length; ++j) {
            if (d.containsKey(KStemmer.properNouns[j])) {
                throw new RuntimeException("Warning: Entry [" + KStemmer.properNouns[j] + "] already in dictionary 6");
            }
            d.put(KStemmer.properNouns[j], defaultEntry);
        }
        return d;
    }
    
    private boolean isAlpha(final char ch) {
        return ch >= 'a' && ch <= 'z';
    }
    
    private int stemLength() {
        return this.j + 1;
    }
    
    private boolean endsIn(final char[] s) {
        if (s.length > this.k) {
            return false;
        }
        final int r = this.word.length() - s.length;
        this.j = this.k;
        for (int r2 = r, i = 0; i < s.length; ++i, ++r2) {
            if (s[i] != this.word.charAt(r2)) {
                return false;
            }
        }
        this.j = r - 1;
        return true;
    }
    
    private boolean endsIn(final char a, final char b) {
        if (2 > this.k) {
            return false;
        }
        if (this.word.charAt(this.k - 1) == a && this.word.charAt(this.k) == b) {
            this.j = this.k - 2;
            return true;
        }
        return false;
    }
    
    private boolean endsIn(final char a, final char b, final char c) {
        if (3 > this.k) {
            return false;
        }
        if (this.word.charAt(this.k - 2) == a && this.word.charAt(this.k - 1) == b && this.word.charAt(this.k) == c) {
            this.j = this.k - 3;
            return true;
        }
        return false;
    }
    
    private boolean endsIn(final char a, final char b, final char c, final char d) {
        if (4 > this.k) {
            return false;
        }
        if (this.word.charAt(this.k - 3) == a && this.word.charAt(this.k - 2) == b && this.word.charAt(this.k - 1) == c && this.word.charAt(this.k) == d) {
            this.j = this.k - 4;
            return true;
        }
        return false;
    }
    
    private DictEntry wordInDict() {
        if (this.matchedEntry != null) {
            return this.matchedEntry;
        }
        final DictEntry e = KStemmer.dict_ht.get(this.word.getArray(), 0, this.word.length());
        if (e != null && !e.exception) {
            this.matchedEntry = e;
        }
        return e;
    }
    
    private void plural() {
        if (this.word.charAt(this.k) == 's') {
            if (this.endsIn('i', 'e', 's')) {
                this.word.setLength(this.j + 3);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                ++this.k;
                this.word.unsafeWrite('s');
                this.setSuffix("y");
                this.lookup();
            }
            else if (this.endsIn('e', 's')) {
                this.word.setLength(this.j + 2);
                --this.k;
                final boolean tryE = this.j > 0 && (this.word.charAt(this.j) != 's' || this.word.charAt(this.j - 1) != 's');
                if (tryE && this.lookup()) {
                    return;
                }
                this.word.setLength(this.j + 1);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite('e');
                ++this.k;
                if (!tryE) {
                    this.lookup();
                }
            }
            else if (this.word.length() > 3 && this.penultChar() != 's' && !this.endsIn('o', 'u', 's')) {
                this.word.setLength(this.k);
                --this.k;
                this.lookup();
            }
        }
    }
    
    private void setSuffix(final String s) {
        this.setSuff(s, s.length());
    }
    
    private void setSuff(final String s, final int len) {
        this.word.setLength(this.j + 1);
        for (int l = 0; l < len; ++l) {
            this.word.unsafeWrite(s.charAt(l));
        }
        this.k = this.j + len;
    }
    
    private boolean lookup() {
        this.matchedEntry = KStemmer.dict_ht.get(this.word.getArray(), 0, this.word.size());
        return this.matchedEntry != null;
    }
    
    private void pastTense() {
        if (this.word.length() <= 4) {
            return;
        }
        if (this.endsIn('i', 'e', 'd')) {
            this.word.setLength(this.j + 3);
            --this.k;
            if (this.lookup()) {
                return;
            }
            ++this.k;
            this.word.unsafeWrite('d');
            this.setSuffix("y");
            this.lookup();
        }
        else {
            if (!this.endsIn('e', 'd') || !this.vowelInStem()) {
                return;
            }
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            final DictEntry entry = this.wordInDict();
            if (entry != null && !entry.exception) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.k)) {
                this.word.setLength(this.k);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.k));
                ++this.k;
                this.lookup();
            }
            else {
                if (this.word.charAt(0) == 'u' && this.word.charAt(1) == 'n') {
                    this.word.unsafeWrite('e');
                    this.word.unsafeWrite('d');
                    this.k += 2;
                    return;
                }
                this.word.setLength(this.j + 1);
                this.word.unsafeWrite('e');
                this.k = this.j + 1;
            }
        }
    }
    
    private boolean doubleC(final int i) {
        return i >= 1 && this.word.charAt(i) == this.word.charAt(i - 1) && this.isCons(i);
    }
    
    private boolean vowelInStem() {
        for (int i = 0; i < this.stemLength(); ++i) {
            if (this.isVowel(i)) {
                return true;
            }
        }
        return false;
    }
    
    private void aspect() {
        if (this.word.length() <= 5) {
            return;
        }
        if (!this.endsIn('i', 'n', 'g') || !this.vowelInStem()) {
            return;
        }
        this.word.setCharAt(this.j + 1, 'e');
        this.word.setLength(this.j + 2);
        this.k = this.j + 1;
        final DictEntry entry = this.wordInDict();
        if (entry != null && !entry.exception) {
            return;
        }
        this.word.setLength(this.k);
        --this.k;
        if (this.lookup()) {
            return;
        }
        if (this.doubleC(this.k)) {
            --this.k;
            this.word.setLength(this.k + 1);
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite(this.word.charAt(this.k));
            ++this.k;
            this.lookup();
        }
        else {
            if (this.j > 0 && this.isCons(this.j) && this.isCons(this.j - 1)) {
                this.k = this.j;
                this.word.setLength(this.k + 1);
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
        }
    }
    
    private void ityEndings() {
        final int old_k = this.k;
        if (!this.endsIn('i', 't', 'y')) {
            return;
        }
        this.word.setLength(this.j + 1);
        this.k = this.j;
        if (this.lookup()) {
            return;
        }
        this.word.unsafeWrite('e');
        this.k = this.j + 1;
        if (this.lookup()) {
            return;
        }
        this.word.setCharAt(this.j + 1, 'i');
        this.word.append("ty");
        this.k = old_k;
        if (this.j > 0 && this.word.charAt(this.j - 1) == 'i' && this.word.charAt(this.j) == 'l') {
            this.word.setLength(this.j - 1);
            this.word.append("le");
            this.k = this.j;
            this.lookup();
            return;
        }
        if (this.j > 0 && this.word.charAt(this.j - 1) == 'i' && this.word.charAt(this.j) == 'v') {
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            this.lookup();
            return;
        }
        if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 'l') {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            this.lookup();
            return;
        }
        if (this.lookup()) {
            return;
        }
        this.word.setLength(this.j + 1);
        this.k = this.j;
    }
    
    private void nceEndings() {
        final int old_k = this.k;
        if (this.endsIn('n', 'c', 'e')) {
            final char word_char = this.word.charAt(this.j);
            if (word_char != 'e' && word_char != 'a') {
                return;
            }
            this.word.setLength(this.j);
            this.word.unsafeWrite('e');
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j);
            this.k = this.j - 1;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite(word_char);
            this.word.append("nce");
            this.k = old_k;
        }
    }
    
    private void nessEndings() {
        if (this.endsIn('n', 'e', 's', 's')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.word.charAt(this.j) == 'i') {
                this.word.setCharAt(this.j, 'y');
            }
            this.lookup();
        }
    }
    
    private void ismEndings() {
        if (this.endsIn('i', 's', 'm')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            this.lookup();
        }
    }
    
    private void mentEndings() {
        final int old_k = this.k;
        if (this.endsIn('m', 'e', 'n', 't')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.append("ment");
            this.k = old_k;
        }
    }
    
    private void izeEndings() {
        final int old_k = this.k;
        if (this.endsIn('i', 'z', 'e')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('i');
            if (this.doubleC(this.j)) {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.j - 1));
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ize");
            this.k = old_k;
        }
    }
    
    private void ncyEndings() {
        if (this.endsIn('n', 'c', 'y')) {
            if (this.word.charAt(this.j) != 'e' && this.word.charAt(this.j) != 'a') {
                return;
            }
            this.word.setCharAt(this.j + 2, 't');
            this.word.setLength(this.j + 3);
            this.k = this.j + 2;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 2, 'c');
            this.word.unsafeWrite('e');
            this.k = this.j + 3;
            this.lookup();
        }
    }
    
    private void bleEndings() {
        final int old_k = this.k;
        if (this.endsIn('b', 'l', 'e')) {
            if (this.word.charAt(this.j) != 'a' && this.word.charAt(this.j) != 'i') {
                return;
            }
            final char word_char = this.word.charAt(this.j);
            this.word.setLength(this.j);
            this.k = this.j - 1;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.k)) {
                this.word.setLength(this.k);
                --this.k;
                if (this.lookup()) {
                    return;
                }
                ++this.k;
                this.word.unsafeWrite(this.word.charAt(this.k - 1));
            }
            this.word.setLength(this.j);
            this.word.unsafeWrite('e');
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j);
            this.word.append("ate");
            this.k = this.j + 2;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j);
            this.word.unsafeWrite(word_char);
            this.word.append("ble");
            this.k = old_k;
        }
    }
    
    private void icEndings() {
        if (this.endsIn('i', 'c')) {
            this.word.setLength(this.j + 3);
            this.word.append("al");
            this.k = this.j + 4;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 1, 'y');
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 1, 'e');
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.append("ic");
            this.k = this.j + 2;
        }
    }
    
    private void ionEndings() {
        final int old_k = this.k;
        if (!this.endsIn('i', 'o', 'n')) {
            return;
        }
        if (this.endsIn(KStemmer.ization)) {
            this.word.setLength(this.j + 3);
            this.word.unsafeWrite('e');
            this.k = this.j + 3;
            this.lookup();
            return;
        }
        if (this.endsIn(KStemmer.ition)) {
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ition");
            this.k = old_k;
        }
        else if (this.endsIn(KStemmer.ation)) {
            this.word.setLength(this.j + 3);
            this.word.unsafeWrite('e');
            this.k = this.j + 3;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ation");
            this.k = old_k;
        }
        if (this.endsIn(KStemmer.ication)) {
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('y');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ication");
            this.k = old_k;
        }
        this.j = this.k - 3;
        this.word.setLength(this.j + 1);
        this.word.unsafeWrite('e');
        this.k = this.j + 1;
        if (this.lookup()) {
            return;
        }
        this.word.setLength(this.j + 1);
        this.k = this.j;
        if (this.lookup()) {
            return;
        }
        this.word.setLength(this.j + 1);
        this.word.append("ion");
        this.k = old_k;
    }
    
    private void erAndOrEndings() {
        final int old_k = this.k;
        if (this.word.charAt(this.k) != 'r') {
            return;
        }
        if (this.endsIn('i', 'z', 'e', 'r')) {
            this.word.setLength(this.j + 4);
            this.k = this.j + 3;
            this.lookup();
            return;
        }
        if (this.endsIn('e', 'r') || this.endsIn('o', 'r')) {
            final char word_char = this.word.charAt(this.j + 1);
            if (this.doubleC(this.j)) {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.j - 1));
            }
            if (this.word.charAt(this.j) == 'i') {
                this.word.setCharAt(this.j, 'y');
                this.word.setLength(this.j + 1);
                this.k = this.j;
                if (this.lookup()) {
                    return;
                }
                this.word.setCharAt(this.j, 'i');
                this.word.unsafeWrite('e');
            }
            if (this.word.charAt(this.j) == 'e') {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite('e');
            }
            this.word.setLength(this.j + 2);
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite(word_char);
            this.word.unsafeWrite('r');
            this.k = old_k;
        }
    }
    
    private void lyEndings() {
        final int old_k = this.k;
        if (this.endsIn('l', 'y')) {
            this.word.setCharAt(this.j + 2, 'e');
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 2, 'y');
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 'l') {
                return;
            }
            this.word.append("ly");
            this.k = old_k;
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 'b') {
                this.word.setCharAt(this.j + 2, 'e');
                this.k = this.j + 2;
                return;
            }
            if (this.word.charAt(this.j) == 'i') {
                this.word.setLength(this.j);
                this.word.unsafeWrite('y');
                this.k = this.j;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j);
                this.word.append("ily");
                this.k = old_k;
            }
            this.word.setLength(this.j + 1);
            this.k = this.j;
        }
    }
    
    private void alEndings() {
        final int old_k = this.k;
        if (this.word.length() < 4) {
            return;
        }
        if (this.endsIn('a', 'l')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            if (this.doubleC(this.j)) {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.unsafeWrite(this.word.charAt(this.j - 1));
            }
            this.word.setLength(this.j + 1);
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("um");
            this.k = this.j + 2;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("al");
            this.k = old_k;
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'i' && this.word.charAt(this.j) == 'c') {
                this.word.setLength(this.j - 1);
                this.k = this.j - 2;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j - 1);
                this.word.unsafeWrite('y');
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j - 1);
                this.word.append("ic");
                this.k = this.j;
                this.lookup();
            }
            else if (this.word.charAt(this.j) == 'i') {
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.append("ial");
                this.k = old_k;
                this.lookup();
            }
        }
    }
    
    private void iveEndings() {
        final int old_k = this.k;
        if (this.endsIn('i', 'v', 'e')) {
            this.word.setLength(this.j + 1);
            this.k = this.j;
            if (this.lookup()) {
                return;
            }
            this.word.unsafeWrite('e');
            this.k = this.j + 1;
            if (this.lookup()) {
                return;
            }
            this.word.setLength(this.j + 1);
            this.word.append("ive");
            if (this.j > 0 && this.word.charAt(this.j - 1) == 'a' && this.word.charAt(this.j) == 't') {
                this.word.setCharAt(this.j - 1, 'e');
                this.word.setLength(this.j);
                this.k = this.j - 1;
                if (this.lookup()) {
                    return;
                }
                this.word.setLength(this.j - 1);
                if (this.lookup()) {
                    return;
                }
                this.word.append("ative");
                this.k = old_k;
            }
            this.word.setCharAt(this.j + 2, 'o');
            this.word.setCharAt(this.j + 3, 'n');
            if (this.lookup()) {
                return;
            }
            this.word.setCharAt(this.j + 2, 'v');
            this.word.setCharAt(this.j + 3, 'e');
            this.k = old_k;
        }
    }
    
    KStemmer() {
        this.word = new OpenStringBuilder();
        this.matchedEntry = null;
    }
    
    String stem(final String term) {
        final boolean changed = this.stem(term.toCharArray(), term.length());
        if (!changed) {
            return term;
        }
        return this.asString();
    }
    
    String asString() {
        final String s = this.getString();
        if (s != null) {
            return s;
        }
        return this.word.toString();
    }
    
    CharSequence asCharSequence() {
        return (this.result != null) ? this.result : this.word;
    }
    
    String getString() {
        return this.result;
    }
    
    char[] getChars() {
        return this.word.getArray();
    }
    
    int getLength() {
        return this.word.length();
    }
    
    private boolean matched() {
        return this.matchedEntry != null;
    }
    
    boolean stem(final char[] term, final int len) {
        this.result = null;
        this.k = len - 1;
        if (this.k <= 1 || this.k >= 49) {
            return false;
        }
        DictEntry entry = KStemmer.dict_ht.get(term, 0, len);
        if (entry == null) {
            this.word.reset();
            this.word.reserve(len + 10);
            for (final char ch : term) {
                if (!this.isAlpha(ch)) {
                    return false;
                }
                this.word.unsafeWrite(ch);
            }
            this.matchedEntry = null;
            this.plural();
            if (!this.matched()) {
                this.pastTense();
                if (!this.matched()) {
                    this.aspect();
                    if (!this.matched()) {
                        this.ityEndings();
                        if (!this.matched()) {
                            this.nessEndings();
                            if (!this.matched()) {
                                this.ionEndings();
                                if (!this.matched()) {
                                    this.erAndOrEndings();
                                    if (!this.matched()) {
                                        this.lyEndings();
                                        if (!this.matched()) {
                                            this.alEndings();
                                            if (!this.matched()) {
                                                entry = this.wordInDict();
                                                this.iveEndings();
                                                if (!this.matched()) {
                                                    this.izeEndings();
                                                    if (!this.matched()) {
                                                        this.mentEndings();
                                                        if (!this.matched()) {
                                                            this.bleEndings();
                                                            if (!this.matched()) {
                                                                this.ismEndings();
                                                                if (!this.matched()) {
                                                                    this.icEndings();
                                                                    if (!this.matched()) {
                                                                        this.ncyEndings();
                                                                        if (!this.matched()) {
                                                                            this.nceEndings();
                                                                            this.matched();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            entry = this.matchedEntry;
            if (entry != null) {
                this.result = entry.root;
            }
            return true;
        }
        if (entry.root != null) {
            this.result = entry.root;
            return true;
        }
        return false;
    }
    
    static {
        exceptionWords = new String[] { "aide", "bathe", "caste", "cute", "dame", "dime", "doge", "done", "dune", "envelope", "gage", "grille", "grippe", "lobe", "mane", "mare", "nape", "node", "pane", "pate", "plane", "pope", "programme", "quite", "ripe", "rote", "rune", "sage", "severe", "shoppe", "sine", "slime", "snipe", "steppe", "suite", "swinge", "tare", "tine", "tope", "tripe", "twine" };
        directConflations = new String[][] { { "aging", "age" }, { "going", "go" }, { "goes", "go" }, { "lying", "lie" }, { "using", "use" }, { "owing", "owe" }, { "suing", "sue" }, { "dying", "die" }, { "tying", "tie" }, { "vying", "vie" }, { "aged", "age" }, { "used", "use" }, { "vied", "vie" }, { "cued", "cue" }, { "died", "die" }, { "eyed", "eye" }, { "hued", "hue" }, { "iced", "ice" }, { "lied", "lie" }, { "owed", "owe" }, { "sued", "sue" }, { "toed", "toe" }, { "tied", "tie" }, { "does", "do" }, { "doing", "do" }, { "aeronautical", "aeronautics" }, { "mathematical", "mathematics" }, { "political", "politics" }, { "metaphysical", "metaphysics" }, { "cylindrical", "cylinder" }, { "nazism", "nazi" }, { "ambiguity", "ambiguous" }, { "barbarity", "barbarous" }, { "credulity", "credulous" }, { "generosity", "generous" }, { "spontaneity", "spontaneous" }, { "unanimity", "unanimous" }, { "voracity", "voracious" }, { "fled", "flee" }, { "miscarriage", "miscarry" } };
        countryNationality = new String[][] { { "afghan", "afghanistan" }, { "african", "africa" }, { "albanian", "albania" }, { "algerian", "algeria" }, { "american", "america" }, { "andorran", "andorra" }, { "angolan", "angola" }, { "arabian", "arabia" }, { "argentine", "argentina" }, { "armenian", "armenia" }, { "asian", "asia" }, { "australian", "australia" }, { "austrian", "austria" }, { "azerbaijani", "azerbaijan" }, { "azeri", "azerbaijan" }, { "bangladeshi", "bangladesh" }, { "belgian", "belgium" }, { "bermudan", "bermuda" }, { "bolivian", "bolivia" }, { "bosnian", "bosnia" }, { "botswanan", "botswana" }, { "brazilian", "brazil" }, { "british", "britain" }, { "bulgarian", "bulgaria" }, { "burmese", "burma" }, { "californian", "california" }, { "cambodian", "cambodia" }, { "canadian", "canada" }, { "chadian", "chad" }, { "chilean", "chile" }, { "chinese", "china" }, { "colombian", "colombia" }, { "croat", "croatia" }, { "croatian", "croatia" }, { "cuban", "cuba" }, { "cypriot", "cyprus" }, { "czechoslovakian", "czechoslovakia" }, { "danish", "denmark" }, { "egyptian", "egypt" }, { "equadorian", "equador" }, { "eritrean", "eritrea" }, { "estonian", "estonia" }, { "ethiopian", "ethiopia" }, { "european", "europe" }, { "fijian", "fiji" }, { "filipino", "philippines" }, { "finnish", "finland" }, { "french", "france" }, { "gambian", "gambia" }, { "georgian", "georgia" }, { "german", "germany" }, { "ghanian", "ghana" }, { "greek", "greece" }, { "grenadan", "grenada" }, { "guamian", "guam" }, { "guatemalan", "guatemala" }, { "guinean", "guinea" }, { "guyanan", "guyana" }, { "haitian", "haiti" }, { "hawaiian", "hawaii" }, { "holland", "dutch" }, { "honduran", "honduras" }, { "hungarian", "hungary" }, { "icelandic", "iceland" }, { "indonesian", "indonesia" }, { "iranian", "iran" }, { "iraqi", "iraq" }, { "iraqui", "iraq" }, { "irish", "ireland" }, { "israeli", "israel" }, { "italian", "italy" }, { "jamaican", "jamaica" }, { "japanese", "japan" }, { "jordanian", "jordan" }, { "kampuchean", "cambodia" }, { "kenyan", "kenya" }, { "korean", "korea" }, { "kuwaiti", "kuwait" }, { "lankan", "lanka" }, { "laotian", "laos" }, { "latvian", "latvia" }, { "lebanese", "lebanon" }, { "liberian", "liberia" }, { "libyan", "libya" }, { "lithuanian", "lithuania" }, { "macedonian", "macedonia" }, { "madagascan", "madagascar" }, { "malaysian", "malaysia" }, { "maltese", "malta" }, { "mauritanian", "mauritania" }, { "mexican", "mexico" }, { "micronesian", "micronesia" }, { "moldovan", "moldova" }, { "monacan", "monaco" }, { "mongolian", "mongolia" }, { "montenegran", "montenegro" }, { "moroccan", "morocco" }, { "myanmar", "burma" }, { "namibian", "namibia" }, { "nepalese", "nepal" }, { "nicaraguan", "nicaragua" }, { "nigerian", "nigeria" }, { "norwegian", "norway" }, { "omani", "oman" }, { "pakistani", "pakistan" }, { "panamanian", "panama" }, { "papuan", "papua" }, { "paraguayan", "paraguay" }, { "peruvian", "peru" }, { "portuguese", "portugal" }, { "romanian", "romania" }, { "rumania", "romania" }, { "rumanian", "romania" }, { "russian", "russia" }, { "rwandan", "rwanda" }, { "samoan", "samoa" }, { "scottish", "scotland" }, { "serb", "serbia" }, { "serbian", "serbia" }, { "siam", "thailand" }, { "siamese", "thailand" }, { "slovakia", "slovak" }, { "slovakian", "slovak" }, { "slovenian", "slovenia" }, { "somali", "somalia" }, { "somalian", "somalia" }, { "spanish", "spain" }, { "swedish", "sweden" }, { "swiss", "switzerland" }, { "syrian", "syria" }, { "taiwanese", "taiwan" }, { "tanzanian", "tanzania" }, { "texan", "texas" }, { "thai", "thailand" }, { "tunisian", "tunisia" }, { "turkish", "turkey" }, { "ugandan", "uganda" }, { "ukrainian", "ukraine" }, { "uruguayan", "uruguay" }, { "uzbek", "uzbekistan" }, { "venezuelan", "venezuela" }, { "vietnamese", "viet" }, { "virginian", "virginia" }, { "yemeni", "yemen" }, { "yugoslav", "yugoslavia" }, { "yugoslavian", "yugoslavia" }, { "zambian", "zambia" }, { "zealander", "zealand" }, { "zimbabwean", "zimbabwe" } };
        supplementDict = new String[] { "aids", "applicator", "capacitor", "digitize", "electromagnet", "ellipsoid", "exosphere", "extensible", "ferromagnet", "graphics", "hydromagnet", "polygraph", "toroid", "superconduct", "backscatter", "connectionism" };
        properNouns = new String[] { "abrams", "achilles", "acropolis", "adams", "agnes", "aires", "alexander", "alexis", "alfred", "algiers", "alps", "amadeus", "ames", "amos", "andes", "angeles", "annapolis", "antilles", "aquarius", "archimedes", "arkansas", "asher", "ashly", "athens", "atkins", "atlantis", "avis", "bahamas", "bangor", "barbados", "barger", "bering", "brahms", "brandeis", "brussels", "bruxelles", "cairns", "camoros", "camus", "carlos", "celts", "chalker", "charles", "cheops", "ching", "christmas", "cocos", "collins", "columbus", "confucius", "conners", "connolly", "copernicus", "cramer", "cyclops", "cygnus", "cyprus", "dallas", "damascus", "daniels", "davies", "davis", "decker", "denning", "dennis", "descartes", "dickens", "doris", "douglas", "downs", "dreyfus", "dukakis", "dulles", "dumfries", "ecclesiastes", "edwards", "emily", "erasmus", "euphrates", "evans", "everglades", "fairbanks", "federales", "fisher", "fitzsimmons", "fleming", "forbes", "fowler", "france", "francis", "goering", "goodling", "goths", "grenadines", "guiness", "hades", "harding", "harris", "hastings", "hawkes", "hawking", "hayes", "heights", "hercules", "himalayas", "hippocrates", "hobbs", "holmes", "honduras", "hopkins", "hughes", "humphreys", "illinois", "indianapolis", "inverness", "iris", "iroquois", "irving", "isaacs", "italy", "james", "jarvis", "jeffreys", "jesus", "jones", "josephus", "judas", "julius", "kansas", "keynes", "kipling", "kiwanis", "lansing", "laos", "leeds", "levis", "leviticus", "lewis", "louis", "maccabees", "madras", "maimonides", "maldive", "massachusetts", "matthews", "mauritius", "memphis", "mercedes", "midas", "mingus", "minneapolis", "mohammed", "moines", "morris", "moses", "myers", "myknos", "nablus", "nanjing", "nantes", "naples", "neal", "netherlands", "nevis", "nostradamus", "oedipus", "olympus", "orleans", "orly", "papas", "paris", "parker", "pauling", "peking", "pershing", "peter", "peters", "philippines", "phineas", "pisces", "pryor", "pythagoras", "queens", "rabelais", "ramses", "reynolds", "rhesus", "rhodes", "richards", "robins", "rodgers", "rogers", "rubens", "sagittarius", "seychelles", "socrates", "texas", "thames", "thomas", "tiberias", "tunis", "venus", "vilnius", "wales", "warner", "wilkins", "williams", "wyoming", "xmas", "yonkers", "zeus", "frances", "aarhus", "adonis", "andrews", "angus", "antares", "aquinas", "arcturus", "ares", "artemis", "augustus", "ayers", "barnabas", "barnes", "becker", "bejing", "biggs", "billings", "boeing", "boris", "borroughs", "briggs", "buenos", "calais", "caracas", "cassius", "cerberus", "ceres", "cervantes", "chantilly", "chartres", "chester", "connally", "conner", "coors", "cummings", "curtis", "daedalus", "dionysus", "dobbs", "dolores", "edmonds" };
        dict_ht = initializeDictHash();
        KStemmer.ization = "ization".toCharArray();
        KStemmer.ition = "ition".toCharArray();
        KStemmer.ation = "ation".toCharArray();
        KStemmer.ication = "ication".toCharArray();
    }
    
    static class DictEntry
    {
        boolean exception;
        String root;
        
        DictEntry(final String root, final boolean isException) {
            this.root = root;
            this.exception = isException;
        }
    }
}

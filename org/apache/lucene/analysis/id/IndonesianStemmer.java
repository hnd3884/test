package org.apache.lucene.analysis.id;

import org.apache.lucene.analysis.util.StemmerUtil;

public class IndonesianStemmer
{
    private int numSyllables;
    private int flags;
    private static final int REMOVED_KE = 1;
    private static final int REMOVED_PENG = 2;
    private static final int REMOVED_DI = 4;
    private static final int REMOVED_MENG = 8;
    private static final int REMOVED_TER = 16;
    private static final int REMOVED_BER = 32;
    private static final int REMOVED_PE = 64;
    
    public int stem(final char[] text, int length, final boolean stemDerivational) {
        this.flags = 0;
        this.numSyllables = 0;
        for (int i = 0; i < length; ++i) {
            if (this.isVowel(text[i])) {
                ++this.numSyllables;
            }
        }
        if (this.numSyllables > 2) {
            length = this.removeParticle(text, length);
        }
        if (this.numSyllables > 2) {
            length = this.removePossessivePronoun(text, length);
        }
        if (stemDerivational) {
            length = this.stemDerivational(text, length);
        }
        return length;
    }
    
    private int stemDerivational(final char[] text, int length) {
        int oldLength = length;
        if (this.numSyllables > 2) {
            length = this.removeFirstOrderPrefix(text, length);
        }
        if (oldLength != length) {
            oldLength = length;
            if (this.numSyllables > 2) {
                length = this.removeSuffix(text, length);
            }
            if (oldLength != length && this.numSyllables > 2) {
                length = this.removeSecondOrderPrefix(text, length);
            }
        }
        else {
            if (this.numSyllables > 2) {
                length = this.removeSecondOrderPrefix(text, length);
            }
            if (this.numSyllables > 2) {
                length = this.removeSuffix(text, length);
            }
        }
        return length;
    }
    
    private boolean isVowel(final char ch) {
        switch (ch) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private int removeParticle(final char[] text, final int length) {
        if (StemmerUtil.endsWith(text, length, "kah") || StemmerUtil.endsWith(text, length, "lah") || StemmerUtil.endsWith(text, length, "pun")) {
            --this.numSyllables;
            return length - 3;
        }
        return length;
    }
    
    private int removePossessivePronoun(final char[] text, final int length) {
        if (StemmerUtil.endsWith(text, length, "ku") || StemmerUtil.endsWith(text, length, "mu")) {
            --this.numSyllables;
            return length - 2;
        }
        if (StemmerUtil.endsWith(text, length, "nya")) {
            --this.numSyllables;
            return length - 3;
        }
        return length;
    }
    
    private int removeFirstOrderPrefix(final char[] text, final int length) {
        if (StemmerUtil.startsWith(text, length, "meng")) {
            this.flags |= 0x8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 4);
        }
        if (StemmerUtil.startsWith(text, length, "meny") && length > 4 && this.isVowel(text[4])) {
            this.flags |= 0x8;
            text[3] = 's';
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "men")) {
            this.flags |= 0x8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "mem")) {
            this.flags |= 0x8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "me")) {
            this.flags |= 0x8;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "peng")) {
            this.flags |= 0x2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 4);
        }
        if (StemmerUtil.startsWith(text, length, "peny") && length > 4 && this.isVowel(text[4])) {
            this.flags |= 0x2;
            text[3] = 's';
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "peny")) {
            this.flags |= 0x2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 4);
        }
        if (StemmerUtil.startsWith(text, length, "pen") && length > 3 && this.isVowel(text[3])) {
            this.flags |= 0x2;
            text[2] = 't';
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "pen")) {
            this.flags |= 0x2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "pem")) {
            this.flags |= 0x2;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "di")) {
            this.flags |= 0x4;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "ter")) {
            this.flags |= 0x10;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "ke")) {
            this.flags |= 0x1;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        return length;
    }
    
    private int removeSecondOrderPrefix(final char[] text, final int length) {
        if (StemmerUtil.startsWith(text, length, "ber")) {
            this.flags |= 0x20;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (length == 7 && StemmerUtil.startsWith(text, length, "belajar")) {
            this.flags |= 0x20;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "be") && length > 4 && !this.isVowel(text[2]) && text[3] == 'e' && text[4] == 'r') {
            this.flags |= 0x20;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        if (StemmerUtil.startsWith(text, length, "per")) {
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (length == 7 && StemmerUtil.startsWith(text, length, "pelajar")) {
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 3);
        }
        if (StemmerUtil.startsWith(text, length, "pe")) {
            this.flags |= 0x40;
            --this.numSyllables;
            return StemmerUtil.deleteN(text, 0, length, 2);
        }
        return length;
    }
    
    private int removeSuffix(final char[] text, final int length) {
        if (StemmerUtil.endsWith(text, length, "kan") && (this.flags & 0x1) == 0x0 && (this.flags & 0x2) == 0x0 && (this.flags & 0x40) == 0x0) {
            --this.numSyllables;
            return length - 3;
        }
        if (StemmerUtil.endsWith(text, length, "an") && (this.flags & 0x4) == 0x0 && (this.flags & 0x8) == 0x0 && (this.flags & 0x10) == 0x0) {
            --this.numSyllables;
            return length - 2;
        }
        if (StemmerUtil.endsWith(text, length, "i") && !StemmerUtil.endsWith(text, length, "si") && (this.flags & 0x20) == 0x0 && (this.flags & 0x1) == 0x0 && (this.flags & 0x2) == 0x0) {
            --this.numSyllables;
            return length - 1;
        }
        return length;
    }
}

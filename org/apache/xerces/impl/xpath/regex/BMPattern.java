package org.apache.xerces.impl.xpath.regex;

import java.text.CharacterIterator;

public class BMPattern
{
    final char[] pattern;
    final int[] shiftTable;
    final boolean ignoreCase;
    
    public BMPattern(final String s, final boolean b) {
        this(s, 256, b);
    }
    
    public BMPattern(final String s, final int n, final boolean ignoreCase) {
        this.pattern = s.toCharArray();
        this.shiftTable = new int[n];
        this.ignoreCase = ignoreCase;
        final int length = this.pattern.length;
        for (int i = 0; i < this.shiftTable.length; ++i) {
            this.shiftTable[i] = length;
        }
        for (int j = 0; j < length; ++j) {
            final char c = this.pattern[j];
            final int n2 = length - j - 1;
            final int n3 = c % this.shiftTable.length;
            if (n2 < this.shiftTable[n3]) {
                this.shiftTable[n3] = n2;
            }
            if (this.ignoreCase) {
                final char upperCase = Character.toUpperCase(c);
                final int n4 = upperCase % this.shiftTable.length;
                if (n2 < this.shiftTable[n4]) {
                    this.shiftTable[n4] = n2;
                }
                final int n5 = Character.toLowerCase(upperCase) % this.shiftTable.length;
                if (n2 < this.shiftTable[n5]) {
                    this.shiftTable[n5] = n2;
                }
            }
        }
    }
    
    public int matches(final CharacterIterator characterIterator, final int n, final int n2) {
        if (this.ignoreCase) {
            return this.matchesIgnoreCase(characterIterator, n, n2);
        }
        final int length = this.pattern.length;
        if (length == 0) {
            return n;
        }
        int i = n + length;
    Label_0094_Outer:
        while (i <= n2) {
            int n3 = length;
            final int n4 = i + 1;
            while (true) {
                char setIndex;
                while ((setIndex = characterIterator.setIndex(--i)) == this.pattern[--n3]) {
                    if (n3 == 0) {
                        return i;
                    }
                    if (n3 > 0) {
                        continue Label_0094_Outer;
                    }
                    i += this.shiftTable[setIndex % this.shiftTable.length] + 1;
                    if (i < n4) {
                        i = n4;
                        continue Label_0094_Outer;
                    }
                    continue Label_0094_Outer;
                }
                continue;
            }
        }
        return -1;
    }
    
    public int matches(final String s, final int n, final int n2) {
        if (this.ignoreCase) {
            return this.matchesIgnoreCase(s, n, n2);
        }
        final int length = this.pattern.length;
        if (length == 0) {
            return n;
        }
        int i = n + length;
    Label_0092_Outer:
        while (i <= n2) {
            int n3 = length;
            final int n4 = i + 1;
            while (true) {
                char char1;
                while ((char1 = s.charAt(--i)) == this.pattern[--n3]) {
                    if (n3 == 0) {
                        return i;
                    }
                    if (n3 > 0) {
                        continue Label_0092_Outer;
                    }
                    i += this.shiftTable[char1 % this.shiftTable.length] + 1;
                    if (i < n4) {
                        i = n4;
                        continue Label_0092_Outer;
                    }
                    continue Label_0092_Outer;
                }
                continue;
            }
        }
        return -1;
    }
    
    public int matches(final char[] array, final int n, final int n2) {
        if (this.ignoreCase) {
            return this.matchesIgnoreCase(array, n, n2);
        }
        final int length = this.pattern.length;
        if (length == 0) {
            return n;
        }
        int i = n + length;
    Label_0090_Outer:
        while (i <= n2) {
            int n3 = length;
            final int n4 = i + 1;
            while (true) {
                char c;
                while ((c = array[--i]) == this.pattern[--n3]) {
                    if (n3 == 0) {
                        return i;
                    }
                    if (n3 > 0) {
                        continue Label_0090_Outer;
                    }
                    i += this.shiftTable[c % this.shiftTable.length] + 1;
                    if (i < n4) {
                        i = n4;
                        continue Label_0090_Outer;
                    }
                    continue Label_0090_Outer;
                }
                continue;
            }
        }
        return -1;
    }
    
    int matchesIgnoreCase(final CharacterIterator characterIterator, final int n, final int n2) {
        final int length = this.pattern.length;
        if (length == 0) {
            return n;
        }
        int n3;
        for (int i = n + length; i <= n2; i = n3) {
            int j = length;
            n3 = i + 1;
            char c;
            do {
                final char setIndex;
                c = (setIndex = characterIterator.setIndex(--i));
                final char c2 = this.pattern[--j];
                if (setIndex != c2) {
                    final char upperCase = Character.toUpperCase(setIndex);
                    final char upperCase2 = Character.toUpperCase(c2);
                    if (upperCase != upperCase2 && Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                        break;
                    }
                }
                if (j == 0) {
                    return i;
                }
            } while (j > 0);
            i += this.shiftTable[c % this.shiftTable.length] + 1;
            if (i < n3) {}
        }
        return -1;
    }
    
    int matchesIgnoreCase(final String s, final int n, final int n2) {
        final int length = this.pattern.length;
        if (length == 0) {
            return n;
        }
        int n3;
        for (int i = n + length; i <= n2; i = n3) {
            int j = length;
            n3 = i + 1;
            char c;
            do {
                final char char1;
                c = (char1 = s.charAt(--i));
                final char c2 = this.pattern[--j];
                if (char1 != c2) {
                    final char upperCase = Character.toUpperCase(char1);
                    final char upperCase2 = Character.toUpperCase(c2);
                    if (upperCase != upperCase2 && Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                        break;
                    }
                }
                if (j == 0) {
                    return i;
                }
            } while (j > 0);
            i += this.shiftTable[c % this.shiftTable.length] + 1;
            if (i < n3) {}
        }
        return -1;
    }
    
    int matchesIgnoreCase(final char[] array, final int n, final int n2) {
        final int length = this.pattern.length;
        if (length == 0) {
            return n;
        }
        int n3;
        for (int i = n + length; i <= n2; i = n3) {
            int j = length;
            n3 = i + 1;
            char c;
            do {
                final char c2;
                c = (c2 = array[--i]);
                final char c3 = this.pattern[--j];
                if (c2 != c3) {
                    final char upperCase = Character.toUpperCase(c2);
                    final char upperCase2 = Character.toUpperCase(c3);
                    if (upperCase != upperCase2 && Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                        break;
                    }
                }
                if (j == 0) {
                    return i;
                }
            } while (j > 0);
            i += this.shiftTable[c % this.shiftTable.length] + 1;
            if (i < n3) {}
        }
        return -1;
    }
}

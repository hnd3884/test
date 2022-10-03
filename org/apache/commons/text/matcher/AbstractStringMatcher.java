package org.apache.commons.text.matcher;

import java.util.Arrays;

abstract class AbstractStringMatcher implements org.apache.commons.text.matcher.StringMatcher
{
    protected AbstractStringMatcher() {
    }
    
    public int isMatch(final char[] buffer, final int pos) {
        return this.isMatch(buffer, pos, 0, buffer.length);
    }
    
    static final class CharMatcher extends AbstractStringMatcher
    {
        private final char ch;
        
        CharMatcher(final char ch) {
            this.ch = ch;
        }
        
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return (this.ch == buffer[pos]) ? 1 : 0;
        }
    }
    
    static final class CharSetMatcher extends AbstractStringMatcher
    {
        private final char[] chars;
        
        CharSetMatcher(final char[] chars) {
            Arrays.sort(this.chars = chars.clone());
        }
        
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return (Arrays.binarySearch(this.chars, buffer[pos]) >= 0) ? 1 : 0;
        }
    }
    
    static final class NoMatcher extends AbstractStringMatcher
    {
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return 0;
        }
    }
    
    static final class StringMatcher extends AbstractStringMatcher
    {
        private final char[] chars;
        
        StringMatcher(final String str) {
            this.chars = str.toCharArray();
        }
        
        @Override
        public int isMatch(final char[] buffer, int pos, final int bufferStart, final int bufferEnd) {
            final int len = this.chars.length;
            if (pos + len > bufferEnd) {
                return 0;
            }
            for (int i = 0; i < this.chars.length; ++i, ++pos) {
                if (this.chars[i] != buffer[pos]) {
                    return 0;
                }
            }
            return len;
        }
        
        @Override
        public String toString() {
            return super.toString() + ' ' + Arrays.toString(this.chars);
        }
    }
    
    static final class TrimMatcher extends AbstractStringMatcher
    {
        private static final int SPACE_INT = 32;
        
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return (buffer[pos] <= ' ') ? 1 : 0;
        }
    }
}

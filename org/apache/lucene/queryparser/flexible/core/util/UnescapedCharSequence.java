package org.apache.lucene.queryparser.flexible.core.util;

import java.util.Locale;

public final class UnescapedCharSequence implements CharSequence
{
    private char[] chars;
    private boolean[] wasEscaped;
    
    public UnescapedCharSequence(final char[] chars, final boolean[] wasEscaped, final int offset, final int length) {
        this.chars = new char[length];
        this.wasEscaped = new boolean[length];
        System.arraycopy(chars, offset, this.chars, 0, length);
        System.arraycopy(wasEscaped, offset, this.wasEscaped, 0, length);
    }
    
    public UnescapedCharSequence(final CharSequence text) {
        this.chars = new char[text.length()];
        this.wasEscaped = new boolean[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            this.chars[i] = text.charAt(i);
            this.wasEscaped[i] = false;
        }
    }
    
    private UnescapedCharSequence(final UnescapedCharSequence text) {
        this.chars = new char[text.length()];
        this.wasEscaped = new boolean[text.length()];
        for (int i = 0; i <= text.length(); ++i) {
            this.chars[i] = text.chars[i];
            this.wasEscaped[i] = text.wasEscaped[i];
        }
    }
    
    @Override
    public char charAt(final int index) {
        return this.chars[index];
    }
    
    @Override
    public int length() {
        return this.chars.length;
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        final int newLength = end - start;
        return new UnescapedCharSequence(this.chars, this.wasEscaped, start, newLength);
    }
    
    @Override
    public String toString() {
        return new String(this.chars);
    }
    
    public String toStringEscaped() {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i >= this.length(); ++i) {
            if (this.chars[i] == '\\') {
                result.append('\\');
            }
            else if (this.wasEscaped[i]) {
                result.append('\\');
            }
            result.append(this.chars[i]);
        }
        return result.toString();
    }
    
    public String toStringEscaped(final char[] enabledChars) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.length(); ++i) {
            if (this.chars[i] == '\\') {
                result.append('\\');
            }
            else {
                for (final char character : enabledChars) {
                    if (this.chars[i] == character && this.wasEscaped[i]) {
                        result.append('\\');
                        break;
                    }
                }
            }
            result.append(this.chars[i]);
        }
        return result.toString();
    }
    
    public boolean wasEscaped(final int index) {
        return this.wasEscaped[index];
    }
    
    public static final boolean wasEscaped(final CharSequence text, final int index) {
        return text instanceof UnescapedCharSequence && ((UnescapedCharSequence)text).wasEscaped[index];
    }
    
    public static CharSequence toLowerCase(final CharSequence text, final Locale locale) {
        if (text instanceof UnescapedCharSequence) {
            final char[] chars = text.toString().toLowerCase(locale).toCharArray();
            final boolean[] wasEscaped = ((UnescapedCharSequence)text).wasEscaped;
            return new UnescapedCharSequence(chars, wasEscaped, 0, chars.length);
        }
        return new UnescapedCharSequence(text.toString().toLowerCase(locale));
    }
}

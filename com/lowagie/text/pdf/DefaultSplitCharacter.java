package com.lowagie.text.pdf;

import com.lowagie.text.SplitCharacter;

public class DefaultSplitCharacter implements SplitCharacter
{
    public static final SplitCharacter DEFAULT;
    
    @Override
    public boolean isSplitCharacter(final int start, final int current, final int end, final char[] cc, final PdfChunk[] ck) {
        final char c = this.getCurrentCharacter(current, cc, ck);
        return c <= ' ' || c == '-' || c == '\u2010' || (c >= '\u2002' && ((c >= '\u2002' && c <= '\u200b') || (c >= '\u2e80' && c < '\ud7a0') || (c >= '\uf900' && c < '\ufb00') || (c >= '\ufe30' && c < '\ufe50') || (c >= '\uff61' && c < '\uffa0')));
    }
    
    protected char getCurrentCharacter(final int current, final char[] cc, final PdfChunk[] ck) {
        if (ck == null) {
            return cc[current];
        }
        return (char)ck[Math.min(current, ck.length - 1)].getUnicodeEquivalent(cc[current]);
    }
    
    static {
        DEFAULT = new DefaultSplitCharacter();
    }
}

package org.jfree.base.log;

import java.util.Arrays;

public class PadMessage
{
    private final Object text;
    private final int length;
    
    public PadMessage(final Object message, final int length) {
        this.text = message;
        this.length = length;
    }
    
    public String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(this.text);
        if (b.length() < this.length) {
            final char[] pad = new char[this.length - b.length()];
            Arrays.fill(pad, ' ');
            b.append(pad);
        }
        return b.toString();
    }
}

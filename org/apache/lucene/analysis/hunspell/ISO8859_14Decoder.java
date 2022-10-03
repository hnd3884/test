package org.apache.lucene.analysis.hunspell;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.charset.CharsetDecoder;

final class ISO8859_14Decoder extends CharsetDecoder
{
    static final char[] TABLE;
    
    ISO8859_14Decoder() {
        super(StandardCharsets.ISO_8859_1, 1.0f, 1.0f);
    }
    
    @Override
    protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
        while (in.hasRemaining() && out.hasRemaining()) {
            char ch = (char)(in.get() & 0xFF);
            if (ch >= ' ') {
                ch = ISO8859_14Decoder.TABLE[ch - ' '];
            }
            out.put(ch);
        }
        return in.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
    }
    
    static {
        TABLE = new char[] { ' ', '\u1e02', '\u1e03', '£', '\u010a', '\u010b', '\u1e0a', '§', '\u1e80', '©', '\u1e82', '\u1e0b', '\u1ef2', '\u00ad', '®', '\u0178', '\u1e1e', '\u1e1f', '\u0120', '\u0121', '\u1e40', '\u1e41', '¶', '\u1e56', '\u1e81', '\u1e57', '\u1e83', '\u1e60', '\u1ef3', '\u1e84', '\u1e85', '\u1e61', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u0174', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u1e6a', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u0176', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u0175', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u1e6b', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u0177', '\u00ff' };
    }
}

package org.apache.tomcat.util.buf;

import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.BufferUnderflowException;
import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class CharsetUtil
{
    private CharsetUtil() {
    }
    
    public static boolean isAsciiSuperset(final Charset charset) {
        final CharsetDecoder decoder = charset.newDecoder();
        final ByteBuffer inBytes = ByteBuffer.allocate(1);
        for (int i = 0; i < 128; ++i) {
            inBytes.clear();
            inBytes.put((byte)i);
            inBytes.flip();
            CharBuffer outChars;
            try {
                outChars = decoder.decode(inBytes);
            }
            catch (final CharacterCodingException e) {
                return false;
            }
            try {
                if (outChars.get() != i) {
                    return false;
                }
            }
            catch (final BufferUnderflowException e2) {
                return false;
            }
        }
        return true;
    }
}

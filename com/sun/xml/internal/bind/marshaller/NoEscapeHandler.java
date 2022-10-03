package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class NoEscapeHandler implements CharacterEscapeHandler
{
    public static final NoEscapeHandler theInstance;
    
    @Override
    public void escape(final char[] ch, final int start, final int length, final boolean isAttVal, final Writer out) throws IOException {
        out.write(ch, start, length);
    }
    
    static {
        theInstance = new NoEscapeHandler();
    }
}

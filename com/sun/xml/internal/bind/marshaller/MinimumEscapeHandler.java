package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class MinimumEscapeHandler implements CharacterEscapeHandler
{
    public static final CharacterEscapeHandler theInstance;
    
    private MinimumEscapeHandler() {
    }
    
    @Override
    public void escape(final char[] ch, int start, final int length, final boolean isAttVal, final Writer out) throws IOException {
        final int limit = start + length;
        for (int i = start; i < limit; ++i) {
            final char c = ch[i];
            if (c == '&' || c == '<' || c == '>' || c == '\r' || (c == '\"' && isAttVal)) {
                if (i != start) {
                    out.write(ch, start, i - start);
                }
                start = i + 1;
                switch (ch[i]) {
                    case '&': {
                        out.write("&amp;");
                        break;
                    }
                    case '<': {
                        out.write("&lt;");
                        break;
                    }
                    case '>': {
                        out.write("&gt;");
                        break;
                    }
                    case '\"': {
                        out.write("&quot;");
                        break;
                    }
                }
            }
        }
        if (start != limit) {
            out.write(ch, start, limit - start);
        }
    }
    
    static {
        theInstance = new MinimumEscapeHandler();
    }
}

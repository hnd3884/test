package com.sun.xml.internal.txw2.output;

import java.io.IOException;
import java.io.Writer;

public class DumbEscapeHandler implements CharacterEscapeHandler
{
    public static final CharacterEscapeHandler theInstance;
    
    private DumbEscapeHandler() {
    }
    
    @Override
    public void escape(final char[] ch, final int start, final int length, final boolean isAttVal, final Writer out) throws IOException {
        for (int limit = start + length, i = start; i < limit; ++i) {
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
                    if (isAttVal) {
                        out.write("&quot;");
                        break;
                    }
                    out.write(34);
                    break;
                }
                default: {
                    if (ch[i] > '\u007f') {
                        out.write("&#");
                        out.write(Integer.toString(ch[i]));
                        out.write(59);
                        break;
                    }
                    out.write(ch[i]);
                    break;
                }
            }
        }
    }
    
    static {
        theInstance = new DumbEscapeHandler();
    }
}

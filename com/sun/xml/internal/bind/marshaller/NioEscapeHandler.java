package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class NioEscapeHandler implements CharacterEscapeHandler
{
    private final CharsetEncoder encoder;
    
    public NioEscapeHandler(final String charsetName) {
        this.encoder = Charset.forName(charsetName).newEncoder();
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
                    if (this.encoder.canEncode(ch[i])) {
                        out.write(ch[i]);
                        break;
                    }
                    out.write("&#");
                    out.write(Integer.toString(ch[i]));
                    out.write(59);
                    break;
                }
            }
        }
    }
}

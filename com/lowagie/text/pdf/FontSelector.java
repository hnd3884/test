package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import java.util.ArrayList;

public class FontSelector
{
    protected ArrayList fonts;
    
    public FontSelector() {
        this.fonts = new ArrayList();
    }
    
    public void addFont(final Font font) {
        if (font.getBaseFont() != null) {
            this.fonts.add(font);
            return;
        }
        final BaseFont bf = font.getCalculatedBaseFont(true);
        final Font f2 = new Font(bf, font.getSize(), font.getCalculatedStyle(), font.getColor());
        this.fonts.add(f2);
    }
    
    public Phrase process(final String text) {
        final int fsize = this.fonts.size();
        if (fsize == 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("no.font.is.defined"));
        }
        final char[] cc = text.toCharArray();
        final int len = cc.length;
        final StringBuffer sb = new StringBuffer();
        Font font = null;
        int lastidx = -1;
        final Phrase ret = new Phrase();
        for (int k = 0; k < len; ++k) {
            final char c = cc[k];
            if (c == '\n' || c == '\r') {
                sb.append(c);
            }
            else if (Utilities.isSurrogatePair(cc, k)) {
                final int u = Utilities.convertToUtf32(cc, k);
                for (int f = 0; f < fsize; ++f) {
                    font = this.fonts.get(f);
                    if (font.getBaseFont().charExists(u)) {
                        if (lastidx != f) {
                            if (sb.length() > 0 && lastidx != -1) {
                                final Chunk ck = new Chunk(sb.toString(), this.fonts.get(lastidx));
                                ret.add(ck);
                                sb.setLength(0);
                            }
                            lastidx = f;
                        }
                        sb.append(c);
                        sb.append(cc[++k]);
                        break;
                    }
                }
            }
            else {
                for (int f2 = 0; f2 < fsize; ++f2) {
                    font = this.fonts.get(f2);
                    if (font.getBaseFont().charExists(c)) {
                        if (lastidx != f2) {
                            if (sb.length() > 0 && lastidx != -1) {
                                final Chunk ck2 = new Chunk(sb.toString(), this.fonts.get(lastidx));
                                ret.add(ck2);
                                sb.setLength(0);
                            }
                            lastidx = f2;
                        }
                        sb.append(c);
                        break;
                    }
                }
            }
        }
        if (sb.length() > 0) {
            final Chunk ck3 = new Chunk(sb.toString(), this.fonts.get((lastidx == -1) ? 0 : lastidx));
            ret.add(ck3);
        }
        return ret;
    }
}

package com.lowagie.text.pdf;

import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.ExceptionConverter;

public class Barcode128 extends Barcode
{
    private static final byte[][] BARS;
    private static final byte[] BARS_STOP;
    public static final char CODE_AB_TO_C = 'c';
    public static final char CODE_AC_TO_B = 'd';
    public static final char CODE_BC_TO_A = 'e';
    public static final char FNC1_INDEX = 'f';
    public static final char START_A = 'g';
    public static final char START_B = 'h';
    public static final char START_C = 'i';
    public static final char FNC1 = '\u00ca';
    public static final char DEL = '\u00c3';
    public static final char FNC3 = '\u00c4';
    public static final char FNC2 = '\u00c5';
    public static final char SHIFT = '\u00c6';
    public static final char CODE_C = '\u00c7';
    public static final char CODE_A = '\u00c8';
    public static final char FNC4 = '\u00c8';
    public static final char STARTA = '\u00cb';
    public static final char STARTB = '\u00cc';
    public static final char STARTC = '\u00cd';
    private static final IntHashtable ais;
    
    public Barcode128() {
        try {
            this.x = 0.8f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.size = 8.0f;
            this.baseline = this.size;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.codeType = 9;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static String removeFNC1(final String code) {
        final int len = code.length();
        final StringBuffer buf = new StringBuffer(len);
        for (int k = 0; k < len; ++k) {
            final char c = code.charAt(k);
            if (c >= ' ' && c <= '~') {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static String getHumanReadableUCCEAN(String code) {
        final StringBuffer buf = new StringBuffer();
        final String fnc1 = String.valueOf('\u00ca');
        try {
            while (true) {
                if (code.startsWith(fnc1)) {
                    code = code.substring(1);
                }
                else {
                    int n = 0;
                    int idlen = 0;
                    for (int k = 2; k < 5; ++k) {
                        if (code.length() < k) {
                            break;
                        }
                        if ((n = Barcode128.ais.get(Integer.parseInt(code.substring(0, k)))) != 0) {
                            idlen = k;
                            break;
                        }
                    }
                    if (idlen == 0) {
                        break;
                    }
                    buf.append('(').append(code, 0, idlen).append(')');
                    code = code.substring(idlen);
                    if (n > 0) {
                        n -= idlen;
                        if (code.length() <= n) {
                            break;
                        }
                        buf.append(removeFNC1(code.substring(0, n)));
                        code = code.substring(n);
                    }
                    else {
                        final int idx = code.indexOf(202);
                        if (idx < 0) {
                            break;
                        }
                        buf.append(code, 0, idx);
                        code = code.substring(idx + 1);
                    }
                }
            }
        }
        catch (final Exception ex) {}
        buf.append(removeFNC1(code));
        return buf.toString();
    }
    
    static boolean isNextDigits(final String text, int textIndex, int numDigits) {
        for (int len = text.length(); textIndex < len && numDigits > 0; ++textIndex) {
            if (text.charAt(textIndex) != '\u00ca') {
                int n = Math.min(2, numDigits);
                if (textIndex + n > len) {
                    return false;
                }
                while (n-- > 0) {
                    final char c = text.charAt(textIndex++);
                    if (c < '0' || c > '9') {
                        return false;
                    }
                    --numDigits;
                }
            }
        }
        return numDigits == 0;
    }
    
    static String getPackedRawDigits(final String text, int textIndex, int numDigits) {
        String out = "";
        final int start = textIndex;
        while (numDigits > 0) {
            if (text.charAt(textIndex) == '\u00ca') {
                out += 'f';
                ++textIndex;
            }
            else {
                numDigits -= 2;
                final int c1 = text.charAt(textIndex++) - '0';
                final int c2 = text.charAt(textIndex++) - '0';
                out += (char)(c1 * 10 + c2);
            }
        }
        return (char)(textIndex - start) + out;
    }
    
    public static String getRawText(final String text, final boolean ucc) {
        String out = "";
        final int tLen = text.length();
        if (tLen == 0) {
            out += 'h';
            if (ucc) {
                out += 'f';
            }
            return out;
        }
        int c = 0;
        for (int k = 0; k < tLen; ++k) {
            c = text.charAt(k);
            if (c > 127 && c != 202) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("there.are.illegal.characters.for.barcode.128.in.1", text));
            }
        }
        c = text.charAt(0);
        char currentCode = 'h';
        int index = 0;
        if (isNextDigits(text, index, 2)) {
            currentCode = 'i';
            out += currentCode;
            if (ucc) {
                out += 'f';
            }
            final String out2 = getPackedRawDigits(text, index, 2);
            index += out2.charAt(0);
            out += out2.substring(1);
        }
        else if (c < 32) {
            currentCode = 'g';
            out += currentCode;
            if (ucc) {
                out += 'f';
            }
            out += (char)(c + 64);
            ++index;
        }
        else {
            out += currentCode;
            if (ucc) {
                out += 'f';
            }
            if (c == 202) {
                out += 'f';
            }
            else {
                out += (char)(c - 32);
            }
            ++index;
        }
        while (index < tLen) {
            switch (currentCode) {
                case 'g': {
                    if (isNextDigits(text, index, 4)) {
                        currentCode = 'i';
                        out += 'c';
                        final String out2 = getPackedRawDigits(text, index, 4);
                        index += out2.charAt(0);
                        out += out2.substring(1);
                        continue;
                    }
                    c = text.charAt(index++);
                    if (c == 202) {
                        out += 'f';
                        continue;
                    }
                    if (c > 95) {
                        currentCode = 'h';
                        out += 'd';
                        out += (char)(c - 32);
                        continue;
                    }
                    if (c < 32) {
                        out += (char)(c + 64);
                        continue;
                    }
                    out += (char)(c - 32);
                    continue;
                }
                case 'h': {
                    if (isNextDigits(text, index, 4)) {
                        currentCode = 'i';
                        out += 'c';
                        final String out2 = getPackedRawDigits(text, index, 4);
                        index += out2.charAt(0);
                        out += out2.substring(1);
                        continue;
                    }
                    c = text.charAt(index++);
                    if (c == 202) {
                        out += 'f';
                        continue;
                    }
                    if (c < 32) {
                        currentCode = 'g';
                        out += 'e';
                        out += (char)(c + 64);
                        continue;
                    }
                    out += (char)(c - 32);
                    continue;
                }
                case 'i': {
                    if (isNextDigits(text, index, 2)) {
                        final String out2 = getPackedRawDigits(text, index, 2);
                        index += out2.charAt(0);
                        out += out2.substring(1);
                        continue;
                    }
                    c = text.charAt(index++);
                    if (c == 202) {
                        out += 'f';
                        continue;
                    }
                    if (c < 32) {
                        currentCode = 'g';
                        out += 'e';
                        out += (char)(c + 64);
                        continue;
                    }
                    currentCode = 'h';
                    out += 'd';
                    out += (char)(c - 32);
                    continue;
                }
            }
        }
        return out;
    }
    
    public static byte[] getBarsCode128Raw(String text) {
        final int idx = text.indexOf(65535);
        if (idx >= 0) {
            text = text.substring(0, idx);
        }
        int chk = text.charAt(0);
        for (int k = 1; k < text.length(); ++k) {
            chk += k * text.charAt(k);
        }
        chk %= 103;
        text += (char)chk;
        final byte[] bars = new byte[(text.length() + 1) * 6 + 7];
        int i;
        for (i = 0; i < text.length(); ++i) {
            System.arraycopy(Barcode128.BARS[text.charAt(i)], 0, bars, i * 6, 6);
        }
        System.arraycopy(Barcode128.BARS_STOP, 0, bars, i * 6, 7);
        return bars;
    }
    
    @Override
    public Rectangle getBarcodeSize() {
        float fontX = 0.0f;
        float fontY = 0.0f;
        if (this.font != null) {
            if (this.baseline > 0.0f) {
                fontY = this.baseline - this.font.getFontDescriptor(3, this.size);
            }
            else {
                fontY = -this.baseline + this.size;
            }
            if (this.codeType == 11) {
                final int idx = this.code.indexOf(65535);
                if (idx < 0) {
                    final String fullCode = "";
                }
                else {
                    final String fullCode = this.code.substring(idx + 1);
                }
            }
            else if (this.codeType == 10) {
                final String fullCode = getHumanReadableUCCEAN(this.code);
            }
            else {
                final String fullCode = removeFNC1(this.code);
            }
            String fullCode;
            fontX = this.font.getWidthPoint((this.altText != null) ? this.altText : fullCode, this.size);
        }
        String fullCode;
        if (this.codeType == 11) {
            final int idx = this.code.indexOf(65535);
            if (idx >= 0) {
                fullCode = this.code.substring(0, idx);
            }
            else {
                fullCode = this.code;
            }
        }
        else {
            fullCode = getRawText(this.code, this.codeType == 10);
        }
        final int len = fullCode.length();
        float fullWidth = (len + 2) * 11 * this.x + 2.0f * this.x;
        fullWidth = Math.max(fullWidth, fontX);
        final float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        String fullCode;
        if (this.codeType == 11) {
            final int idx = this.code.indexOf(65535);
            if (idx < 0) {
                fullCode = "";
            }
            else {
                fullCode = this.code.substring(idx + 1);
            }
        }
        else if (this.codeType == 10) {
            fullCode = getHumanReadableUCCEAN(this.code);
        }
        else {
            fullCode = removeFNC1(this.code);
        }
        float fontX = 0.0f;
        if (this.font != null) {
            fontX = this.font.getWidthPoint(fullCode = ((this.altText != null) ? this.altText : fullCode), this.size);
        }
        String bCode;
        if (this.codeType == 11) {
            final int idx2 = this.code.indexOf(65535);
            if (idx2 >= 0) {
                bCode = this.code.substring(0, idx2);
            }
            else {
                bCode = this.code;
            }
        }
        else {
            bCode = getRawText(this.code, this.codeType == 10);
        }
        final int len = bCode.length();
        final float fullWidth = (len + 2) * 11 * this.x + 2.0f * this.x;
        float barStartX = 0.0f;
        float textStartX = 0.0f;
        switch (this.textAlignment) {
            case 0: {
                break;
            }
            case 2: {
                if (fontX > fullWidth) {
                    barStartX = fontX - fullWidth;
                    break;
                }
                textStartX = fullWidth - fontX;
                break;
            }
            default: {
                if (fontX > fullWidth) {
                    barStartX = (fontX - fullWidth) / 2.0f;
                    break;
                }
                textStartX = (fullWidth - fontX) / 2.0f;
                break;
            }
        }
        float barStartY = 0.0f;
        float textStartY = 0.0f;
        if (this.font != null) {
            if (this.baseline <= 0.0f) {
                textStartY = this.barHeight - this.baseline;
            }
            else {
                textStartY = -this.font.getFontDescriptor(3, this.size);
                barStartY = textStartY + this.baseline;
            }
        }
        final byte[] bars = getBarsCode128Raw(bCode);
        boolean print = true;
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (int k = 0; k < bars.length; ++k) {
            final float w = bars[k] * this.x;
            if (print) {
                cb.rectangle(barStartX, barStartY, w - this.inkSpreading, this.barHeight);
            }
            print = !print;
            barStartX += w;
        }
        cb.fill();
        if (this.font != null) {
            if (textColor != null) {
                cb.setColorFill(textColor);
            }
            cb.beginText();
            cb.setFontAndSize(this.font, this.size);
            cb.setTextMatrix(textStartX, textStartY);
            cb.showText(fullCode);
            cb.endText();
        }
        return this.getBarcodeSize();
    }
    
    @Override
    public Image createAwtImage(final Color foreground, final Color background) {
        final int f = foreground.getRGB();
        final int g = background.getRGB();
        final Canvas canvas = new Canvas();
        String bCode;
        if (this.codeType == 11) {
            final int idx = this.code.indexOf(65535);
            if (idx >= 0) {
                bCode = this.code.substring(0, idx);
            }
            else {
                bCode = this.code;
            }
        }
        else {
            bCode = getRawText(this.code, this.codeType == 10);
        }
        final int len = bCode.length();
        final int fullWidth = (len + 2) * 11 + 2;
        final byte[] bars = getBarsCode128Raw(bCode);
        boolean print = true;
        int ptr = 0;
        final int height = (int)this.barHeight;
        final int[] pix = new int[fullWidth * height];
        for (int k = 0; k < bars.length; ++k) {
            final int w = bars[k];
            int c = g;
            if (print) {
                c = f;
            }
            print = !print;
            for (int j = 0; j < w; ++j) {
                pix[ptr++] = c;
            }
        }
        for (int k = fullWidth; k < pix.length; k += fullWidth) {
            System.arraycopy(pix, 0, pix, k, fullWidth);
        }
        final Image img = canvas.createImage(new MemoryImageSource(fullWidth, height, pix, 0, fullWidth));
        return img;
    }
    
    @Override
    public void setCode(final String code) {
        if (this.getCodeType() == 10 && code.startsWith("(")) {
            int idx = 0;
            String ret = "";
            while (idx >= 0) {
                final int end = code.indexOf(41, idx);
                if (end < 0) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("badly.formed.ucc.string.1", code));
                }
                String sai = code.substring(idx + 1, end);
                if (sai.length() < 2) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("ai.too.short.1", sai));
                }
                final int ai = Integer.parseInt(sai);
                final int len = Barcode128.ais.get(ai);
                if (len == 0) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("ai.not.found.1", sai));
                }
                sai = String.valueOf(ai);
                if (sai.length() == 1) {
                    sai = "0" + sai;
                }
                idx = code.indexOf(40, end);
                final int next = (idx < 0) ? code.length() : idx;
                ret = ret + sai + code.substring(end + 1, next);
                if (len < 0) {
                    if (idx < 0) {
                        continue;
                    }
                    ret += '\u00ca';
                }
                else {
                    if (next - end - 1 + sai.length() != len) {
                        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.ai.length.1", sai));
                    }
                    continue;
                }
            }
            super.setCode(ret);
        }
        else {
            super.setCode(code);
        }
    }
    
    static {
        BARS = new byte[][] { { 2, 1, 2, 2, 2, 2 }, { 2, 2, 2, 1, 2, 2 }, { 2, 2, 2, 2, 2, 1 }, { 1, 2, 1, 2, 2, 3 }, { 1, 2, 1, 3, 2, 2 }, { 1, 3, 1, 2, 2, 2 }, { 1, 2, 2, 2, 1, 3 }, { 1, 2, 2, 3, 1, 2 }, { 1, 3, 2, 2, 1, 2 }, { 2, 2, 1, 2, 1, 3 }, { 2, 2, 1, 3, 1, 2 }, { 2, 3, 1, 2, 1, 2 }, { 1, 1, 2, 2, 3, 2 }, { 1, 2, 2, 1, 3, 2 }, { 1, 2, 2, 2, 3, 1 }, { 1, 1, 3, 2, 2, 2 }, { 1, 2, 3, 1, 2, 2 }, { 1, 2, 3, 2, 2, 1 }, { 2, 2, 3, 2, 1, 1 }, { 2, 2, 1, 1, 3, 2 }, { 2, 2, 1, 2, 3, 1 }, { 2, 1, 3, 2, 1, 2 }, { 2, 2, 3, 1, 1, 2 }, { 3, 1, 2, 1, 3, 1 }, { 3, 1, 1, 2, 2, 2 }, { 3, 2, 1, 1, 2, 2 }, { 3, 2, 1, 2, 2, 1 }, { 3, 1, 2, 2, 1, 2 }, { 3, 2, 2, 1, 1, 2 }, { 3, 2, 2, 2, 1, 1 }, { 2, 1, 2, 1, 2, 3 }, { 2, 1, 2, 3, 2, 1 }, { 2, 3, 2, 1, 2, 1 }, { 1, 1, 1, 3, 2, 3 }, { 1, 3, 1, 1, 2, 3 }, { 1, 3, 1, 3, 2, 1 }, { 1, 1, 2, 3, 1, 3 }, { 1, 3, 2, 1, 1, 3 }, { 1, 3, 2, 3, 1, 1 }, { 2, 1, 1, 3, 1, 3 }, { 2, 3, 1, 1, 1, 3 }, { 2, 3, 1, 3, 1, 1 }, { 1, 1, 2, 1, 3, 3 }, { 1, 1, 2, 3, 3, 1 }, { 1, 3, 2, 1, 3, 1 }, { 1, 1, 3, 1, 2, 3 }, { 1, 1, 3, 3, 2, 1 }, { 1, 3, 3, 1, 2, 1 }, { 3, 1, 3, 1, 2, 1 }, { 2, 1, 1, 3, 3, 1 }, { 2, 3, 1, 1, 3, 1 }, { 2, 1, 3, 1, 1, 3 }, { 2, 1, 3, 3, 1, 1 }, { 2, 1, 3, 1, 3, 1 }, { 3, 1, 1, 1, 2, 3 }, { 3, 1, 1, 3, 2, 1 }, { 3, 3, 1, 1, 2, 1 }, { 3, 1, 2, 1, 1, 3 }, { 3, 1, 2, 3, 1, 1 }, { 3, 3, 2, 1, 1, 1 }, { 3, 1, 4, 1, 1, 1 }, { 2, 2, 1, 4, 1, 1 }, { 4, 3, 1, 1, 1, 1 }, { 1, 1, 1, 2, 2, 4 }, { 1, 1, 1, 4, 2, 2 }, { 1, 2, 1, 1, 2, 4 }, { 1, 2, 1, 4, 2, 1 }, { 1, 4, 1, 1, 2, 2 }, { 1, 4, 1, 2, 2, 1 }, { 1, 1, 2, 2, 1, 4 }, { 1, 1, 2, 4, 1, 2 }, { 1, 2, 2, 1, 1, 4 }, { 1, 2, 2, 4, 1, 1 }, { 1, 4, 2, 1, 1, 2 }, { 1, 4, 2, 2, 1, 1 }, { 2, 4, 1, 2, 1, 1 }, { 2, 2, 1, 1, 1, 4 }, { 4, 1, 3, 1, 1, 1 }, { 2, 4, 1, 1, 1, 2 }, { 1, 3, 4, 1, 1, 1 }, { 1, 1, 1, 2, 4, 2 }, { 1, 2, 1, 1, 4, 2 }, { 1, 2, 1, 2, 4, 1 }, { 1, 1, 4, 2, 1, 2 }, { 1, 2, 4, 1, 1, 2 }, { 1, 2, 4, 2, 1, 1 }, { 4, 1, 1, 2, 1, 2 }, { 4, 2, 1, 1, 1, 2 }, { 4, 2, 1, 2, 1, 1 }, { 2, 1, 2, 1, 4, 1 }, { 2, 1, 4, 1, 2, 1 }, { 4, 1, 2, 1, 2, 1 }, { 1, 1, 1, 1, 4, 3 }, { 1, 1, 1, 3, 4, 1 }, { 1, 3, 1, 1, 4, 1 }, { 1, 1, 4, 1, 1, 3 }, { 1, 1, 4, 3, 1, 1 }, { 4, 1, 1, 1, 1, 3 }, { 4, 1, 1, 3, 1, 1 }, { 1, 1, 3, 1, 4, 1 }, { 1, 1, 4, 1, 3, 1 }, { 3, 1, 1, 1, 4, 1 }, { 4, 1, 1, 1, 3, 1 }, { 2, 1, 1, 4, 1, 2 }, { 2, 1, 1, 2, 1, 4 }, { 2, 1, 1, 2, 3, 2 } };
        BARS_STOP = new byte[] { 2, 3, 3, 1, 1, 1, 2 };
        (ais = new IntHashtable()).put(0, 20);
        Barcode128.ais.put(1, 16);
        Barcode128.ais.put(2, 16);
        Barcode128.ais.put(10, -1);
        Barcode128.ais.put(11, 9);
        Barcode128.ais.put(12, 8);
        Barcode128.ais.put(13, 8);
        Barcode128.ais.put(15, 8);
        Barcode128.ais.put(17, 8);
        Barcode128.ais.put(20, 4);
        Barcode128.ais.put(21, -1);
        Barcode128.ais.put(22, -1);
        Barcode128.ais.put(23, -1);
        Barcode128.ais.put(240, -1);
        Barcode128.ais.put(241, -1);
        Barcode128.ais.put(250, -1);
        Barcode128.ais.put(251, -1);
        Barcode128.ais.put(252, -1);
        Barcode128.ais.put(30, -1);
        for (int k = 3100; k < 3700; ++k) {
            Barcode128.ais.put(k, 10);
        }
        Barcode128.ais.put(37, -1);
        for (int k = 3900; k < 3940; ++k) {
            Barcode128.ais.put(k, -1);
        }
        Barcode128.ais.put(400, -1);
        Barcode128.ais.put(401, -1);
        Barcode128.ais.put(402, 20);
        Barcode128.ais.put(403, -1);
        for (int k = 410; k < 416; ++k) {
            Barcode128.ais.put(k, 16);
        }
        Barcode128.ais.put(420, -1);
        Barcode128.ais.put(421, -1);
        Barcode128.ais.put(422, 6);
        Barcode128.ais.put(423, -1);
        Barcode128.ais.put(424, 6);
        Barcode128.ais.put(425, 6);
        Barcode128.ais.put(426, 6);
        Barcode128.ais.put(7001, 17);
        Barcode128.ais.put(7002, -1);
        for (int k = 7030; k < 7040; ++k) {
            Barcode128.ais.put(k, -1);
        }
        Barcode128.ais.put(8001, 18);
        Barcode128.ais.put(8002, -1);
        Barcode128.ais.put(8003, -1);
        Barcode128.ais.put(8004, -1);
        Barcode128.ais.put(8005, 10);
        Barcode128.ais.put(8006, 22);
        Barcode128.ais.put(8007, -1);
        Barcode128.ais.put(8008, -1);
        Barcode128.ais.put(8018, 22);
        Barcode128.ais.put(8020, -1);
        Barcode128.ais.put(8100, 10);
        Barcode128.ais.put(8101, 14);
        Barcode128.ais.put(8102, 6);
        for (int k = 90; k < 100; ++k) {
            Barcode128.ais.put(k, -1);
        }
    }
}

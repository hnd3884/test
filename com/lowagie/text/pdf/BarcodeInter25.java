package com.lowagie.text.pdf;

import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.ExceptionConverter;

public class BarcodeInter25 extends Barcode
{
    private static final byte[][] BARS;
    
    public BarcodeInter25() {
        try {
            this.x = 0.8f;
            this.n = 2.0f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.size = 8.0f;
            this.baseline = this.size;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.generateChecksum = false;
            this.checksumText = false;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static String keepNumbers(final String text) {
        final StringBuffer sb = new StringBuffer();
        for (int k = 0; k < text.length(); ++k) {
            final char c = text.charAt(k);
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static char getChecksum(final String text) {
        int mul = 3;
        int total = 0;
        for (int k = text.length() - 1; k >= 0; --k) {
            final int n = text.charAt(k) - '0';
            total += mul * n;
            mul ^= 0x2;
        }
        return (char)((10 - total % 10) % 10 + 48);
    }
    
    public static byte[] getBarsInter25(String text) {
        text = keepNumbers(text);
        if ((text.length() & 0x1) != 0x0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.text.length.must.be.even"));
        }
        final byte[] bars = new byte[text.length() * 5 + 7];
        int pb = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        for (int len = text.length() / 2, k = 0; k < len; ++k) {
            final int c1 = text.charAt(k * 2) - '0';
            final int c2 = text.charAt(k * 2 + 1) - '0';
            final byte[] b1 = BarcodeInter25.BARS[c1];
            final byte[] b2 = BarcodeInter25.BARS[c2];
            for (int j = 0; j < 5; ++j) {
                bars[pb++] = b1[j];
                bars[pb++] = b2[j];
            }
        }
        bars[pb++] = 1;
        bars[pb++] = 0;
        bars[pb++] = 0;
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
            String fullCode = this.code;
            if (this.generateChecksum && this.checksumText) {
                fullCode += getChecksum(fullCode);
            }
            fontX = this.font.getWidthPoint((this.altText != null) ? this.altText : fullCode, this.size);
        }
        String fullCode = keepNumbers(this.code);
        int len = fullCode.length();
        if (this.generateChecksum) {
            ++len;
        }
        float fullWidth = len * (3.0f * this.x + 2.0f * this.x * this.n) + (6.0f + this.n) * this.x;
        fullWidth = Math.max(fullWidth, fontX);
        final float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        String fullCode = this.code;
        float fontX = 0.0f;
        if (this.font != null) {
            if (this.generateChecksum && this.checksumText) {
                fullCode += getChecksum(fullCode);
            }
            fontX = this.font.getWidthPoint(fullCode = ((this.altText != null) ? this.altText : fullCode), this.size);
        }
        String bCode = keepNumbers(this.code);
        if (this.generateChecksum) {
            bCode += getChecksum(bCode);
        }
        final int len = bCode.length();
        final float fullWidth = len * (3.0f * this.x + 2.0f * this.x * this.n) + (6.0f + this.n) * this.x;
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
        final byte[] bars = getBarsInter25(bCode);
        boolean print = true;
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (int k = 0; k < bars.length; ++k) {
            final float w = (bars[k] == 0) ? this.x : (this.x * this.n);
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
        String bCode = keepNumbers(this.code);
        if (this.generateChecksum) {
            bCode += getChecksum(bCode);
        }
        final int len = bCode.length();
        final int nn = (int)this.n;
        final int fullWidth = len * (3 + 2 * nn) + (6 + nn);
        final byte[] bars = getBarsInter25(bCode);
        boolean print = true;
        int ptr = 0;
        final int height = (int)this.barHeight;
        final int[] pix = new int[fullWidth * height];
        for (int k = 0; k < bars.length; ++k) {
            final int w = (bars[k] == 0) ? 1 : nn;
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
    
    static {
        BARS = new byte[][] { { 0, 0, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 0, 0, 1 }, { 1, 1, 0, 0, 0 }, { 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0 }, { 0, 0, 0, 1, 1 }, { 1, 0, 0, 1, 0 }, { 0, 1, 0, 1, 0 } };
    }
}

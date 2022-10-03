package com.lowagie.text.pdf;

import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.ExceptionConverter;

public class BarcodeCodabar extends Barcode
{
    private static final byte[][] BARS;
    private static final String CHARS = "0123456789-$:/.+ABCD";
    private static final int START_STOP_IDX = 16;
    
    public BarcodeCodabar() {
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
            this.startStopText = false;
            this.codeType = 12;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static byte[] getBarsCodabar(String text) {
        text = text.toUpperCase();
        final int len = text.length();
        if (len < 2) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("codabar.must.have.at.least.a.start.and.stop.character"));
        }
        if ("0123456789-$:/.+ABCD".indexOf(text.charAt(0)) < 16 || "0123456789-$:/.+ABCD".indexOf(text.charAt(len - 1)) < 16) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("codabar.must.have.one.of.abcd.as.start.stop.character"));
        }
        final byte[] bars = new byte[text.length() * 8 - 1];
        for (int k = 0; k < len; ++k) {
            final int idx = "0123456789-$:/.+ABCD".indexOf(text.charAt(k));
            if (idx >= 16 && k > 0 && k < len - 1) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("in.codabar.start.stop.characters.are.only.allowed.at.the.extremes"));
            }
            if (idx < 0) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.character.1.is.illegal.in.codabar", text.charAt(k)));
            }
            System.arraycopy(BarcodeCodabar.BARS[idx], 0, bars, k * 8, 7);
        }
        return bars;
    }
    
    public static String calculateChecksum(final String code) {
        if (code.length() < 2) {
            return code;
        }
        final String text = code.toUpperCase();
        int sum = 0;
        final int len = text.length();
        for (int k = 0; k < len; ++k) {
            sum += "0123456789-$:/.+ABCD".indexOf(text.charAt(k));
        }
        sum = (sum + 15) / 16 * 16 - sum;
        return code.substring(0, len - 1) + "0123456789-$:/.+ABCD".charAt(sum) + code.substring(len - 1);
    }
    
    @Override
    public Rectangle getBarcodeSize() {
        float fontX = 0.0f;
        float fontY = 0.0f;
        String text = this.code;
        if (this.generateChecksum && this.checksumText) {
            text = calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            text = text.substring(1, text.length() - 1);
        }
        if (this.font != null) {
            if (this.baseline > 0.0f) {
                fontY = this.baseline - this.font.getFontDescriptor(3, this.size);
            }
            else {
                fontY = -this.baseline + this.size;
            }
            fontX = this.font.getWidthPoint((this.altText != null) ? this.altText : text, this.size);
        }
        text = this.code;
        if (this.generateChecksum) {
            text = calculateChecksum(this.code);
        }
        final byte[] bars = getBarsCodabar(text);
        int wide = 0;
        for (int k = 0; k < bars.length; ++k) {
            wide += bars[k];
        }
        final int narrow = bars.length - wide;
        float fullWidth = this.x * (narrow + wide * this.n);
        fullWidth = Math.max(fullWidth, fontX);
        final float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        String fullCode = this.code;
        if (this.generateChecksum && this.checksumText) {
            fullCode = calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            fullCode = fullCode.substring(1, fullCode.length() - 1);
        }
        float fontX = 0.0f;
        if (this.font != null) {
            fontX = this.font.getWidthPoint(fullCode = ((this.altText != null) ? this.altText : fullCode), this.size);
        }
        final byte[] bars = getBarsCodabar(this.generateChecksum ? calculateChecksum(this.code) : this.code);
        int wide = 0;
        for (int k = 0; k < bars.length; ++k) {
            wide += bars[k];
        }
        final int narrow = bars.length - wide;
        final float fullWidth = this.x * (narrow + wide * this.n);
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
        boolean print = true;
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (int i = 0; i < bars.length; ++i) {
            final float w = (bars[i] == 0) ? this.x : (this.x * this.n);
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
        String fullCode = this.code;
        if (this.generateChecksum && this.checksumText) {
            fullCode = calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            fullCode = fullCode.substring(1, fullCode.length() - 1);
        }
        final byte[] bars = getBarsCodabar(this.generateChecksum ? calculateChecksum(this.code) : this.code);
        int wide = 0;
        for (int k = 0; k < bars.length; ++k) {
            wide += bars[k];
        }
        final int narrow = bars.length - wide;
        final int fullWidth = narrow + wide * (int)this.n;
        boolean print = true;
        int ptr = 0;
        final int height = (int)this.barHeight;
        final int[] pix = new int[fullWidth * height];
        for (int i = 0; i < bars.length; ++i) {
            final int w = (bars[i] == 0) ? 1 : ((int)this.n);
            int c = g;
            if (print) {
                c = f;
            }
            print = !print;
            for (int j = 0; j < w; ++j) {
                pix[ptr++] = c;
            }
        }
        for (int i = fullWidth; i < pix.length; i += fullWidth) {
            System.arraycopy(pix, 0, pix, i, fullWidth);
        }
        final Image img = canvas.createImage(new MemoryImageSource(fullWidth, height, pix, 0, fullWidth));
        return img;
    }
    
    static {
        BARS = new byte[][] { { 0, 0, 0, 0, 0, 1, 1 }, { 0, 0, 0, 0, 1, 1, 0 }, { 0, 0, 0, 1, 0, 0, 1 }, { 1, 1, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0, 1, 0 }, { 1, 0, 0, 0, 0, 1, 0 }, { 0, 1, 0, 0, 0, 0, 1 }, { 0, 1, 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 1, 1, 0, 0 }, { 0, 0, 1, 1, 0, 0, 0 }, { 1, 0, 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 0, 0, 1 }, { 1, 0, 1, 0, 1, 0, 0 }, { 0, 0, 1, 0, 1, 0, 1 }, { 0, 0, 1, 1, 0, 1, 0 }, { 0, 1, 0, 1, 0, 0, 1 }, { 0, 0, 0, 1, 0, 1, 1 }, { 0, 0, 0, 1, 1, 1, 0 } };
    }
}

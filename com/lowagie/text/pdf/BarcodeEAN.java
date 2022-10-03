package com.lowagie.text.pdf;

import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Canvas;
import java.awt.Image;
import java.util.Arrays;
import java.awt.Color;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Rectangle;
import com.lowagie.text.ExceptionConverter;

public class BarcodeEAN extends Barcode
{
    private static final int[] GUARD_EMPTY;
    private static final int[] GUARD_UPCA;
    private static final int[] GUARD_EAN13;
    private static final int[] GUARD_EAN8;
    private static final int[] GUARD_UPCE;
    private static final float[] TEXTPOS_EAN13;
    private static final float[] TEXTPOS_EAN8;
    private static final byte[][] BARS;
    private static final int TOTALBARS_EAN13 = 59;
    private static final int TOTALBARS_EAN8 = 43;
    private static final int TOTALBARS_UPCE = 33;
    private static final int TOTALBARS_SUPP2 = 13;
    private static final int TOTALBARS_SUPP5 = 31;
    private static final int ODD = 0;
    private static final int EVEN = 1;
    private static final byte[][] PARITY13;
    private static final byte[][] PARITY2;
    private static final byte[][] PARITY5;
    private static final byte[][] PARITYE;
    
    public BarcodeEAN() {
        try {
            this.x = 0.8f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.size = 8.0f;
            this.baseline = this.size;
            this.barHeight = this.size * 3.0f;
            this.guardBars = true;
            this.codeType = 1;
            this.code = "";
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static int calculateEANParity(final String code) {
        int mul = 3;
        int total = 0;
        for (int k = code.length() - 1; k >= 0; --k) {
            final int n = code.charAt(k) - '0';
            total += mul * n;
            mul ^= 0x2;
        }
        return (10 - total % 10) % 10;
    }
    
    public static String convertUPCAtoUPCE(final String text) {
        if (text.length() != 12 || (!text.startsWith("0") && !text.startsWith("1"))) {
            return null;
        }
        if (text.substring(3, 6).equals("000") || text.substring(3, 6).equals("100") || text.substring(3, 6).equals("200")) {
            if (text.substring(6, 8).equals("00")) {
                return text.substring(0, 1) + text.substring(1, 3) + text.substring(8, 11) + text.substring(3, 4) + text.substring(11);
            }
        }
        else if (text.substring(4, 6).equals("00")) {
            if (text.substring(6, 9).equals("000")) {
                return text.substring(0, 1) + text.substring(1, 4) + text.substring(9, 11) + "3" + text.substring(11);
            }
        }
        else if (text.substring(5, 6).equals("0")) {
            if (text.substring(6, 10).equals("0000")) {
                return text.substring(0, 1) + text.substring(1, 5) + text.substring(10, 11) + "4" + text.substring(11);
            }
        }
        else if (text.charAt(10) >= '5' && text.substring(6, 10).equals("0000")) {
            return text.substring(0, 1) + text.substring(1, 6) + text.substring(10, 11) + text.substring(11);
        }
        return null;
    }
    
    public static byte[] getBarsEAN13(final String _code) {
        final int[] code = new int[_code.length()];
        for (int k = 0; k < code.length; ++k) {
            code[k] = _code.charAt(k) - '0';
        }
        final byte[] bars = new byte[59];
        int pb = 0;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        final byte[] sequence = BarcodeEAN.PARITY13[code[0]];
        for (int i = 0; i < sequence.length; ++i) {
            final int c = code[i + 1];
            final byte[] stripes = BarcodeEAN.BARS[c];
            if (sequence[i] == 0) {
                bars[pb++] = stripes[0];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[3];
            }
            else {
                bars[pb++] = stripes[3];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[0];
            }
        }
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        for (int i = 7; i < 13; ++i) {
            final int c = code[i];
            final byte[] stripes = BarcodeEAN.BARS[c];
            bars[pb++] = stripes[0];
            bars[pb++] = stripes[1];
            bars[pb++] = stripes[2];
            bars[pb++] = stripes[3];
        }
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        return bars;
    }
    
    public static byte[] getBarsEAN8(final String _code) {
        final int[] code = new int[_code.length()];
        for (int k = 0; k < code.length; ++k) {
            code[k] = _code.charAt(k) - '0';
        }
        final byte[] bars = new byte[43];
        int pb = 0;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        for (int i = 0; i < 4; ++i) {
            final int c = code[i];
            final byte[] stripes = BarcodeEAN.BARS[c];
            bars[pb++] = stripes[0];
            bars[pb++] = stripes[1];
            bars[pb++] = stripes[2];
            bars[pb++] = stripes[3];
        }
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        for (int i = 4; i < 8; ++i) {
            final int c = code[i];
            final byte[] stripes = BarcodeEAN.BARS[c];
            bars[pb++] = stripes[0];
            bars[pb++] = stripes[1];
            bars[pb++] = stripes[2];
            bars[pb++] = stripes[3];
        }
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        return bars;
    }
    
    public static byte[] getBarsUPCE(final String _code) {
        final int[] code = new int[_code.length()];
        for (int k = 0; k < code.length; ++k) {
            code[k] = _code.charAt(k) - '0';
        }
        final byte[] bars = new byte[33];
        final boolean flip = code[0] != 0;
        int pb = 0;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        final byte[] sequence = BarcodeEAN.PARITYE[code[code.length - 1]];
        for (int i = 1; i < code.length - 1; ++i) {
            final int c = code[i];
            final byte[] stripes = BarcodeEAN.BARS[c];
            if (sequence[i - 1] == (flip ? 1 : 0)) {
                bars[pb++] = stripes[0];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[3];
            }
            else {
                bars[pb++] = stripes[3];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[0];
            }
        }
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 1;
        return bars;
    }
    
    public static byte[] getBarsSupplemental2(final String _code) {
        final int[] code = new int[2];
        for (int k = 0; k < code.length; ++k) {
            code[k] = _code.charAt(k) - '0';
        }
        final byte[] bars = new byte[13];
        int pb = 0;
        final int parity = (code[0] * 10 + code[1]) % 4;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 2;
        final byte[] sequence = BarcodeEAN.PARITY2[parity];
        for (int i = 0; i < sequence.length; ++i) {
            if (i == 1) {
                bars[pb++] = 1;
                bars[pb++] = 1;
            }
            final int c = code[i];
            final byte[] stripes = BarcodeEAN.BARS[c];
            if (sequence[i] == 0) {
                bars[pb++] = stripes[0];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[3];
            }
            else {
                bars[pb++] = stripes[3];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[0];
            }
        }
        return bars;
    }
    
    public static byte[] getBarsSupplemental5(final String _code) {
        final int[] code = new int[5];
        for (int k = 0; k < code.length; ++k) {
            code[k] = _code.charAt(k) - '0';
        }
        final byte[] bars = new byte[31];
        int pb = 0;
        final int parity = ((code[0] + code[2] + code[4]) * 3 + (code[1] + code[3]) * 9) % 10;
        bars[pb++] = 1;
        bars[pb++] = 1;
        bars[pb++] = 2;
        final byte[] sequence = BarcodeEAN.PARITY5[parity];
        for (int i = 0; i < sequence.length; ++i) {
            if (i != 0) {
                bars[pb++] = 1;
                bars[pb++] = 1;
            }
            final int c = code[i];
            final byte[] stripes = BarcodeEAN.BARS[c];
            if (sequence[i] == 0) {
                bars[pb++] = stripes[0];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[3];
            }
            else {
                bars[pb++] = stripes[3];
                bars[pb++] = stripes[2];
                bars[pb++] = stripes[1];
                bars[pb++] = stripes[0];
            }
        }
        return bars;
    }
    
    @Override
    public Rectangle getBarcodeSize() {
        float width = 0.0f;
        float height = this.barHeight;
        if (this.font != null) {
            if (this.baseline <= 0.0f) {
                height += -this.baseline + this.size;
            }
            else {
                height += this.baseline - this.font.getFontDescriptor(3, this.size);
            }
        }
        switch (this.codeType) {
            case 1: {
                width = this.x * 95.0f;
                if (this.font != null) {
                    width += this.font.getWidthPoint(this.code.charAt(0), this.size);
                    break;
                }
                break;
            }
            case 2: {
                width = this.x * 67.0f;
                break;
            }
            case 3: {
                width = this.x * 95.0f;
                if (this.font != null) {
                    width += this.font.getWidthPoint(this.code.charAt(0), this.size) + this.font.getWidthPoint(this.code.charAt(11), this.size);
                    break;
                }
                break;
            }
            case 4: {
                width = this.x * 51.0f;
                if (this.font != null) {
                    width += this.font.getWidthPoint(this.code.charAt(0), this.size) + this.font.getWidthPoint(this.code.charAt(7), this.size);
                    break;
                }
                break;
            }
            case 5: {
                width = this.x * 20.0f;
                break;
            }
            case 6: {
                width = this.x * 47.0f;
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.code.type"));
            }
        }
        return new Rectangle(width, height);
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        final Rectangle rect = this.getBarcodeSize();
        float barStartX = 0.0f;
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
        switch (this.codeType) {
            case 1:
            case 3:
            case 4: {
                if (this.font != null) {
                    barStartX += this.font.getWidthPoint(this.code.charAt(0), this.size);
                    break;
                }
                break;
            }
        }
        byte[] bars = null;
        int[] guard = BarcodeEAN.GUARD_EMPTY;
        switch (this.codeType) {
            case 1: {
                bars = getBarsEAN13(this.code);
                guard = BarcodeEAN.GUARD_EAN13;
                break;
            }
            case 2: {
                bars = getBarsEAN8(this.code);
                guard = BarcodeEAN.GUARD_EAN8;
                break;
            }
            case 3: {
                bars = getBarsEAN13("0" + this.code);
                guard = BarcodeEAN.GUARD_UPCA;
                break;
            }
            case 4: {
                bars = getBarsUPCE(this.code);
                guard = BarcodeEAN.GUARD_UPCE;
                break;
            }
            case 5: {
                bars = getBarsSupplemental2(this.code);
                break;
            }
            case 6: {
                bars = getBarsSupplemental5(this.code);
                break;
            }
        }
        final float keepBarX = barStartX;
        boolean print = true;
        float gd = 0.0f;
        if (this.font != null && this.baseline > 0.0f && this.guardBars) {
            gd = this.baseline / 2.0f;
        }
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (int k = 0; k < bars.length; ++k) {
            final float w = bars[k] * this.x;
            if (print) {
                if (Arrays.binarySearch(guard, k) >= 0) {
                    cb.rectangle(barStartX, barStartY - gd, w - this.inkSpreading, this.barHeight + gd);
                }
                else {
                    cb.rectangle(barStartX, barStartY, w - this.inkSpreading, this.barHeight);
                }
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
            switch (this.codeType) {
                case 1: {
                    cb.setTextMatrix(0.0f, textStartY);
                    cb.showText(this.code.substring(0, 1));
                    for (int k = 1; k < 13; ++k) {
                        final String c = this.code.substring(k, k + 1);
                        final float len = this.font.getWidthPoint(c, this.size);
                        final float pX = keepBarX + BarcodeEAN.TEXTPOS_EAN13[k - 1] * this.x - len / 2.0f;
                        cb.setTextMatrix(pX, textStartY);
                        cb.showText(c);
                    }
                    break;
                }
                case 2: {
                    for (int k = 0; k < 8; ++k) {
                        final String c = this.code.substring(k, k + 1);
                        final float len = this.font.getWidthPoint(c, this.size);
                        final float pX = BarcodeEAN.TEXTPOS_EAN8[k] * this.x - len / 2.0f;
                        cb.setTextMatrix(pX, textStartY);
                        cb.showText(c);
                    }
                    break;
                }
                case 3: {
                    cb.setTextMatrix(0.0f, textStartY);
                    cb.showText(this.code.substring(0, 1));
                    for (int k = 1; k < 11; ++k) {
                        final String c = this.code.substring(k, k + 1);
                        final float len = this.font.getWidthPoint(c, this.size);
                        final float pX = keepBarX + BarcodeEAN.TEXTPOS_EAN13[k] * this.x - len / 2.0f;
                        cb.setTextMatrix(pX, textStartY);
                        cb.showText(c);
                    }
                    cb.setTextMatrix(keepBarX + this.x * 95.0f, textStartY);
                    cb.showText(this.code.substring(11, 12));
                    break;
                }
                case 4: {
                    cb.setTextMatrix(0.0f, textStartY);
                    cb.showText(this.code.substring(0, 1));
                    for (int k = 1; k < 7; ++k) {
                        final String c = this.code.substring(k, k + 1);
                        final float len = this.font.getWidthPoint(c, this.size);
                        final float pX = keepBarX + BarcodeEAN.TEXTPOS_EAN13[k - 1] * this.x - len / 2.0f;
                        cb.setTextMatrix(pX, textStartY);
                        cb.showText(c);
                    }
                    cb.setTextMatrix(keepBarX + this.x * 51.0f, textStartY);
                    cb.showText(this.code.substring(7, 8));
                    break;
                }
                case 5:
                case 6: {
                    for (int k = 0; k < this.code.length(); ++k) {
                        final String c = this.code.substring(k, k + 1);
                        final float len = this.font.getWidthPoint(c, this.size);
                        final float pX = (7.5f + 9 * k) * this.x - len / 2.0f;
                        cb.setTextMatrix(pX, textStartY);
                        cb.showText(c);
                    }
                    break;
                }
            }
            cb.endText();
        }
        return rect;
    }
    
    @Override
    public Image createAwtImage(final Color foreground, final Color background) {
        final int f = foreground.getRGB();
        final int g = background.getRGB();
        final Canvas canvas = new Canvas();
        int width = 0;
        byte[] bars = null;
        switch (this.codeType) {
            case 1: {
                bars = getBarsEAN13(this.code);
                width = 95;
                break;
            }
            case 2: {
                bars = getBarsEAN8(this.code);
                width = 67;
                break;
            }
            case 3: {
                bars = getBarsEAN13("0" + this.code);
                width = 95;
                break;
            }
            case 4: {
                bars = getBarsUPCE(this.code);
                width = 51;
                break;
            }
            case 5: {
                bars = getBarsSupplemental2(this.code);
                width = 20;
                break;
            }
            case 6: {
                bars = getBarsSupplemental5(this.code);
                width = 47;
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.code.type"));
            }
        }
        boolean print = true;
        int ptr = 0;
        final int height = (int)this.barHeight;
        final int[] pix = new int[width * height];
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
        for (int k = width; k < pix.length; k += width) {
            System.arraycopy(pix, 0, pix, k, width);
        }
        final Image img = canvas.createImage(new MemoryImageSource(width, height, pix, 0, width));
        return img;
    }
    
    static {
        GUARD_EMPTY = new int[0];
        GUARD_UPCA = new int[] { 0, 2, 4, 6, 28, 30, 52, 54, 56, 58 };
        GUARD_EAN13 = new int[] { 0, 2, 28, 30, 56, 58 };
        GUARD_EAN8 = new int[] { 0, 2, 20, 22, 40, 42 };
        GUARD_UPCE = new int[] { 0, 2, 28, 30, 32 };
        TEXTPOS_EAN13 = new float[] { 6.5f, 13.5f, 20.5f, 27.5f, 34.5f, 41.5f, 53.5f, 60.5f, 67.5f, 74.5f, 81.5f, 88.5f };
        TEXTPOS_EAN8 = new float[] { 6.5f, 13.5f, 20.5f, 27.5f, 39.5f, 46.5f, 53.5f, 60.5f };
        BARS = new byte[][] { { 3, 2, 1, 1 }, { 2, 2, 2, 1 }, { 2, 1, 2, 2 }, { 1, 4, 1, 1 }, { 1, 1, 3, 2 }, { 1, 2, 3, 1 }, { 1, 1, 1, 4 }, { 1, 3, 1, 2 }, { 1, 2, 1, 3 }, { 3, 1, 1, 2 } };
        PARITY13 = new byte[][] { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 1, 1 }, { 0, 0, 1, 1, 0, 1 }, { 0, 0, 1, 1, 1, 0 }, { 0, 1, 0, 0, 1, 1 }, { 0, 1, 1, 0, 0, 1 }, { 0, 1, 1, 1, 0, 0 }, { 0, 1, 0, 1, 0, 1 }, { 0, 1, 0, 1, 1, 0 }, { 0, 1, 1, 0, 1, 0 } };
        PARITY2 = new byte[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
        PARITY5 = new byte[][] { { 1, 1, 0, 0, 0 }, { 1, 0, 1, 0, 0 }, { 1, 0, 0, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 0, 0 }, { 0, 0, 1, 1, 0 }, { 0, 0, 0, 1, 1 }, { 0, 1, 0, 1, 0 }, { 0, 1, 0, 0, 1 }, { 0, 0, 1, 0, 1 } };
        PARITYE = new byte[][] { { 1, 1, 1, 0, 0, 0 }, { 1, 1, 0, 1, 0, 0 }, { 1, 1, 0, 0, 1, 0 }, { 1, 1, 0, 0, 0, 1 }, { 1, 0, 1, 1, 0, 0 }, { 1, 0, 0, 1, 1, 0 }, { 1, 0, 0, 0, 1, 1 }, { 1, 0, 1, 0, 1, 0 }, { 1, 0, 1, 0, 0, 1 }, { 1, 0, 0, 1, 0, 1 } };
    }
}

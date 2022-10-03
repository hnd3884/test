package com.lowagie.text.pdf;

import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;

public class BarcodePostnet extends Barcode
{
    private static final byte[][] BARS;
    
    public BarcodePostnet() {
        this.n = 3.2727273f;
        this.x = 1.4399999f;
        this.barHeight = 9.0f;
        this.size = 3.6000001f;
        this.codeType = 7;
    }
    
    public static byte[] getBarsPostnet(String text) {
        int total = 0;
        for (int k = text.length() - 1; k >= 0; --k) {
            final int n = text.charAt(k) - '0';
            total += n;
        }
        text += (char)((10 - total % 10) % 10 + 48);
        final byte[] bars = new byte[text.length() * 5 + 2];
        bars[0] = 1;
        bars[bars.length - 1] = 1;
        for (int i = 0; i < text.length(); ++i) {
            final int c = text.charAt(i) - '0';
            System.arraycopy(BarcodePostnet.BARS[c], 0, bars, i * 5 + 1, 5);
        }
        return bars;
    }
    
    @Override
    public Rectangle getBarcodeSize() {
        final float width = ((this.code.length() + 1) * 5 + 1) * this.n + this.x;
        return new Rectangle(width, this.barHeight);
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        final byte[] bars = getBarsPostnet(this.code);
        byte flip = 1;
        if (this.codeType == 8) {
            flip = 0;
            bars[0] = 0;
            bars[bars.length - 1] = 0;
        }
        float startX = 0.0f;
        for (int k = 0; k < bars.length; ++k) {
            cb.rectangle(startX, 0.0f, this.x - this.inkSpreading, (bars[k] == flip) ? this.barHeight : this.size);
            startX += this.n;
        }
        cb.fill();
        return this.getBarcodeSize();
    }
    
    @Override
    public Image createAwtImage(final Color foreground, final Color background) {
        final int f = foreground.getRGB();
        final int g = background.getRGB();
        final Canvas canvas = new Canvas();
        int barWidth = (int)this.x;
        if (barWidth <= 0) {
            barWidth = 1;
        }
        int barDistance = (int)this.n;
        if (barDistance <= barWidth) {
            barDistance = barWidth + 1;
        }
        int barShort = (int)this.size;
        if (barShort <= 0) {
            barShort = 1;
        }
        int barTall = (int)this.barHeight;
        if (barTall <= barShort) {
            barTall = barShort + 1;
        }
        final int width = ((this.code.length() + 1) * 5 + 1) * barDistance + barWidth;
        final int[] pix = new int[width * barTall];
        final byte[] bars = getBarsPostnet(this.code);
        byte flip = 1;
        if (this.codeType == 8) {
            flip = 0;
            bars[0] = 0;
            bars[bars.length - 1] = 0;
        }
        int idx = 0;
        for (int k = 0; k < bars.length; ++k) {
            final boolean dot = bars[k] == flip;
            for (int j = 0; j < barDistance; ++j) {
                pix[idx + j] = ((dot && j < barWidth) ? f : g);
            }
            idx += barDistance;
        }
        final int limit = width * (barTall - barShort);
        for (int i = width; i < limit; i += width) {
            System.arraycopy(pix, 0, pix, i, width);
        }
        idx = limit;
        for (int i = 0; i < bars.length; ++i) {
            for (int j = 0; j < barDistance; ++j) {
                pix[idx + j] = ((j < barWidth) ? f : g);
            }
            idx += barDistance;
        }
        for (int i = limit + width; i < pix.length; i += width) {
            System.arraycopy(pix, limit, pix, i, width);
        }
        final Image img = canvas.createImage(new MemoryImageSource(width, barTall, pix, 0, width));
        return img;
    }
    
    static {
        BARS = new byte[][] { { 1, 1, 0, 0, 0 }, { 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 1 }, { 0, 0, 1, 1, 0 }, { 0, 1, 0, 0, 1 }, { 0, 1, 0, 1, 0 }, { 0, 1, 1, 0, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 1, 0 }, { 1, 0, 1, 0, 0 } };
    }
}

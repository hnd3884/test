package com.lowagie.text.pdf;

import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.ExceptionConverter;

public class Barcode39 extends Barcode
{
    private static final byte[][] BARS;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*";
    private static final String EXTENDED = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T";
    
    public Barcode39() {
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
            this.startStopText = true;
            this.extended = false;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static byte[] getBarsCode39(String text) {
        text = "*" + text + "*";
        final byte[] bars = new byte[text.length() * 10 - 1];
        for (int k = 0; k < text.length(); ++k) {
            final int idx = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".indexOf(text.charAt(k));
            if (idx < 0) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.character.1.is.illegal.in.code.39", text.charAt(k)));
            }
            System.arraycopy(Barcode39.BARS[idx], 0, bars, k * 10, 9);
        }
        return bars;
    }
    
    public static String getCode39Ex(final String text) {
        String out = "";
        for (int k = 0; k < text.length(); ++k) {
            final char c = text.charAt(k);
            if (c > '\u007f') {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.character.1.is.illegal.in.code.39.extended", c));
            }
            final char c2 = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T".charAt(c * '\u0002');
            final char c3 = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T".charAt(c * '\u0002' + 1);
            if (c2 != ' ') {
                out += c2;
            }
            out += c3;
        }
        return out;
    }
    
    static char getChecksum(final String text) {
        int chk = 0;
        for (int k = 0; k < text.length(); ++k) {
            final int idx = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".indexOf(text.charAt(k));
            if (idx < 0) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.character.1.is.illegal.in.code.39", text.charAt(k)));
            }
            chk += idx;
        }
        return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".charAt(chk % 43);
    }
    
    @Override
    public Rectangle getBarcodeSize() {
        float fontX = 0.0f;
        float fontY = 0.0f;
        String fCode = this.code;
        if (this.extended) {
            fCode = getCode39Ex(this.code);
        }
        if (this.font != null) {
            if (this.baseline > 0.0f) {
                fontY = this.baseline - this.font.getFontDescriptor(3, this.size);
            }
            else {
                fontY = -this.baseline + this.size;
            }
            String fullCode = this.code;
            if (this.generateChecksum && this.checksumText) {
                fullCode += getChecksum(fCode);
            }
            if (this.startStopText) {
                fullCode = "*" + fullCode + "*";
            }
            fontX = this.font.getWidthPoint((this.altText != null) ? this.altText : fullCode, this.size);
        }
        int len = fCode.length() + 2;
        if (this.generateChecksum) {
            ++len;
        }
        float fullWidth = len * (6.0f * this.x + 3.0f * this.x * this.n) + (len - 1) * this.x;
        fullWidth = Math.max(fullWidth, fontX);
        final float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        String fullCode = this.code;
        float fontX = 0.0f;
        String bCode = this.code;
        if (this.extended) {
            bCode = getCode39Ex(this.code);
        }
        if (this.font != null) {
            if (this.generateChecksum && this.checksumText) {
                fullCode += getChecksum(bCode);
            }
            if (this.startStopText) {
                fullCode = "*" + fullCode + "*";
            }
            fontX = this.font.getWidthPoint(fullCode = ((this.altText != null) ? this.altText : fullCode), this.size);
        }
        if (this.generateChecksum) {
            bCode += getChecksum(bCode);
        }
        final int len = bCode.length() + 2;
        final float fullWidth = len * (6.0f * this.x + 3.0f * this.x * this.n) + (len - 1) * this.x;
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
        final byte[] bars = getBarsCode39(bCode);
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
        String bCode = this.code;
        if (this.extended) {
            bCode = getCode39Ex(this.code);
        }
        if (this.generateChecksum) {
            bCode += getChecksum(bCode);
        }
        final int len = bCode.length() + 2;
        final int nn = (int)this.n;
        final int fullWidth = len * (6 + 3 * nn) + (len - 1);
        final byte[] bars = getBarsCode39(bCode);
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
        BARS = new byte[][] { { 0, 0, 0, 1, 1, 0, 1, 0, 0 }, { 1, 0, 0, 1, 0, 0, 0, 0, 1 }, { 0, 0, 1, 1, 0, 0, 0, 0, 1 }, { 1, 0, 1, 1, 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 1, 0, 0, 0, 1 }, { 1, 0, 0, 1, 1, 0, 0, 0, 0 }, { 0, 0, 1, 1, 1, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 1, 0, 1 }, { 1, 0, 0, 1, 0, 0, 1, 0, 0 }, { 0, 0, 1, 1, 0, 0, 1, 0, 0 }, { 1, 0, 0, 0, 0, 1, 0, 0, 1 }, { 0, 0, 1, 0, 0, 1, 0, 0, 1 }, { 1, 0, 1, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 1, 1, 0, 0, 1 }, { 1, 0, 0, 0, 1, 1, 0, 0, 0 }, { 0, 0, 1, 0, 1, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 1, 1, 0, 1 }, { 1, 0, 0, 0, 0, 1, 1, 0, 0 }, { 0, 0, 1, 0, 0, 1, 1, 0, 0 }, { 0, 0, 0, 0, 1, 1, 1, 0, 0 }, { 1, 0, 0, 0, 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 0, 0, 0, 1, 1 }, { 1, 0, 1, 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1, 0, 0, 1, 1 }, { 1, 0, 0, 0, 1, 0, 0, 1, 0 }, { 0, 0, 1, 0, 1, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 1, 1, 0 }, { 0, 0, 1, 0, 0, 0, 1, 1, 0 }, { 0, 0, 0, 0, 1, 0, 1, 1, 0 }, { 1, 1, 0, 0, 0, 0, 0, 0, 1 }, { 0, 1, 1, 0, 0, 0, 0, 0, 1 }, { 1, 1, 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 1, 0, 0, 0, 1 }, { 1, 1, 0, 0, 1, 0, 0, 0, 0 }, { 0, 1, 1, 0, 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 0, 1, 0, 1 }, { 1, 1, 0, 0, 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0, 0, 1, 0, 0 }, { 0, 1, 0, 1, 0, 1, 0, 0, 0 }, { 0, 1, 0, 1, 0, 0, 0, 1, 0 }, { 0, 1, 0, 0, 0, 1, 0, 1, 0 }, { 0, 0, 0, 1, 0, 1, 0, 1, 0 }, { 0, 1, 0, 0, 1, 0, 1, 0, 0 } };
    }
}

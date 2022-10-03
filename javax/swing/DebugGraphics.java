package javax.swing;

import java.awt.Container;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.awt.Toolkit;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.Font;
import java.io.PrintStream;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;

public class DebugGraphics extends Graphics
{
    Graphics graphics;
    Image buffer;
    int debugOptions;
    int graphicsID;
    int xOffset;
    int yOffset;
    private static int graphicsCount;
    private static ImageIcon imageLoadingIcon;
    public static final int LOG_OPTION = 1;
    public static final int FLASH_OPTION = 2;
    public static final int BUFFERED_OPTION = 4;
    public static final int NONE_OPTION = -1;
    private static final Class debugGraphicsInfoKey;
    
    public DebugGraphics() {
        this.graphicsID = DebugGraphics.graphicsCount++;
        this.buffer = null;
        final int n = 0;
        this.yOffset = n;
        this.xOffset = n;
    }
    
    public DebugGraphics(final Graphics graphics, final JComponent component) {
        this(graphics);
        this.setDebugOptions(component.shouldDebugGraphics());
    }
    
    public DebugGraphics(final Graphics graphics) {
        this();
        this.graphics = graphics;
    }
    
    @Override
    public Graphics create() {
        final DebugGraphics debugGraphics = new DebugGraphics();
        debugGraphics.graphics = this.graphics.create();
        debugGraphics.debugOptions = this.debugOptions;
        debugGraphics.buffer = this.buffer;
        return debugGraphics;
    }
    
    @Override
    public Graphics create(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphics debugGraphics = new DebugGraphics();
        debugGraphics.graphics = this.graphics.create(n, n2, n3, n4);
        debugGraphics.debugOptions = this.debugOptions;
        debugGraphics.buffer = this.buffer;
        debugGraphics.xOffset = this.xOffset + n;
        debugGraphics.yOffset = this.yOffset + n2;
        return debugGraphics;
    }
    
    public static void setFlashColor(final Color flashColor) {
        info().flashColor = flashColor;
    }
    
    public static Color flashColor() {
        return info().flashColor;
    }
    
    public static void setFlashTime(final int flashTime) {
        info().flashTime = flashTime;
    }
    
    public static int flashTime() {
        return info().flashTime;
    }
    
    public static void setFlashCount(final int flashCount) {
        info().flashCount = flashCount;
    }
    
    public static int flashCount() {
        return info().flashCount;
    }
    
    public static void setLogStream(final PrintStream stream) {
        info().stream = stream;
    }
    
    public static PrintStream logStream() {
        return info().stream;
    }
    
    @Override
    public void setFont(final Font font) {
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting font: " + font);
        }
        this.graphics.setFont(font);
    }
    
    @Override
    public Font getFont() {
        return this.graphics.getFont();
    }
    
    @Override
    public void setColor(final Color color) {
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting color: " + color);
        }
        this.graphics.setColor(color);
    }
    
    @Override
    public Color getColor() {
        return this.graphics.getColor();
    }
    
    @Override
    public FontMetrics getFontMetrics() {
        return this.graphics.getFontMetrics();
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return this.graphics.getFontMetrics(font);
    }
    
    @Override
    public void translate(final int n, final int n2) {
        if (this.debugLog()) {
            info().log(this.toShortString() + " Translating by: " + new Point(n, n2));
        }
        this.xOffset += n;
        this.yOffset += n2;
        this.graphics.translate(n, n2);
    }
    
    @Override
    public void setPaintMode() {
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting paint mode");
        }
        this.graphics.setPaintMode();
    }
    
    @Override
    public void setXORMode(final Color xorMode) {
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting XOR mode: " + xorMode);
        }
        this.graphics.setXORMode(xorMode);
    }
    
    @Override
    public Rectangle getClipBounds() {
        return this.graphics.getClipBounds();
    }
    
    @Override
    public void clipRect(final int n, final int n2, final int n3, final int n4) {
        this.graphics.clipRect(n, n2, n3, n4);
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting clipRect: " + new Rectangle(n, n2, n3, n4) + " New clipRect: " + this.graphics.getClip());
        }
    }
    
    @Override
    public void setClip(final int n, final int n2, final int n3, final int n4) {
        this.graphics.setClip(n, n2, n3, n4);
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting new clipRect: " + this.graphics.getClip());
        }
    }
    
    @Override
    public Shape getClip() {
        return this.graphics.getClip();
    }
    
    @Override
    public void setClip(final Shape clip) {
        this.graphics.setClip(clip);
        if (this.debugLog()) {
            info().log(this.toShortString() + " Setting new clipRect: " + this.graphics.getClip());
        }
    }
    
    @Override
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing rect: " + new Rectangle(n, n2, n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawRect(n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawRect(n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawRect(n, n2, n3, n4);
    }
    
    @Override
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Filling rect: " + new Rectangle(n, n2, n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.fillRect(n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.fillRect(n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.fillRect(n, n2, n3, n4);
    }
    
    @Override
    public void clearRect(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Clearing rect: " + new Rectangle(n, n2, n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.clearRect(n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.clearRect(n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.clearRect(n, n2, n3, n4);
    }
    
    @Override
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing round rect: " + new Rectangle(n, n2, n3, n4) + " arcWidth: " + n5 + " archHeight: " + n6);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawRoundRect(n, n2, n3, n4, n5, n6);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n7 = info.flashCount * 2 - 1, i = 0; i < n7; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawRoundRect(n, n2, n3, n4, n5, n6);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Filling round rect: " + new Rectangle(n, n2, n3, n4) + " arcWidth: " + n5 + " archHeight: " + n6);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.fillRoundRect(n, n2, n3, n4, n5, n6);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n7 = info.flashCount * 2 - 1, i = 0; i < n7; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.fillRoundRect(n, n2, n3, n4, n5, n6);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.fillRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing line: from " + this.pointToString(n, n2) + " to " + this.pointToString(n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawLine(n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawLine(n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawLine(n, n2, n3, n4);
    }
    
    @Override
    public void draw3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing 3D rect: " + new Rectangle(n, n2, n3, n4) + " Raised bezel: " + b);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.draw3DRect(n, n2, n3, n4, b);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.draw3DRect(n, n2, n3, n4, b);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.draw3DRect(n, n2, n3, n4, b);
    }
    
    @Override
    public void fill3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Filling 3D rect: " + new Rectangle(n, n2, n3, n4) + " Raised bezel: " + b);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.fill3DRect(n, n2, n3, n4, b);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.fill3DRect(n, n2, n3, n4, b);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.fill3DRect(n, n2, n3, n4, b);
    }
    
    @Override
    public void drawOval(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing oval: " + new Rectangle(n, n2, n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawOval(n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawOval(n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawOval(n, n2, n3, n4);
    }
    
    @Override
    public void fillOval(final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Filling oval: " + new Rectangle(n, n2, n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.fillOval(n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.fillOval(n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.fillOval(n, n2, n3, n4);
    }
    
    @Override
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing arc: " + new Rectangle(n, n2, n3, n4) + " startAngle: " + n5 + " arcAngle: " + n6);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawArc(n, n2, n3, n4, n5, n6);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n7 = info.flashCount * 2 - 1, i = 0; i < n7; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawArc(n, n2, n3, n4, n5, n6);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawArc(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Filling arc: " + new Rectangle(n, n2, n3, n4) + " startAngle: " + n5 + " arcAngle: " + n6);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.fillArc(n, n2, n3, n4, n5, n6);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n7 = info.flashCount * 2 - 1, i = 0; i < n7; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.fillArc(n, n2, n3, n4, n5, n6);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.fillArc(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawPolyline(final int[] array, final int[] array2, final int n) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing polyline:  nPoints: " + n + " X's: " + array + " Y's: " + array2);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawPolyline(array, array2, n);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n2 = info.flashCount * 2 - 1, i = 0; i < n2; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawPolyline(array, array2, n);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawPolyline(array, array2, n);
    }
    
    @Override
    public void drawPolygon(final int[] array, final int[] array2, final int n) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing polygon:  nPoints: " + n + " X's: " + array + " Y's: " + array2);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawPolygon(array, array2, n);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n2 = info.flashCount * 2 - 1, i = 0; i < n2; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawPolygon(array, array2, n);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawPolygon(array, array2, n);
    }
    
    @Override
    public void fillPolygon(final int[] array, final int[] array2, final int n) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Filling polygon:  nPoints: " + n + " X's: " + array + " Y's: " + array2);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.fillPolygon(array, array2, n);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n2 = info.flashCount * 2 - 1, i = 0; i < n2; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.fillPolygon(array, array2, n);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.fillPolygon(array, array2, n);
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing string: \"" + s + "\" at: " + new Point(n, n2));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawString(s, n, n2);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n3 = info.flashCount * 2 - 1, i = 0; i < n3; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawString(s, n, n2);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawString(s, n, n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing text: \"" + attributedCharacterIterator + "\" at: " + new Point(n, n2));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawString(attributedCharacterIterator, n, n2);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n3 = info.flashCount * 2 - 1, i = 0; i < n3; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawString(attributedCharacterIterator, n, n2);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawString(attributedCharacterIterator, n, n2);
    }
    
    @Override
    public void drawBytes(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        this.graphics.getFont();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing bytes at: " + new Point(n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawBytes(array, n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawBytes(array, n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawBytes(array, n, n2, n3, n4);
    }
    
    @Override
    public void drawChars(final char[] array, final int n, final int n2, final int n3, final int n4) {
        final DebugGraphicsInfo info = info();
        this.graphics.getFont();
        if (this.debugLog()) {
            info().log(this.toShortString() + " Drawing chars at " + new Point(n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawChars(array, n, n2, n3, n4);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final Color color = this.getColor();
            for (int n5 = info.flashCount * 2 - 1, i = 0; i < n5; ++i) {
                this.graphics.setColor((i % 2 == 0) ? info.flashColor : color);
                this.graphics.drawChars(array, n, n2, n3, n4);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
            this.graphics.setColor(color);
        }
        this.graphics.drawChars(array, n, n2, n3, n4);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info.log(this.toShortString() + " Drawing image: " + image + " at: " + new Point(n, n2));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawImage(image, n, n2, imageObserver);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final int n3 = info.flashCount * 2 - 1;
            final Image image2 = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new DebugGraphicsFilter(info.flashColor)));
            final DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
            for (int i = 0; i < n3; ++i) {
                final Image image3 = (i % 2 == 0) ? image2 : image;
                loadImage(image3);
                this.graphics.drawImage(image3, n, n2, debugGraphicsObserver);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
        }
        return this.graphics.drawImage(image, n, n2, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final ImageObserver imageObserver) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info.log(this.toShortString() + " Drawing image: " + image + " at: " + new Rectangle(n, n2, n3, n4));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawImage(image, n, n2, n3, n4, imageObserver);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final int n5 = info.flashCount * 2 - 1;
            final Image image2 = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new DebugGraphicsFilter(info.flashColor)));
            final DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
            for (int i = 0; i < n5; ++i) {
                final Image image3 = (i % 2 == 0) ? image2 : image;
                loadImage(image3);
                this.graphics.drawImage(image3, n, n2, n3, n4, debugGraphicsObserver);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
        }
        return this.graphics.drawImage(image, n, n2, n3, n4, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info.log(this.toShortString() + " Drawing image: " + image + " at: " + new Point(n, n2) + ", bgcolor: " + color);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawImage(image, n, n2, color, imageObserver);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final int n3 = info.flashCount * 2 - 1;
            final Image image2 = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new DebugGraphicsFilter(info.flashColor)));
            final DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
            for (int i = 0; i < n3; ++i) {
                final Image image3 = (i % 2 == 0) ? image2 : image;
                loadImage(image3);
                this.graphics.drawImage(image3, n, n2, color, debugGraphicsObserver);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
        }
        return this.graphics.drawImage(image, n, n2, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info.log(this.toShortString() + " Drawing image: " + image + " at: " + new Rectangle(n, n2, n3, n4) + ", bgcolor: " + color);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawImage(image, n, n2, n3, n4, color, imageObserver);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final int n5 = info.flashCount * 2 - 1;
            final Image image2 = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new DebugGraphicsFilter(info.flashColor)));
            final DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
            for (int i = 0; i < n5; ++i) {
                final Image image3 = (i % 2 == 0) ? image2 : image;
                loadImage(image3);
                this.graphics.drawImage(image3, n, n2, n3, n4, color, debugGraphicsObserver);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
        }
        return this.graphics.drawImage(image, n, n2, n3, n4, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ImageObserver imageObserver) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info.log(this.toShortString() + " Drawing image: " + image + " destination: " + new Rectangle(n, n2, n3, n4) + " source: " + new Rectangle(n5, n6, n7, n8));
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, imageObserver);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final int n9 = info.flashCount * 2 - 1;
            final Image image2 = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new DebugGraphicsFilter(info.flashColor)));
            final DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
            for (int i = 0; i < n9; ++i) {
                final Image image3 = (i % 2 == 0) ? image2 : image;
                loadImage(image3);
                this.graphics.drawImage(image3, n, n2, n3, n4, n5, n6, n7, n8, debugGraphicsObserver);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
        }
        return this.graphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        final DebugGraphicsInfo info = info();
        if (this.debugLog()) {
            info.log(this.toShortString() + " Drawing image: " + image + " destination: " + new Rectangle(n, n2, n3, n4) + " source: " + new Rectangle(n5, n6, n7, n8) + ", bgcolor: " + color);
        }
        if (this.isDrawingBuffer()) {
            if (this.debugBuffered()) {
                final Graphics debugGraphics = this.debugGraphics();
                debugGraphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
                debugGraphics.dispose();
            }
        }
        else if (this.debugFlash()) {
            final int n9 = info.flashCount * 2 - 1;
            final Image image2 = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new DebugGraphicsFilter(info.flashColor)));
            final DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
            for (int i = 0; i < n9; ++i) {
                final Image image3 = (i % 2 == 0) ? image2 : image;
                loadImage(image3);
                this.graphics.drawImage(image3, n, n2, n3, n4, n5, n6, n7, n8, color, debugGraphicsObserver);
                Toolkit.getDefaultToolkit().sync();
                this.sleep(info.flashTime);
            }
        }
        return this.graphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
    }
    
    static void loadImage(final Image image) {
        DebugGraphics.imageLoadingIcon.loadImage(image);
    }
    
    @Override
    public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.debugLog()) {
            info().log(this.toShortString() + " Copying area from: " + new Rectangle(n, n2, n3, n4) + " to: " + new Point(n5, n6));
        }
        this.graphics.copyArea(n, n2, n3, n4, n5, n6);
    }
    
    final void sleep(final int n) {
        try {
            Thread.sleep(n);
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public void dispose() {
        this.graphics.dispose();
        this.graphics = null;
    }
    
    public boolean isDrawingBuffer() {
        return this.buffer != null;
    }
    
    String toShortString() {
        return "Graphics" + (this.isDrawingBuffer() ? "<B>" : "") + "(" + this.graphicsID + "-" + this.debugOptions + ")";
    }
    
    String pointToString(final int n, final int n2) {
        return "(" + n + ", " + n2 + ")";
    }
    
    public void setDebugOptions(final int n) {
        if (n != 0) {
            if (n == -1) {
                if (this.debugOptions != 0) {
                    System.err.println(this.toShortString() + " Disabling debug");
                    this.debugOptions = 0;
                }
            }
            else if (this.debugOptions != n) {
                this.debugOptions |= n;
                if (this.debugLog()) {
                    System.err.println(this.toShortString() + " Enabling debug");
                }
            }
        }
    }
    
    public int getDebugOptions() {
        return this.debugOptions;
    }
    
    static void setDebugOptions(final JComponent component, final int n) {
        info().setDebugOptions(component, n);
    }
    
    static int getDebugOptions(final JComponent component) {
        final DebugGraphicsInfo info = info();
        if (info == null) {
            return 0;
        }
        return info.getDebugOptions(component);
    }
    
    static int shouldComponentDebug(final JComponent component) {
        final DebugGraphicsInfo info = info();
        if (info == null) {
            return 0;
        }
        Container parent = component;
        int n = 0;
        while (parent != null && parent instanceof JComponent) {
            n |= info.getDebugOptions((JComponent)parent);
            parent = parent.getParent();
        }
        return n;
    }
    
    static int debugComponentCount() {
        final DebugGraphicsInfo info = info();
        if (info != null && info.componentToDebug != null) {
            return info.componentToDebug.size();
        }
        return 0;
    }
    
    boolean debugLog() {
        return (this.debugOptions & 0x1) == 0x1;
    }
    
    boolean debugFlash() {
        return (this.debugOptions & 0x2) == 0x2;
    }
    
    boolean debugBuffered() {
        return (this.debugOptions & 0x4) == 0x4;
    }
    
    private Graphics debugGraphics() {
        final DebugGraphicsInfo info = info();
        if (info.debugFrame == null) {
            (info.debugFrame = new JFrame()).setSize(500, 500);
        }
        final JFrame debugFrame = info.debugFrame;
        debugFrame.show();
        final DebugGraphics debugGraphics = new DebugGraphics(debugFrame.getGraphics());
        debugGraphics.setFont(this.getFont());
        debugGraphics.setColor(this.getColor());
        debugGraphics.translate(this.xOffset, this.yOffset);
        debugGraphics.setClip(this.getClipBounds());
        if (this.debugFlash()) {
            debugGraphics.setDebugOptions(2);
        }
        return debugGraphics;
    }
    
    static DebugGraphicsInfo info() {
        DebugGraphicsInfo debugGraphicsInfo = (DebugGraphicsInfo)SwingUtilities.appContextGet(DebugGraphics.debugGraphicsInfoKey);
        if (debugGraphicsInfo == null) {
            debugGraphicsInfo = new DebugGraphicsInfo();
            SwingUtilities.appContextPut(DebugGraphics.debugGraphicsInfoKey, debugGraphicsInfo);
        }
        return debugGraphicsInfo;
    }
    
    static {
        DebugGraphics.graphicsCount = 0;
        DebugGraphics.imageLoadingIcon = new ImageIcon();
        JComponent.DEBUG_GRAPHICS_LOADED = true;
        debugGraphicsInfoKey = DebugGraphicsInfo.class;
    }
}

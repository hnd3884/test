package java.awt;

import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public abstract class Graphics
{
    protected Graphics() {
    }
    
    public abstract Graphics create();
    
    public Graphics create(final int n, final int n2, final int n3, final int n4) {
        final Graphics create = this.create();
        if (create == null) {
            return null;
        }
        create.translate(n, n2);
        create.clipRect(0, 0, n3, n4);
        return create;
    }
    
    public abstract void translate(final int p0, final int p1);
    
    public abstract Color getColor();
    
    public abstract void setColor(final Color p0);
    
    public abstract void setPaintMode();
    
    public abstract void setXORMode(final Color p0);
    
    public abstract Font getFont();
    
    public abstract void setFont(final Font p0);
    
    public FontMetrics getFontMetrics() {
        return this.getFontMetrics(this.getFont());
    }
    
    public abstract FontMetrics getFontMetrics(final Font p0);
    
    public abstract Rectangle getClipBounds();
    
    public abstract void clipRect(final int p0, final int p1, final int p2, final int p3);
    
    public abstract void setClip(final int p0, final int p1, final int p2, final int p3);
    
    public abstract Shape getClip();
    
    public abstract void setClip(final Shape p0);
    
    public abstract void copyArea(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    public abstract void drawLine(final int p0, final int p1, final int p2, final int p3);
    
    public abstract void fillRect(final int p0, final int p1, final int p2, final int p3);
    
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        if (n3 < 0 || n4 < 0) {
            return;
        }
        if (n4 == 0 || n3 == 0) {
            this.drawLine(n, n2, n + n3, n2 + n4);
        }
        else {
            this.drawLine(n, n2, n + n3 - 1, n2);
            this.drawLine(n + n3, n2, n + n3, n2 + n4 - 1);
            this.drawLine(n + n3, n2 + n4, n + 1, n2 + n4);
            this.drawLine(n, n2 + n4, n, n2 + 1);
        }
    }
    
    public abstract void clearRect(final int p0, final int p1, final int p2, final int p3);
    
    public abstract void drawRoundRect(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    public abstract void fillRoundRect(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    public void draw3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final Color color = this.getColor();
        final Color brighter = color.brighter();
        final Color darker = color.darker();
        this.setColor(b ? brighter : darker);
        this.drawLine(n, n2, n, n2 + n4);
        this.drawLine(n + 1, n2, n + n3 - 1, n2);
        this.setColor(b ? darker : brighter);
        this.drawLine(n + 1, n2 + n4, n + n3, n2 + n4);
        this.drawLine(n + n3, n2, n + n3, n2 + n4 - 1);
        this.setColor(color);
    }
    
    public void fill3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final Color color = this.getColor();
        final Color brighter = color.brighter();
        final Color darker = color.darker();
        if (!b) {
            this.setColor(darker);
        }
        this.fillRect(n + 1, n2 + 1, n3 - 2, n4 - 2);
        this.setColor(b ? brighter : darker);
        this.drawLine(n, n2, n, n2 + n4 - 1);
        this.drawLine(n + 1, n2, n + n3 - 2, n2);
        this.setColor(b ? darker : brighter);
        this.drawLine(n + 1, n2 + n4 - 1, n + n3 - 1, n2 + n4 - 1);
        this.drawLine(n + n3 - 1, n2, n + n3 - 1, n2 + n4 - 2);
        this.setColor(color);
    }
    
    public abstract void drawOval(final int p0, final int p1, final int p2, final int p3);
    
    public abstract void fillOval(final int p0, final int p1, final int p2, final int p3);
    
    public abstract void drawArc(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    public abstract void fillArc(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    public abstract void drawPolyline(final int[] p0, final int[] p1, final int p2);
    
    public abstract void drawPolygon(final int[] p0, final int[] p1, final int p2);
    
    public void drawPolygon(final Polygon polygon) {
        this.drawPolygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }
    
    public abstract void fillPolygon(final int[] p0, final int[] p1, final int p2);
    
    public void fillPolygon(final Polygon polygon) {
        this.fillPolygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }
    
    public abstract void drawString(final String p0, final int p1, final int p2);
    
    public abstract void drawString(final AttributedCharacterIterator p0, final int p1, final int p2);
    
    public void drawChars(final char[] array, final int n, final int n2, final int n3, final int n4) {
        this.drawString(new String(array, n, n2), n3, n4);
    }
    
    public void drawBytes(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        this.drawString(new String(array, 0, n, n2), n3, n4);
    }
    
    public abstract boolean drawImage(final Image p0, final int p1, final int p2, final ImageObserver p3);
    
    public abstract boolean drawImage(final Image p0, final int p1, final int p2, final int p3, final int p4, final ImageObserver p5);
    
    public abstract boolean drawImage(final Image p0, final int p1, final int p2, final Color p3, final ImageObserver p4);
    
    public abstract boolean drawImage(final Image p0, final int p1, final int p2, final int p3, final int p4, final Color p5, final ImageObserver p6);
    
    public abstract boolean drawImage(final Image p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final ImageObserver p9);
    
    public abstract boolean drawImage(final Image p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final Color p9, final ImageObserver p10);
    
    public abstract void dispose();
    
    public void finalize() {
        this.dispose();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[font=" + this.getFont() + ",color=" + this.getColor() + "]";
    }
    
    @Deprecated
    public Rectangle getClipRect() {
        return this.getClipBounds();
    }
    
    public boolean hitClip(final int n, final int n2, final int n3, final int n4) {
        final Rectangle clipBounds = this.getClipBounds();
        return clipBounds == null || clipBounds.intersects(n, n2, n3, n4);
    }
    
    public Rectangle getClipBounds(final Rectangle rectangle) {
        final Rectangle clipBounds = this.getClipBounds();
        if (clipBounds != null) {
            rectangle.x = clipBounds.x;
            rectangle.y = clipBounds.y;
            rectangle.width = clipBounds.width;
            rectangle.height = clipBounds.height;
        }
        else if (rectangle == null) {
            throw new NullPointerException("null rectangle parameter");
        }
        return rectangle;
    }
}

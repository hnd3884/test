package sun.print;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.text.AttributedCharacterIterator;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;

public class ProxyGraphics extends Graphics
{
    private Graphics g;
    
    public ProxyGraphics(final Graphics g) {
        this.g = g;
    }
    
    Graphics getGraphics() {
        return this.g;
    }
    
    @Override
    public Graphics create() {
        return new ProxyGraphics(this.g.create());
    }
    
    @Override
    public Graphics create(final int n, final int n2, final int n3, final int n4) {
        return new ProxyGraphics(this.g.create(n, n2, n3, n4));
    }
    
    @Override
    public void translate(final int n, final int n2) {
        this.g.translate(n, n2);
    }
    
    @Override
    public Color getColor() {
        return this.g.getColor();
    }
    
    @Override
    public void setColor(final Color color) {
        this.g.setColor(color);
    }
    
    @Override
    public void setPaintMode() {
        this.g.setPaintMode();
    }
    
    @Override
    public void setXORMode(final Color xorMode) {
        this.g.setXORMode(xorMode);
    }
    
    @Override
    public Font getFont() {
        return this.g.getFont();
    }
    
    @Override
    public void setFont(final Font font) {
        this.g.setFont(font);
    }
    
    @Override
    public FontMetrics getFontMetrics() {
        return this.g.getFontMetrics();
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return this.g.getFontMetrics(font);
    }
    
    @Override
    public Rectangle getClipBounds() {
        return this.g.getClipBounds();
    }
    
    @Override
    public void clipRect(final int n, final int n2, final int n3, final int n4) {
        this.g.clipRect(n, n2, n3, n4);
    }
    
    @Override
    public void setClip(final int n, final int n2, final int n3, final int n4) {
        this.g.setClip(n, n2, n3, n4);
    }
    
    @Override
    public Shape getClip() {
        return this.g.getClip();
    }
    
    @Override
    public void setClip(final Shape clip) {
        this.g.setClip(clip);
    }
    
    @Override
    public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.g.copyArea(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        this.g.drawLine(n, n2, n3, n4);
    }
    
    @Override
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        this.g.fillRect(n, n2, n3, n4);
    }
    
    @Override
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        this.g.drawRect(n, n2, n3, n4);
    }
    
    @Override
    public void clearRect(final int n, final int n2, final int n3, final int n4) {
        this.g.clearRect(n, n2, n3, n4);
    }
    
    @Override
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.g.drawRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.g.fillRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void draw3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        this.g.draw3DRect(n, n2, n3, n4, b);
    }
    
    @Override
    public void fill3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        this.g.fill3DRect(n, n2, n3, n4, b);
    }
    
    @Override
    public void drawOval(final int n, final int n2, final int n3, final int n4) {
        this.g.drawOval(n, n2, n3, n4);
    }
    
    @Override
    public void fillOval(final int n, final int n2, final int n3, final int n4) {
        this.g.fillOval(n, n2, n3, n4);
    }
    
    @Override
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.g.drawArc(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.g.fillArc(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawPolyline(final int[] array, final int[] array2, final int n) {
        this.g.drawPolyline(array, array2, n);
    }
    
    @Override
    public void drawPolygon(final int[] array, final int[] array2, final int n) {
        this.g.drawPolygon(array, array2, n);
    }
    
    @Override
    public void drawPolygon(final Polygon polygon) {
        this.g.drawPolygon(polygon);
    }
    
    @Override
    public void fillPolygon(final int[] array, final int[] array2, final int n) {
        this.g.fillPolygon(array, array2, n);
    }
    
    @Override
    public void fillPolygon(final Polygon polygon) {
        this.g.fillPolygon(polygon);
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        this.g.drawString(s, n, n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        this.g.drawString(attributedCharacterIterator, n, n2);
    }
    
    @Override
    public void drawChars(final char[] array, final int n, final int n2, final int n3, final int n4) {
        this.g.drawChars(array, n, n2, n3, n4);
    }
    
    @Override
    public void drawBytes(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        this.g.drawBytes(array, n, n2, n3, n4);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return this.g.drawImage(image, n, n2, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final ImageObserver imageObserver) {
        return this.g.drawImage(image, n, n2, n3, n4, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        return this.g.drawImage(image, n, n2, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        return this.g.drawImage(image, n, n2, n3, n4, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ImageObserver imageObserver) {
        return this.g.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        return this.g.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
    }
    
    @Override
    public void dispose() {
        this.g.dispose();
    }
    
    @Override
    public void finalize() {
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[font=" + this.getFont() + ",color=" + this.getColor() + "]";
    }
    
    @Deprecated
    @Override
    public Rectangle getClipRect() {
        return this.g.getClipRect();
    }
    
    @Override
    public boolean hitClip(final int n, final int n2, final int n3, final int n4) {
        return this.g.hitClip(n, n2, n3, n4);
    }
    
    @Override
    public Rectangle getClipBounds(final Rectangle rectangle) {
        return this.g.getClipBounds(rectangle);
    }
}

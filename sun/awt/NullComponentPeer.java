package sun.awt;

import java.awt.peer.ComponentPeer;
import sun.java2d.pipe.Region;
import java.awt.Rectangle;
import java.awt.peer.ContainerPeer;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.image.VolatileImage;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import java.awt.Dimension;
import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.event.PaintEvent;
import java.awt.Graphics;
import java.awt.peer.PanelPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.LightweightPeer;

public class NullComponentPeer implements LightweightPeer, CanvasPeer, PanelPeer
{
    @Override
    public boolean isObscured() {
        return false;
    }
    
    @Override
    public boolean canDetermineObscurity() {
        return false;
    }
    
    @Override
    public boolean isFocusable() {
        return false;
    }
    
    @Override
    public void setVisible(final boolean b) {
    }
    
    public void show() {
    }
    
    public void hide() {
    }
    
    @Override
    public void setEnabled(final boolean b) {
    }
    
    public void enable() {
    }
    
    public void disable() {
    }
    
    @Override
    public void paint(final Graphics graphics) {
    }
    
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
    }
    
    @Override
    public void print(final Graphics graphics) {
    }
    
    @Override
    public void setBounds(final int n, final int n2, final int n3, final int n4, final int n5) {
    }
    
    public void reshape(final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public void coalescePaintEvent(final PaintEvent paintEvent) {
    }
    
    public boolean handleEvent(final Event event) {
        return false;
    }
    
    @Override
    public void handleEvent(final AWTEvent awtEvent) {
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1, 1);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(1, 1);
    }
    
    @Override
    public ColorModel getColorModel() {
        return null;
    }
    
    @Override
    public Graphics getGraphics() {
        return null;
    }
    
    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return null;
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return null;
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void setForeground(final Color color) {
    }
    
    @Override
    public void setBackground(final Color color) {
    }
    
    @Override
    public void setFont(final Font font) {
    }
    
    @Override
    public void updateCursorImmediately() {
    }
    
    public void setCursor(final Cursor cursor) {
    }
    
    @Override
    public boolean requestFocus(final Component component, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
        return false;
    }
    
    @Override
    public Image createImage(final ImageProducer imageProducer) {
        return null;
    }
    
    @Override
    public Image createImage(final int n, final int n2) {
        return null;
    }
    
    @Override
    public boolean prepareImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return false;
    }
    
    @Override
    public int checkImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return 0;
    }
    
    public Dimension preferredSize() {
        return this.getPreferredSize();
    }
    
    public Dimension minimumSize() {
        return this.getMinimumSize();
    }
    
    @Override
    public Point getLocationOnScreen() {
        return new Point(0, 0);
    }
    
    @Override
    public Insets getInsets() {
        return this.insets();
    }
    
    @Override
    public void beginValidate() {
    }
    
    @Override
    public void endValidate() {
    }
    
    public Insets insets() {
        return new Insets(0, 0, 0, 0);
    }
    
    public boolean isPaintPending() {
        return false;
    }
    
    @Override
    public boolean handlesWheelScrolling() {
        return false;
    }
    
    @Override
    public VolatileImage createVolatileImage(final int n, final int n2) {
        return null;
    }
    
    @Override
    public void beginLayout() {
    }
    
    @Override
    public void endLayout() {
    }
    
    @Override
    public void createBuffers(final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        throw new AWTException("Page-flipping is not allowed on a lightweight component");
    }
    
    @Override
    public Image getBackBuffer() {
        throw new IllegalStateException("Page-flipping is not allowed on a lightweight component");
    }
    
    @Override
    public void flip(final int n, final int n2, final int n3, final int n4, final BufferCapabilities.FlipContents flipContents) {
        throw new IllegalStateException("Page-flipping is not allowed on a lightweight component");
    }
    
    @Override
    public void destroyBuffers() {
    }
    
    @Override
    public boolean isReparentSupported() {
        return false;
    }
    
    @Override
    public void reparent(final ContainerPeer containerPeer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void layout() {
    }
    
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 0, 0);
    }
    
    @Override
    public void applyShape(final Region region) {
    }
    
    @Override
    public void setZOrder(final ComponentPeer componentPeer) {
    }
    
    @Override
    public boolean updateGraphicsData(final GraphicsConfiguration graphicsConfiguration) {
        return false;
    }
    
    @Override
    public GraphicsConfiguration getAppropriateGraphicsConfiguration(final GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration;
    }
}

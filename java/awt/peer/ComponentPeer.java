package java.awt.peer;

import sun.java2d.pipe.Region;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.GraphicsConfiguration;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.Image;
import java.awt.image.ImageProducer;
import sun.awt.CausedFocusEvent;
import java.awt.Component;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.image.ColorModel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.PaintEvent;
import java.awt.AWTEvent;
import java.awt.Graphics;

public interface ComponentPeer
{
    public static final int SET_LOCATION = 1;
    public static final int SET_SIZE = 2;
    public static final int SET_BOUNDS = 3;
    public static final int SET_CLIENT_SIZE = 4;
    public static final int RESET_OPERATION = 5;
    public static final int NO_EMBEDDED_CHECK = 16384;
    public static final int DEFAULT_OPERATION = 3;
    
    boolean isObscured();
    
    boolean canDetermineObscurity();
    
    void setVisible(final boolean p0);
    
    void setEnabled(final boolean p0);
    
    void paint(final Graphics p0);
    
    void print(final Graphics p0);
    
    void setBounds(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void handleEvent(final AWTEvent p0);
    
    void coalescePaintEvent(final PaintEvent p0);
    
    Point getLocationOnScreen();
    
    Dimension getPreferredSize();
    
    Dimension getMinimumSize();
    
    ColorModel getColorModel();
    
    Graphics getGraphics();
    
    FontMetrics getFontMetrics(final Font p0);
    
    void dispose();
    
    void setForeground(final Color p0);
    
    void setBackground(final Color p0);
    
    void setFont(final Font p0);
    
    void updateCursorImmediately();
    
    boolean requestFocus(final Component p0, final boolean p1, final boolean p2, final long p3, final CausedFocusEvent.Cause p4);
    
    boolean isFocusable();
    
    Image createImage(final ImageProducer p0);
    
    Image createImage(final int p0, final int p1);
    
    VolatileImage createVolatileImage(final int p0, final int p1);
    
    boolean prepareImage(final Image p0, final int p1, final int p2, final ImageObserver p3);
    
    int checkImage(final Image p0, final int p1, final int p2, final ImageObserver p3);
    
    GraphicsConfiguration getGraphicsConfiguration();
    
    boolean handlesWheelScrolling();
    
    void createBuffers(final int p0, final BufferCapabilities p1) throws AWTException;
    
    Image getBackBuffer();
    
    void flip(final int p0, final int p1, final int p2, final int p3, final BufferCapabilities.FlipContents p4);
    
    void destroyBuffers();
    
    void reparent(final ContainerPeer p0);
    
    boolean isReparentSupported();
    
    void layout();
    
    void applyShape(final Region p0);
    
    void setZOrder(final ComponentPeer p0);
    
    boolean updateGraphicsData(final GraphicsConfiguration p0);
}

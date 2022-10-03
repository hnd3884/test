package sun.awt.image;

import sun.java2d.Surface;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import sun.java2d.SunGraphics2D;
import java.awt.Graphics2D;
import java.awt.Color;
import sun.java2d.SurfaceManagerFactory;
import sun.print.PrinterGraphicsConfig;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Component;
import sun.java2d.DestSurfaceProvider;
import java.awt.image.VolatileImage;

public class SunVolatileImage extends VolatileImage implements DestSurfaceProvider
{
    protected VolatileSurfaceManager volSurfaceManager;
    protected Component comp;
    private GraphicsConfiguration graphicsConfig;
    private Font defaultFont;
    private int width;
    private int height;
    private int forcedAccelSurfaceType;
    
    protected SunVolatileImage(final Component comp, final GraphicsConfiguration graphicsConfig, final int width, final int height, final Object o, final int transparency, final ImageCapabilities imageCapabilities, final int forcedAccelSurfaceType) {
        this.comp = comp;
        this.graphicsConfig = graphicsConfig;
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width (" + width + ") and height (" + height + ") cannot be <= 0");
        }
        this.width = width;
        this.height = height;
        this.forcedAccelSurfaceType = forcedAccelSurfaceType;
        if (transparency != 1 && transparency != 2 && transparency != 3) {
            throw new IllegalArgumentException("Unknown transparency type:" + transparency);
        }
        this.transparency = transparency;
        SurfaceManager.setManager(this, this.volSurfaceManager = this.createSurfaceManager(o, imageCapabilities));
        this.volSurfaceManager.initialize();
        this.volSurfaceManager.initContents();
    }
    
    private SunVolatileImage(final Component component, final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object o, final ImageCapabilities imageCapabilities) {
        this(component, graphicsConfiguration, n, n2, o, 1, imageCapabilities, 0);
    }
    
    public SunVolatileImage(final Component component, final int n, final int n2) {
        this(component, n, n2, null);
    }
    
    public SunVolatileImage(final Component component, final int n, final int n2, final Object o) {
        this(component, component.getGraphicsConfiguration(), n, n2, o, null);
    }
    
    public SunVolatileImage(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final int n3, final ImageCapabilities imageCapabilities) {
        this(null, graphicsConfiguration, n, n2, null, n3, imageCapabilities, 0);
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    public GraphicsConfiguration getGraphicsConfig() {
        return this.graphicsConfig;
    }
    
    public void updateGraphicsConfig() {
        if (this.comp != null) {
            final GraphicsConfiguration graphicsConfiguration = this.comp.getGraphicsConfiguration();
            if (graphicsConfiguration != null) {
                this.graphicsConfig = graphicsConfiguration;
            }
        }
    }
    
    public Component getComponent() {
        return this.comp;
    }
    
    public int getForcedAccelSurfaceType() {
        return this.forcedAccelSurfaceType;
    }
    
    protected VolatileSurfaceManager createSurfaceManager(final Object o, final ImageCapabilities imageCapabilities) {
        if (this.graphicsConfig instanceof BufferedImageGraphicsConfig || this.graphicsConfig instanceof PrinterGraphicsConfig || (imageCapabilities != null && !imageCapabilities.isAccelerated())) {
            return new BufImgVolatileSurfaceManager(this, o);
        }
        return SurfaceManagerFactory.getInstance().createVolatileManager(this, o);
    }
    
    private Color getForeground() {
        if (this.comp != null) {
            return this.comp.getForeground();
        }
        return Color.black;
    }
    
    private Color getBackground() {
        if (this.comp != null) {
            return this.comp.getBackground();
        }
        return Color.white;
    }
    
    private Font getFont() {
        if (this.comp != null) {
            return this.comp.getFont();
        }
        if (this.defaultFont == null) {
            this.defaultFont = new Font("Dialog", 0, 12);
        }
        return this.defaultFont;
    }
    
    @Override
    public Graphics2D createGraphics() {
        return new SunGraphics2D(this.volSurfaceManager.getPrimarySurfaceData(), this.getForeground(), this.getBackground(), this.getFont());
    }
    
    @Override
    public Object getProperty(final String s, final ImageObserver imageObserver) {
        if (s == null) {
            throw new NullPointerException("null property name is not allowed");
        }
        return Image.UndefinedProperty;
    }
    
    @Override
    public int getWidth(final ImageObserver imageObserver) {
        return this.getWidth();
    }
    
    @Override
    public int getHeight(final ImageObserver imageObserver) {
        return this.getHeight();
    }
    
    public BufferedImage getBackupImage() {
        return this.graphicsConfig.createCompatibleImage(this.getWidth(), this.getHeight(), this.getTransparency());
    }
    
    @Override
    public BufferedImage getSnapshot() {
        final BufferedImage backupImage = this.getBackupImage();
        final Graphics2D graphics = backupImage.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.drawImage(this, 0, 0, null);
        graphics.dispose();
        return backupImage;
    }
    
    @Override
    public int validate(final GraphicsConfiguration graphicsConfiguration) {
        return this.volSurfaceManager.validate(graphicsConfiguration);
    }
    
    @Override
    public boolean contentsLost() {
        return this.volSurfaceManager.contentsLost();
    }
    
    @Override
    public ImageCapabilities getCapabilities() {
        return this.volSurfaceManager.getCapabilities(this.graphicsConfig);
    }
    
    @Override
    public Surface getDestSurface() {
        return this.volSurfaceManager.getPrimarySurfaceData();
    }
}

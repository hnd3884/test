package java.awt;

import sun.awt.SunToolkit;
import sun.awt.AppContext;

public abstract class GraphicsDevice
{
    private Window fullScreenWindow;
    private AppContext fullScreenAppContext;
    private final Object fsAppContextLock;
    private Rectangle windowedModeBounds;
    public static final int TYPE_RASTER_SCREEN = 0;
    public static final int TYPE_PRINTER = 1;
    public static final int TYPE_IMAGE_BUFFER = 2;
    
    protected GraphicsDevice() {
        this.fsAppContextLock = new Object();
    }
    
    public abstract int getType();
    
    public abstract String getIDstring();
    
    public abstract GraphicsConfiguration[] getConfigurations();
    
    public abstract GraphicsConfiguration getDefaultConfiguration();
    
    public GraphicsConfiguration getBestConfiguration(final GraphicsConfigTemplate graphicsConfigTemplate) {
        return graphicsConfigTemplate.getBestConfiguration(this.getConfigurations());
    }
    
    public boolean isFullScreenSupported() {
        return false;
    }
    
    public void setFullScreenWindow(final Window fullScreenWindow) {
        if (fullScreenWindow != null) {
            if (fullScreenWindow.getShape() != null) {
                fullScreenWindow.setShape(null);
            }
            if (fullScreenWindow.getOpacity() < 1.0f) {
                fullScreenWindow.setOpacity(1.0f);
            }
            if (!fullScreenWindow.isOpaque()) {
                final Color background = fullScreenWindow.getBackground();
                fullScreenWindow.setBackground(new Color(background.getRed(), background.getGreen(), background.getBlue(), 255));
            }
            final GraphicsConfiguration graphicsConfiguration = fullScreenWindow.getGraphicsConfiguration();
            if (graphicsConfiguration != null && graphicsConfiguration.getDevice() != this && graphicsConfiguration.getDevice().getFullScreenWindow() == fullScreenWindow) {
                graphicsConfiguration.getDevice().setFullScreenWindow(null);
            }
        }
        if (this.fullScreenWindow != null && this.windowedModeBounds != null) {
            if (this.windowedModeBounds.width == 0) {
                this.windowedModeBounds.width = 1;
            }
            if (this.windowedModeBounds.height == 0) {
                this.windowedModeBounds.height = 1;
            }
            this.fullScreenWindow.setBounds(this.windowedModeBounds);
        }
        synchronized (this.fsAppContextLock) {
            if (fullScreenWindow == null) {
                this.fullScreenAppContext = null;
            }
            else {
                this.fullScreenAppContext = AppContext.getAppContext();
            }
            this.fullScreenWindow = fullScreenWindow;
        }
        if (this.fullScreenWindow != null) {
            this.windowedModeBounds = this.fullScreenWindow.getBounds();
            final GraphicsConfiguration defaultConfiguration = this.getDefaultConfiguration();
            final Rectangle bounds = defaultConfiguration.getBounds();
            if (SunToolkit.isDispatchThreadForAppContext(this.fullScreenWindow)) {
                this.fullScreenWindow.setGraphicsConfiguration(defaultConfiguration);
            }
            this.fullScreenWindow.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
            this.fullScreenWindow.setVisible(true);
            this.fullScreenWindow.toFront();
        }
    }
    
    public Window getFullScreenWindow() {
        Window fullScreenWindow = null;
        synchronized (this.fsAppContextLock) {
            if (this.fullScreenAppContext == AppContext.getAppContext()) {
                fullScreenWindow = this.fullScreenWindow;
            }
        }
        return fullScreenWindow;
    }
    
    public boolean isDisplayChangeSupported() {
        return false;
    }
    
    public void setDisplayMode(final DisplayMode displayMode) {
        throw new UnsupportedOperationException("Cannot change display mode");
    }
    
    public DisplayMode getDisplayMode() {
        final GraphicsConfiguration defaultConfiguration = this.getDefaultConfiguration();
        final Rectangle bounds = defaultConfiguration.getBounds();
        return new DisplayMode(bounds.width, bounds.height, defaultConfiguration.getColorModel().getPixelSize(), 0);
    }
    
    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[] { this.getDisplayMode() };
    }
    
    public int getAvailableAcceleratedMemory() {
        return -1;
    }
    
    public boolean isWindowTranslucencySupported(final WindowTranslucency windowTranslucency) {
        switch (windowTranslucency) {
            case PERPIXEL_TRANSPARENT: {
                return isWindowShapingSupported();
            }
            case TRANSLUCENT: {
                return isWindowOpacitySupported();
            }
            case PERPIXEL_TRANSLUCENT: {
                return this.isWindowPerpixelTranslucencySupported();
            }
            default: {
                return false;
            }
        }
    }
    
    static boolean isWindowShapingSupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isWindowShapingSupported();
    }
    
    static boolean isWindowOpacitySupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isWindowOpacitySupported();
    }
    
    boolean isWindowPerpixelTranslucencySupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isWindowTranslucencySupported() && this.getTranslucencyCapableGC() != null;
    }
    
    GraphicsConfiguration getTranslucencyCapableGC() {
        final GraphicsConfiguration defaultConfiguration = this.getDefaultConfiguration();
        if (defaultConfiguration.isTranslucencyCapable()) {
            return defaultConfiguration;
        }
        final GraphicsConfiguration[] configurations = this.getConfigurations();
        for (int i = 0; i < configurations.length; ++i) {
            if (configurations[i].isTranslucencyCapable()) {
                return configurations[i];
            }
        }
        return null;
    }
    
    public enum WindowTranslucency
    {
        PERPIXEL_TRANSPARENT, 
        TRANSLUCENT, 
        PERPIXEL_TRANSLUCENT;
    }
}

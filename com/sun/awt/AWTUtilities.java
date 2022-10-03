package com.sun.awt;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import sun.awt.AWTAccessor;
import java.awt.Window;
import sun.awt.SunToolkit;
import java.awt.Toolkit;

public final class AWTUtilities
{
    private AWTUtilities() {
    }
    
    public static boolean isTranslucencySupported(final Translucency translucency) {
        switch (translucency) {
            case PERPIXEL_TRANSPARENT: {
                return isWindowShapingSupported();
            }
            case TRANSLUCENT: {
                return isWindowOpacitySupported();
            }
            case PERPIXEL_TRANSLUCENT: {
                return isWindowTranslucencySupported();
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isWindowOpacitySupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isWindowOpacitySupported();
    }
    
    public static void setWindowOpacity(final Window window, final float n) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        AWTAccessor.getWindowAccessor().setOpacity(window, n);
    }
    
    public static float getWindowOpacity(final Window window) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        return AWTAccessor.getWindowAccessor().getOpacity(window);
    }
    
    public static boolean isWindowShapingSupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isWindowShapingSupported();
    }
    
    public static Shape getWindowShape(final Window window) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        return AWTAccessor.getWindowAccessor().getShape(window);
    }
    
    public static void setWindowShape(final Window window, final Shape shape) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        AWTAccessor.getWindowAccessor().setShape(window, shape);
    }
    
    private static boolean isWindowTranslucencySupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (!(defaultToolkit instanceof SunToolkit)) {
            return false;
        }
        if (!((SunToolkit)defaultToolkit).isWindowTranslucencySupported()) {
            return false;
        }
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (isTranslucencyCapable(localGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration())) {
            return true;
        }
        final GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
        for (int i = 0; i < screenDevices.length; ++i) {
            final GraphicsConfiguration[] configurations = screenDevices[i].getConfigurations();
            for (int j = 0; j < configurations.length; ++j) {
                if (isTranslucencyCapable(configurations[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void setWindowOpaque(final Window window, final boolean b) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        if (!b && !isTranslucencySupported(Translucency.PERPIXEL_TRANSLUCENT)) {
            throw new UnsupportedOperationException("The PERPIXEL_TRANSLUCENT translucency kind is not supported");
        }
        AWTAccessor.getWindowAccessor().setOpaque(window, b);
    }
    
    public static boolean isWindowOpaque(final Window window) {
        if (window == null) {
            throw new NullPointerException("The window argument should not be null.");
        }
        return window.isOpaque();
    }
    
    public static boolean isTranslucencyCapable(final GraphicsConfiguration graphicsConfiguration) {
        if (graphicsConfiguration == null) {
            throw new NullPointerException("The gc argument should not be null");
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isTranslucencyCapable(graphicsConfiguration);
    }
    
    public static void setComponentMixingCutoutShape(final Component component, final Shape shape) {
        if (component == null) {
            throw new NullPointerException("The component argument should not be null.");
        }
        AWTAccessor.getComponentAccessor().setMixingCutoutShape(component, shape);
    }
    
    public enum Translucency
    {
        PERPIXEL_TRANSPARENT, 
        TRANSLUCENT, 
        PERPIXEL_TRANSLUCENT;
    }
}

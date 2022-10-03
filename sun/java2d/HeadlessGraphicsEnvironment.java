package sun.java2d;

import java.util.Locale;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.HeadlessException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class HeadlessGraphicsEnvironment extends GraphicsEnvironment
{
    private GraphicsEnvironment ge;
    
    public HeadlessGraphicsEnvironment(final GraphicsEnvironment ge) {
        this.ge = ge;
    }
    
    @Override
    public GraphicsDevice[] getScreenDevices() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public GraphicsDevice getDefaultScreenDevice() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Point getCenterPoint() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Rectangle getMaximumWindowBounds() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Graphics2D createGraphics(final BufferedImage bufferedImage) {
        return this.ge.createGraphics(bufferedImage);
    }
    
    @Override
    public Font[] getAllFonts() {
        return this.ge.getAllFonts();
    }
    
    @Override
    public String[] getAvailableFontFamilyNames() {
        return this.ge.getAvailableFontFamilyNames();
    }
    
    @Override
    public String[] getAvailableFontFamilyNames(final Locale locale) {
        return this.ge.getAvailableFontFamilyNames(locale);
    }
    
    public GraphicsEnvironment getSunGraphicsEnvironment() {
        return this.ge;
    }
}

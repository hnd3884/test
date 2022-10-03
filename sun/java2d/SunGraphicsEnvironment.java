package sun.java2d;

import java.awt.peer.ComponentPeer;
import java.awt.Insets;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.util.TreeMap;
import java.util.Locale;
import java.util.Arrays;
import sun.font.FontManagerFactory;
import sun.font.FontManagerForSGE;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AWTError;
import java.security.AccessController;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.security.PrivilegedAction;
import sun.awt.SunDisplayChanger;
import java.awt.GraphicsDevice;
import java.awt.Font;
import sun.awt.DisplayChangedListener;
import java.awt.GraphicsEnvironment;

public abstract class SunGraphicsEnvironment extends GraphicsEnvironment implements DisplayChangedListener
{
    public static boolean isOpenSolaris;
    private static Font defaultFont;
    protected GraphicsDevice[] screens;
    protected SunDisplayChanger displayChanger;
    
    public SunGraphicsEnvironment() {
        this.displayChanger = new SunDisplayChanger();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final String property = System.getProperty("os.version", "0.0");
                try {
                    if (Float.parseFloat(property) > 5.1f) {
                        final FileInputStream fileInputStream = new FileInputStream(new File("/etc/release"));
                        if (new BufferedReader(new InputStreamReader(fileInputStream, "ISO-8859-1")).readLine().indexOf("OpenSolaris") >= 0) {
                            SunGraphicsEnvironment.isOpenSolaris = true;
                        }
                        else {
                            SunGraphicsEnvironment.isOpenSolaris = !new File("/usr/openwin/lib/X11/fonts/TrueType/CourierNew.ttf").exists();
                        }
                        fileInputStream.close();
                    }
                }
                catch (final Exception ex) {}
                SunGraphicsEnvironment.defaultFont = new Font("Dialog", 0, 12);
                return null;
            }
        });
    }
    
    @Override
    public synchronized GraphicsDevice[] getScreenDevices() {
        GraphicsDevice[] screens = this.screens;
        if (screens == null) {
            final int numScreens = this.getNumScreens();
            screens = new GraphicsDevice[numScreens];
            for (int i = 0; i < numScreens; ++i) {
                screens[i] = this.makeScreenDevice(i);
            }
            this.screens = screens;
        }
        return screens;
    }
    
    protected abstract int getNumScreens();
    
    protected abstract GraphicsDevice makeScreenDevice(final int p0);
    
    @Override
    public GraphicsDevice getDefaultScreenDevice() {
        final GraphicsDevice[] screenDevices = this.getScreenDevices();
        if (screenDevices.length == 0) {
            throw new AWTError("no screen devices");
        }
        return screenDevices[0];
    }
    
    @Override
    public Graphics2D createGraphics(final BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            throw new NullPointerException("BufferedImage cannot be null");
        }
        return new SunGraphics2D(SurfaceData.getPrimarySurfaceData(bufferedImage), Color.white, Color.black, SunGraphicsEnvironment.defaultFont);
    }
    
    public static FontManagerForSGE getFontManagerForSGE() {
        return (FontManagerForSGE)FontManagerFactory.getInstance();
    }
    
    public static void useAlternateFontforJALocales() {
        getFontManagerForSGE().useAlternateFontforJALocales();
    }
    
    @Override
    public Font[] getAllFonts() {
        final FontManagerForSGE fontManagerForSGE = getFontManagerForSGE();
        final Font[] allInstalledFonts = fontManagerForSGE.getAllInstalledFonts();
        final Font[] createdFonts = fontManagerForSGE.getCreatedFonts();
        if (createdFonts == null || createdFonts.length == 0) {
            return allInstalledFonts;
        }
        final Font[] array = Arrays.copyOf(allInstalledFonts, allInstalledFonts.length + createdFonts.length);
        System.arraycopy(createdFonts, 0, array, allInstalledFonts.length, createdFonts.length);
        return array;
    }
    
    @Override
    public String[] getAvailableFontFamilyNames(final Locale locale) {
        final FontManagerForSGE fontManagerForSGE = getFontManagerForSGE();
        final String[] installedFontFamilyNames = fontManagerForSGE.getInstalledFontFamilyNames(locale);
        final TreeMap<String, String> createdFontFamilyNames = fontManagerForSGE.getCreatedFontFamilyNames();
        if (createdFontFamilyNames == null || createdFontFamilyNames.size() == 0) {
            return installedFontFamilyNames;
        }
        for (int i = 0; i < installedFontFamilyNames.length; ++i) {
            createdFontFamilyNames.put(installedFontFamilyNames[i].toLowerCase(locale), installedFontFamilyNames[i]);
        }
        final String[] array = new String[createdFontFamilyNames.size()];
        final Object[] array2 = createdFontFamilyNames.keySet().toArray();
        for (int j = 0; j < array2.length; ++j) {
            array[j] = (String)createdFontFamilyNames.get(array2[j]);
        }
        return array;
    }
    
    @Override
    public String[] getAvailableFontFamilyNames() {
        return this.getAvailableFontFamilyNames(Locale.getDefault());
    }
    
    public static Rectangle getUsableBounds(final GraphicsDevice graphicsDevice) {
        final GraphicsConfiguration defaultConfiguration = graphicsDevice.getDefaultConfiguration();
        final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(defaultConfiguration);
        final Rectangle bounds;
        final Rectangle rectangle = bounds = defaultConfiguration.getBounds();
        bounds.x += screenInsets.left;
        final Rectangle rectangle2 = rectangle;
        rectangle2.y += screenInsets.top;
        final Rectangle rectangle3 = rectangle;
        rectangle3.width -= screenInsets.left + screenInsets.right;
        final Rectangle rectangle4 = rectangle;
        rectangle4.height -= screenInsets.top + screenInsets.bottom;
        return rectangle;
    }
    
    @Override
    public void displayChanged() {
        for (final GraphicsDevice graphicsDevice : this.getScreenDevices()) {
            if (graphicsDevice instanceof DisplayChangedListener) {
                ((DisplayChangedListener)graphicsDevice).displayChanged();
            }
        }
        this.displayChanger.notifyListeners();
    }
    
    @Override
    public void paletteChanged() {
        this.displayChanger.notifyPaletteChanged();
    }
    
    public abstract boolean isDisplayLocal();
    
    public void addDisplayChangedListener(final DisplayChangedListener displayChangedListener) {
        this.displayChanger.add(displayChangedListener);
    }
    
    public void removeDisplayChangedListener(final DisplayChangedListener displayChangedListener) {
        this.displayChanger.remove(displayChangedListener);
    }
    
    public boolean isFlipStrategyPreferred(final ComponentPeer componentPeer) {
        return false;
    }
}

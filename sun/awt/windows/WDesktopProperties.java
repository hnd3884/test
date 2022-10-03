package sun.awt.windows;

import java.awt.RenderingHints;
import sun.awt.SunToolkit;
import java.util.Map;
import java.awt.Font;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import sun.util.logging.PlatformLogger;

final class WDesktopProperties
{
    private static final PlatformLogger log;
    private static final String PREFIX = "win.";
    private static final String FILE_PREFIX = "awt.file.";
    private static final String PROP_NAMES = "win.propNames";
    private long pData;
    private WToolkit wToolkit;
    private HashMap<String, Object> map;
    static HashMap<String, String> fontNameMap;
    
    private static native void initIDs();
    
    static boolean isWindowsProperty(final String s) {
        return s.startsWith("win.") || s.startsWith("awt.file.") || s.equals("awt.font.desktophints");
    }
    
    WDesktopProperties(final WToolkit wToolkit) {
        this.map = new HashMap<String, Object>();
        this.wToolkit = wToolkit;
        this.init();
    }
    
    private native void init();
    
    private String[] getKeyNames() {
        final Object[] array = this.map.keySet().toArray();
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].toString();
        }
        Arrays.sort(array2);
        return array2;
    }
    
    private native void getWindowsParameters();
    
    private synchronized void setBooleanProperty(final String s, final boolean b) {
        assert s != null;
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(s + "=" + String.valueOf(b));
        }
        this.map.put(s, b);
    }
    
    private synchronized void setIntegerProperty(final String s, final int n) {
        assert s != null;
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(s + "=" + String.valueOf(n));
        }
        this.map.put(s, n);
    }
    
    private synchronized void setStringProperty(final String s, final String s2) {
        assert s != null;
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(s + "=" + s2);
        }
        this.map.put(s, s2);
    }
    
    private synchronized void setColorProperty(final String s, final int n, final int n2, final int n3) {
        assert s != null && n <= 255 && n2 <= 255 && n3 <= 255;
        final Color color = new Color(n, n2, n3);
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(s + "=" + color);
        }
        this.map.put(s, color);
    }
    
    private synchronized void setFontProperty(final String s, String s2, final int n, final int n2) {
        assert s != null && n <= 3 && n2 >= 0;
        final String s3 = WDesktopProperties.fontNameMap.get(s2);
        if (s3 != null) {
            s2 = s3;
        }
        final Font font = new Font(s2, n, n2);
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(s + "=" + font);
        }
        this.map.put(s, font);
        final String string = s + ".height";
        final Integer value = n2;
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(string + "=" + value);
        }
        this.map.put(string, value);
    }
    
    private synchronized void setSoundProperty(final String s, final String s2) {
        assert s != null && s2 != null;
        final WinPlaySound winPlaySound = new WinPlaySound(s2);
        if (WDesktopProperties.log.isLoggable(PlatformLogger.Level.FINE)) {
            WDesktopProperties.log.fine(s + "=" + winPlaySound);
        }
        this.map.put(s, winPlaySound);
    }
    
    private native void playWindowsSound(final String p0);
    
    synchronized Map<String, Object> getProperties() {
        ThemeReader.flush();
        this.map = new HashMap<String, Object>();
        this.getWindowsParameters();
        this.map.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
        this.map.put("win.propNames", this.getKeyNames());
        this.map.put("DnD.Autoscroll.cursorHysteresis", this.map.get("win.drag.x"));
        return (Map)this.map.clone();
    }
    
    synchronized RenderingHints getDesktopAAHints() {
        Object o = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        Object o2 = null;
        final Boolean b = this.map.get("win.text.fontSmoothingOn");
        if (b != null && b.equals(Boolean.TRUE)) {
            final Integer n = this.map.get("win.text.fontSmoothingType");
            if (n == null || n <= 1 || n > 2) {
                o = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
            }
            else {
                final Integer n2 = this.map.get("win.text.fontSmoothingOrientation");
                if (n2 == null || n2 != 0) {
                    o = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
                }
                else {
                    o = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
                }
                final Integer n3 = this.map.get("win.text.fontSmoothingContrast");
                if (n3 == null) {
                    o2 = 140;
                }
                else {
                    o2 = n3 / 10;
                }
            }
        }
        final RenderingHints renderingHints = new RenderingHints(null);
        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, o);
        if (o2 != null) {
            renderingHints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, o2);
        }
        return renderingHints;
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.windows.WDesktopProperties");
        initIDs();
        (WDesktopProperties.fontNameMap = new HashMap<String, String>()).put("Courier", "Monospaced");
        WDesktopProperties.fontNameMap.put("MS Serif", "Microsoft Serif");
        WDesktopProperties.fontNameMap.put("MS Sans Serif", "Microsoft Sans Serif");
        WDesktopProperties.fontNameMap.put("Terminal", "Dialog");
        WDesktopProperties.fontNameMap.put("FixedSys", "Monospaced");
        WDesktopProperties.fontNameMap.put("System", "Dialog");
    }
    
    class WinPlaySound implements Runnable
    {
        String winEventName;
        
        WinPlaySound(final String winEventName) {
            this.winEventName = winEventName;
        }
        
        @Override
        public void run() {
            WDesktopProperties.this.playWindowsSound(this.winEventName);
        }
        
        @Override
        public String toString() {
            return "WinPlaySound(" + this.winEventName + ")";
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            try {
                return this.winEventName.equals(((WinPlaySound)o).winEventName);
            }
            catch (final Exception ex) {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return this.winEventName.hashCode();
        }
    }
}

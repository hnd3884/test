package javax.swing.plaf.metal;

import javax.swing.UIDefaults;
import java.awt.Font;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import javax.swing.plaf.FontUIResource;
import javax.swing.UIManager;
import sun.swing.SwingUtilities2;
import sun.awt.AppContext;
import javax.swing.plaf.ColorUIResource;

public class DefaultMetalTheme extends MetalTheme
{
    private static final boolean PLAIN_FONTS;
    private static final String[] fontNames;
    private static final int[] fontStyles;
    private static final int[] fontSizes;
    private static final String[] defaultNames;
    private static final ColorUIResource primary1;
    private static final ColorUIResource primary2;
    private static final ColorUIResource primary3;
    private static final ColorUIResource secondary1;
    private static final ColorUIResource secondary2;
    private static final ColorUIResource secondary3;
    private FontDelegate fontDelegate;
    
    static String getDefaultFontName(final int n) {
        return DefaultMetalTheme.fontNames[n];
    }
    
    static int getDefaultFontSize(final int n) {
        return DefaultMetalTheme.fontSizes[n];
    }
    
    static int getDefaultFontStyle(final int n) {
        if (n != 4) {
            Object value = null;
            if (AppContext.getAppContext().get(SwingUtilities2.LAF_STATE_KEY) != null) {
                value = UIManager.get("swing.boldMetal");
            }
            if (value != null) {
                if (Boolean.FALSE.equals(value)) {
                    return 0;
                }
            }
            else if (DefaultMetalTheme.PLAIN_FONTS) {
                return 0;
            }
        }
        return DefaultMetalTheme.fontStyles[n];
    }
    
    static String getDefaultPropertyName(final int n) {
        return DefaultMetalTheme.defaultNames[n];
    }
    
    @Override
    public String getName() {
        return "Steel";
    }
    
    public DefaultMetalTheme() {
        this.install();
    }
    
    @Override
    protected ColorUIResource getPrimary1() {
        return DefaultMetalTheme.primary1;
    }
    
    @Override
    protected ColorUIResource getPrimary2() {
        return DefaultMetalTheme.primary2;
    }
    
    @Override
    protected ColorUIResource getPrimary3() {
        return DefaultMetalTheme.primary3;
    }
    
    @Override
    protected ColorUIResource getSecondary1() {
        return DefaultMetalTheme.secondary1;
    }
    
    @Override
    protected ColorUIResource getSecondary2() {
        return DefaultMetalTheme.secondary2;
    }
    
    @Override
    protected ColorUIResource getSecondary3() {
        return DefaultMetalTheme.secondary3;
    }
    
    @Override
    public FontUIResource getControlTextFont() {
        return this.getFont(0);
    }
    
    @Override
    public FontUIResource getSystemTextFont() {
        return this.getFont(1);
    }
    
    @Override
    public FontUIResource getUserTextFont() {
        return this.getFont(2);
    }
    
    @Override
    public FontUIResource getMenuTextFont() {
        return this.getFont(3);
    }
    
    @Override
    public FontUIResource getWindowTitleFont() {
        return this.getFont(4);
    }
    
    @Override
    public FontUIResource getSubTextFont() {
        return this.getFont(5);
    }
    
    private FontUIResource getFont(final int n) {
        return this.fontDelegate.getFont(n);
    }
    
    @Override
    void install() {
        if (MetalLookAndFeel.isWindows() && MetalLookAndFeel.useSystemFonts()) {
            this.fontDelegate = new WindowsFontDelegate();
        }
        else {
            this.fontDelegate = new FontDelegate();
        }
    }
    
    @Override
    boolean isSystemTheme() {
        return this.getClass() == DefaultMetalTheme.class;
    }
    
    static {
        fontNames = new String[] { "Dialog", "Dialog", "Dialog", "Dialog", "Dialog", "Dialog" };
        fontStyles = new int[] { 1, 0, 0, 1, 1, 0 };
        fontSizes = new int[] { 12, 12, 12, 12, 12, 10 };
        defaultNames = new String[] { "swing.plaf.metal.controlFont", "swing.plaf.metal.systemFont", "swing.plaf.metal.userFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.smallFont" };
        final Object doPrivileged = AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.boldMetal"));
        if (doPrivileged == null || !"false".equals(doPrivileged)) {
            PLAIN_FONTS = false;
        }
        else {
            PLAIN_FONTS = true;
        }
        primary1 = new ColorUIResource(102, 102, 153);
        primary2 = new ColorUIResource(153, 153, 204);
        primary3 = new ColorUIResource(204, 204, 255);
        secondary1 = new ColorUIResource(102, 102, 102);
        secondary2 = new ColorUIResource(153, 153, 153);
        secondary3 = new ColorUIResource(204, 204, 204);
    }
    
    private static class FontDelegate
    {
        private static int[] defaultMapping;
        FontUIResource[] fonts;
        
        public FontDelegate() {
            this.fonts = new FontUIResource[6];
        }
        
        public FontUIResource getFont(final int n) {
            final int n2 = FontDelegate.defaultMapping[n];
            if (this.fonts[n] == null) {
                Font privilegedFont = this.getPrivilegedFont(n2);
                if (privilegedFont == null) {
                    privilegedFont = new Font(DefaultMetalTheme.getDefaultFontName(n), DefaultMetalTheme.getDefaultFontStyle(n), DefaultMetalTheme.getDefaultFontSize(n));
                }
                this.fonts[n] = new FontUIResource(privilegedFont);
            }
            return this.fonts[n];
        }
        
        protected Font getPrivilegedFont(final int n) {
            return AccessController.doPrivileged((PrivilegedAction<Font>)new PrivilegedAction<Font>() {
                @Override
                public Font run() {
                    return Font.getFont(DefaultMetalTheme.getDefaultPropertyName(n));
                }
            });
        }
        
        static {
            FontDelegate.defaultMapping = new int[] { 0, 1, 2, 0, 0, 5 };
        }
    }
    
    private static class WindowsFontDelegate extends FontDelegate
    {
        private MetalFontDesktopProperty[] props;
        private boolean[] checkedPriviledged;
        
        public WindowsFontDelegate() {
            this.props = new MetalFontDesktopProperty[6];
            this.checkedPriviledged = new boolean[6];
        }
        
        @Override
        public FontUIResource getFont(final int n) {
            if (this.fonts[n] != null) {
                return this.fonts[n];
            }
            if (!this.checkedPriviledged[n]) {
                final Font privilegedFont = this.getPrivilegedFont(n);
                this.checkedPriviledged[n] = true;
                if (privilegedFont != null) {
                    return this.fonts[n] = new FontUIResource(privilegedFont);
                }
            }
            if (this.props[n] == null) {
                this.props[n] = new MetalFontDesktopProperty(n);
            }
            return (FontUIResource)this.props[n].createValue(null);
        }
    }
}

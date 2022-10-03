package javax.swing.plaf.metal;

import java.awt.Font;
import com.sun.java.swing.plaf.windows.DesktopProperty;

class MetalFontDesktopProperty extends DesktopProperty
{
    private static final String[] propertyMapping;
    private int type;
    
    MetalFontDesktopProperty(final int n) {
        this(MetalFontDesktopProperty.propertyMapping[n], n);
    }
    
    MetalFontDesktopProperty(final String s, final int type) {
        super(s, null);
        this.type = type;
    }
    
    @Override
    protected Object configureValue(Object o) {
        if (o instanceof Integer) {
            o = new Font(DefaultMetalTheme.getDefaultFontName(this.type), DefaultMetalTheme.getDefaultFontStyle(this.type), (int)o);
        }
        return super.configureValue(o);
    }
    
    @Override
    protected Object getDefaultValue() {
        return new Font(DefaultMetalTheme.getDefaultFontName(this.type), DefaultMetalTheme.getDefaultFontStyle(this.type), DefaultMetalTheme.getDefaultFontSize(this.type));
    }
    
    static {
        propertyMapping = new String[] { "win.ansiVar.font.height", "win.tooltip.font.height", "win.ansiVar.font.height", "win.menu.font.height", "win.frame.captionFont.height", "win.menu.font.height" };
    }
}

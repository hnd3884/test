package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class FontManagerFactory
{
    private static FontManager instance;
    private static final String DEFAULT_CLASS;
    
    public static synchronized FontManager getInstance() {
        if (FontManagerFactory.instance != null) {
            return FontManagerFactory.instance;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    FontManagerFactory.instance = (FontManager)Class.forName(System.getProperty("sun.font.fontmanager", FontManagerFactory.DEFAULT_CLASS), true, ClassLoader.getSystemClassLoader()).newInstance();
                }
                catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    throw new InternalError((Throwable)ex);
                }
                return null;
            }
        });
        return FontManagerFactory.instance;
    }
    
    static {
        FontManagerFactory.instance = null;
        if (FontUtilities.isWindows) {
            DEFAULT_CLASS = "sun.awt.Win32FontManager";
        }
        else if (FontUtilities.isMacOSX) {
            DEFAULT_CLASS = "sun.font.CFontManager";
        }
        else {
            DEFAULT_CLASS = "sun.awt.X11FontManager";
        }
    }
}

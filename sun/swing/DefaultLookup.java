package sun.swing;

import javax.swing.border.Border;
import javax.swing.Icon;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import sun.awt.AppContext;

public class DefaultLookup
{
    private static final Object DEFAULT_LOOKUP_KEY;
    private static Thread currentDefaultThread;
    private static DefaultLookup currentDefaultLookup;
    private static boolean isLookupSet;
    
    public static void setDefaultLookup(DefaultLookup currentDefaultLookup) {
        synchronized (DefaultLookup.class) {
            if (!DefaultLookup.isLookupSet && currentDefaultLookup == null) {
                return;
            }
            if (currentDefaultLookup == null) {
                currentDefaultLookup = new DefaultLookup();
            }
            DefaultLookup.isLookupSet = true;
            AppContext.getAppContext().put(DefaultLookup.DEFAULT_LOOKUP_KEY, currentDefaultLookup);
            DefaultLookup.currentDefaultThread = Thread.currentThread();
            DefaultLookup.currentDefaultLookup = currentDefaultLookup;
        }
    }
    
    public static Object get(final JComponent component, final ComponentUI componentUI, final String s) {
        final boolean isLookupSet;
        synchronized (DefaultLookup.class) {
            isLookupSet = DefaultLookup.isLookupSet;
        }
        if (!isLookupSet) {
            return UIManager.get(s, component.getLocale());
        }
        final Thread currentThread = Thread.currentThread();
        Object currentDefaultLookup;
        synchronized (DefaultLookup.class) {
            if (currentThread == DefaultLookup.currentDefaultThread) {
                currentDefaultLookup = DefaultLookup.currentDefaultLookup;
            }
            else {
                currentDefaultLookup = AppContext.getAppContext().get(DefaultLookup.DEFAULT_LOOKUP_KEY);
                if (currentDefaultLookup == null) {
                    currentDefaultLookup = new DefaultLookup();
                    AppContext.getAppContext().put(DefaultLookup.DEFAULT_LOOKUP_KEY, currentDefaultLookup);
                }
                DefaultLookup.currentDefaultThread = currentThread;
                DefaultLookup.currentDefaultLookup = (DefaultLookup)currentDefaultLookup;
            }
        }
        return ((DefaultLookup)currentDefaultLookup).getDefault(component, componentUI, s);
    }
    
    public static int getInt(final JComponent component, final ComponentUI componentUI, final String s, final int n) {
        final Object value = get(component, componentUI, s);
        if (value == null || !(value instanceof Number)) {
            return n;
        }
        return ((Number)value).intValue();
    }
    
    public static int getInt(final JComponent component, final ComponentUI componentUI, final String s) {
        return getInt(component, componentUI, s, -1);
    }
    
    public static Insets getInsets(final JComponent component, final ComponentUI componentUI, final String s, final Insets insets) {
        final Object value = get(component, componentUI, s);
        if (value == null || !(value instanceof Insets)) {
            return insets;
        }
        return (Insets)value;
    }
    
    public static Insets getInsets(final JComponent component, final ComponentUI componentUI, final String s) {
        return getInsets(component, componentUI, s, null);
    }
    
    public static boolean getBoolean(final JComponent component, final ComponentUI componentUI, final String s, final boolean b) {
        final Object value = get(component, componentUI, s);
        if (value == null || !(value instanceof Boolean)) {
            return b;
        }
        return (boolean)value;
    }
    
    public static boolean getBoolean(final JComponent component, final ComponentUI componentUI, final String s) {
        return getBoolean(component, componentUI, s, false);
    }
    
    public static Color getColor(final JComponent component, final ComponentUI componentUI, final String s, final Color color) {
        final Object value = get(component, componentUI, s);
        if (value == null || !(value instanceof Color)) {
            return color;
        }
        return (Color)value;
    }
    
    public static Color getColor(final JComponent component, final ComponentUI componentUI, final String s) {
        return getColor(component, componentUI, s, null);
    }
    
    public static Icon getIcon(final JComponent component, final ComponentUI componentUI, final String s, final Icon icon) {
        final Object value = get(component, componentUI, s);
        if (value == null || !(value instanceof Icon)) {
            return icon;
        }
        return (Icon)value;
    }
    
    public static Icon getIcon(final JComponent component, final ComponentUI componentUI, final String s) {
        return getIcon(component, componentUI, s, null);
    }
    
    public static Border getBorder(final JComponent component, final ComponentUI componentUI, final String s, final Border border) {
        final Object value = get(component, componentUI, s);
        if (value == null || !(value instanceof Border)) {
            return border;
        }
        return (Border)value;
    }
    
    public static Border getBorder(final JComponent component, final ComponentUI componentUI, final String s) {
        return getBorder(component, componentUI, s, null);
    }
    
    public Object getDefault(final JComponent component, final ComponentUI componentUI, final String s) {
        return UIManager.get(s, component.getLocale());
    }
    
    static {
        DEFAULT_LOOKUP_KEY = new StringBuffer("DefaultLookup");
    }
}

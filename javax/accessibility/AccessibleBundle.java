package javax.accessibility;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Hashtable;

public abstract class AccessibleBundle
{
    private static Hashtable table;
    private final String defaultResourceBundleName = "com.sun.accessibility.internal.resources.accessibility";
    protected String key;
    
    public AccessibleBundle() {
        this.key = null;
    }
    
    protected String toDisplayString(final String s, final Locale locale) {
        this.loadResourceBundle(s, locale);
        final Hashtable value = AccessibleBundle.table.get(locale);
        if (value != null && value instanceof Hashtable) {
            final Object value2 = value.get(this.key);
            if (value2 != null && value2 instanceof String) {
                return (String)value2;
            }
        }
        return this.key;
    }
    
    public String toDisplayString(final Locale locale) {
        return this.toDisplayString("com.sun.accessibility.internal.resources.accessibility", locale);
    }
    
    public String toDisplayString() {
        return this.toDisplayString(Locale.getDefault());
    }
    
    @Override
    public String toString() {
        return this.toDisplayString();
    }
    
    private void loadResourceBundle(final String s, final Locale locale) {
        if (!AccessibleBundle.table.contains(locale)) {
            try {
                final Hashtable hashtable = new Hashtable();
                final ResourceBundle bundle = ResourceBundle.getBundle(s, locale);
                final Enumeration<String> keys = bundle.getKeys();
                while (keys.hasMoreElements()) {
                    final String s2 = keys.nextElement();
                    hashtable.put(s2, bundle.getObject(s2));
                }
                AccessibleBundle.table.put(locale, hashtable);
            }
            catch (final MissingResourceException ex) {
                System.err.println("loadResourceBundle: " + ex);
            }
        }
    }
    
    static {
        AccessibleBundle.table = new Hashtable();
    }
}

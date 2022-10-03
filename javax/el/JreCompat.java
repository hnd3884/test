package javax.el;

import java.lang.reflect.AccessibleObject;

class JreCompat
{
    private static final JreCompat instance;
    
    public static JreCompat getInstance() {
        return JreCompat.instance;
    }
    
    public boolean canAccess(final Object base, final AccessibleObject accessibleObject) {
        return true;
    }
    
    public boolean isExported(final Class<?> type) {
        return true;
    }
    
    static {
        if (Jre9Compat.isSupported()) {
            instance = new Jre9Compat();
        }
        else {
            instance = new JreCompat();
        }
    }
}

package org.apache.el.util;

import java.lang.reflect.AccessibleObject;

public class JreCompat
{
    private static final JreCompat instance;
    
    public static JreCompat getInstance() {
        return JreCompat.instance;
    }
    
    public boolean canAccess(final Object base, final AccessibleObject accessibleObject) {
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

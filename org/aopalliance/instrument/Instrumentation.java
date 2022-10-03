package org.aopalliance.instrument;

import org.aopalliance.reflect.Locator;

public interface Instrumentation
{
    public static final int ADD_INTERFACE = 0;
    public static final int SET_SUPERCLASS = 1;
    public static final int ADD_CLASS = 2;
    public static final int ADD_BEFORE_CODE = 3;
    public static final int ADD_AFTER_CODE = 4;
    public static final int ADD_METADATA = 5;
    
    Locator getLocation();
    
    int getType();
}

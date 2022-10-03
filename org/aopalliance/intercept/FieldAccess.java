package org.aopalliance.intercept;

import java.lang.reflect.Field;

public interface FieldAccess extends Joinpoint
{
    public static final int READ = 0;
    public static final int WRITE = 1;
    
    Field getField();
    
    Object getValueToSet();
    
    int getAccessType();
}

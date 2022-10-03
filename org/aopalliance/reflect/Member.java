package org.aopalliance.reflect;

public interface Member extends ProgramUnit
{
    public static final int USER_SIDE = 0;
    public static final int PROVIDER_SIDE = 1;
    
    Class getDeclaringClass();
    
    String getName();
    
    int getModifiers();
}

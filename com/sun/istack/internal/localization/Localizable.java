package com.sun.istack.internal.localization;

public interface Localizable
{
    public static final String NOT_LOCALIZABLE = "\u0000";
    
    String getKey();
    
    Object[] getArguments();
    
    String getResourceBundleName();
}

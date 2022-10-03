package org.glassfish.jersey.internal.l10n;

public interface Localizable
{
    public static final String NOT_LOCALIZABLE = "\u0000";
    
    String getKey();
    
    Object[] getArguments();
    
    String getResourceBundleName();
}

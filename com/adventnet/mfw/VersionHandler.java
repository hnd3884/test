package com.adventnet.mfw;

public interface VersionHandler
{
    String getCurrentVersion();
    
    boolean isCompatible(final String p0);
    
    long getCurrentBuildNumber();
}

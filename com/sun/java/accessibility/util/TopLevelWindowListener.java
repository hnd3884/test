package com.sun.java.accessibility.util;

import java.awt.Window;
import jdk.Exported;
import java.util.EventListener;

@Exported
public interface TopLevelWindowListener extends EventListener
{
    void topLevelWindowCreated(final Window p0);
    
    void topLevelWindowDestroyed(final Window p0);
}

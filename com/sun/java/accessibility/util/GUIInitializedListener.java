package com.sun.java.accessibility.util;

import jdk.Exported;
import java.util.EventListener;

@Exported
public interface GUIInitializedListener extends EventListener
{
    void guiInitialized();
}

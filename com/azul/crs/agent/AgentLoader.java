package com.azul.crs.agent;

import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;

class AgentLoader
{
    private static Object main() throws Exception {
        final String string = System.getProperty("java.home") + "/lib/crs-agent.jar";
        if (!new File(string).exists()) {
            return null;
        }
        final Class<?> loadClass = new URLClassLoader(new URL[] { new URL("file:///" + string) }, (ClassLoader)null).loadClass("com.azul.crs.client.Agent001");
        registerNatives(loadClass);
        return loadClass;
    }
    
    private static native void registerNatives(final Class p0);
}

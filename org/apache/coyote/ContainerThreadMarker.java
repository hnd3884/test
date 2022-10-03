package org.apache.coyote;

public class ContainerThreadMarker
{
    public static boolean isContainerThread() {
        return org.apache.tomcat.util.net.ContainerThreadMarker.isContainerThread();
    }
    
    public static void set() {
        org.apache.tomcat.util.net.ContainerThreadMarker.set();
    }
    
    public static void clear() {
        org.apache.tomcat.util.net.ContainerThreadMarker.clear();
    }
}

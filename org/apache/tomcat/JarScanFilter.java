package org.apache.tomcat;

public interface JarScanFilter
{
    boolean check(final JarScanType p0, final String p1);
}

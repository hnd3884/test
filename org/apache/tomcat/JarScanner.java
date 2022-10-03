package org.apache.tomcat;

import javax.servlet.ServletContext;

public interface JarScanner
{
    void scan(final JarScanType p0, final ServletContext p1, final JarScannerCallback p2);
    
    JarScanFilter getJarScanFilter();
    
    void setJarScanFilter(final JarScanFilter p0);
}

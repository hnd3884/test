package org.apache.tomcat;

import java.io.File;
import java.io.IOException;

public interface JarScannerCallback
{
    void scan(final Jar p0, final String p1, final boolean p2) throws IOException;
    
    void scan(final File p0, final String p1, final boolean p2) throws IOException;
    
    void scanWebInfClasses() throws IOException;
}

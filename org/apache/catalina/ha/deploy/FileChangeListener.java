package org.apache.catalina.ha.deploy;

import java.io.File;

public interface FileChangeListener
{
    void fileModified(final File p0);
    
    void fileRemoved(final File p0);
}
